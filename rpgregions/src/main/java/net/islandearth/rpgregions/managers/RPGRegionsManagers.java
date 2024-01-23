package net.islandearth.rpgregions.managers;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.integrations.IntegrationManager;
import net.islandearth.rpgregions.api.integrations.IntegrationType;
import net.islandearth.rpgregions.api.integrations.hooks.PlaceholderRegionHook;
import net.islandearth.rpgregions.command.IconCommand;
import net.islandearth.rpgregions.effects.FogEffect;
import net.islandearth.rpgregions.effects.PotionRegionEffect;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.effects.RegionEffectRegistry;
import net.islandearth.rpgregions.effects.protocol.ProtocolCreator;
import net.islandearth.rpgregions.exception.CouldNotStartException;
import net.islandearth.rpgregions.fauna.FaunaCache;
import net.islandearth.rpgregions.gui.element.BooleanGuiFieldElement;
import net.islandearth.rpgregions.gui.element.CompareTypeGuiFieldElement;
import net.islandearth.rpgregions.gui.element.GuiFieldElementRegistry;
import net.islandearth.rpgregions.gui.element.IGuiFieldElementRegistry;
import net.islandearth.rpgregions.gui.element.IntegerGuiFieldElement;
import net.islandearth.rpgregions.gui.element.ItemStackGuiFieldElement;
import net.islandearth.rpgregions.gui.element.ListGuiFieldElement;
import net.islandearth.rpgregions.gui.element.LocationGuiFieldElement;
import net.islandearth.rpgregions.gui.element.PotionEffectGuiFieldElement;
import net.islandearth.rpgregions.managers.data.IRPGRegionsCache;
import net.islandearth.rpgregions.managers.data.IStorageManager;
import net.islandearth.rpgregions.managers.data.RPGRegionsCache;
import net.islandearth.rpgregions.managers.data.StorageType;
import net.islandearth.rpgregions.managers.data.fauna.IFaunaCache;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.regeneration.RegenerationManager;
import net.islandearth.rpgregions.managers.registry.IRPGRegionsRegistry;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import net.islandearth.rpgregions.regenerate.Regenerate;
import net.islandearth.rpgregions.regenerate.entity.RegeneratingEntity;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirementRegistry;
import net.islandearth.rpgregions.rewards.ConsoleCommandReward;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.rewards.ExperienceReward;
import net.islandearth.rpgregions.rewards.ItemReward;
import net.islandearth.rpgregions.rewards.MessageReward;
import net.islandearth.rpgregions.rewards.PlayerCommandReward;
import net.islandearth.rpgregions.rewards.RegionRewardRegistry;
import net.islandearth.rpgregions.thread.Blocking;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Stream;

public class RPGRegionsManagers implements IRPGRegionsManagers {

    private IStorageManager storageManager;
    private final IntegrationManager integrationManager;
    private final IFaunaCache faunaCache;
    private final IRPGRegionsCache regionsCache;
    private final IRegenerationManager regenerationManager;
    private final Map<Class<? extends RPGRegionsRegistry<?>>, RPGRegionsRegistry<?>> registry;
    private final IGuiFieldElementRegistry guiFieldElementRegistry;

    public RPGRegionsManagers(RPGRegions plugin) throws ReflectiveOperationException, CouldNotStartException, IOException {
        StorageType.valueOf(plugin.getConfig().getString("settings.storage.mode").toUpperCase(Locale.ROOT))
                .get()
                .ifPresent(storageManager1 -> storageManager = storageManager1);
        if (storageManager == null) throw new CouldNotStartException("Could not find StorageManager!");

        Optional<IntegrationManager> integrationManager = IntegrationType.valueOf(plugin.getConfig().getString("settings.integration.name").toUpperCase(Locale.ROOT))
                .get(plugin);
        if (integrationManager.isPresent()) {
            this.integrationManager = integrationManager.get();
        } else {
            throw new CouldNotStartException("Unable to load IntegrationManager. The requested plugin is not enabled.");
        }

        this.faunaCache = new FaunaCache(plugin);
        this.regionsCache = new RPGRegionsCache(plugin);
        this.registry = new ConcurrentHashMap<>();
        registry.put(RegionRequirementRegistry.class, new RegionRequirementRegistry());
        registry.put(RegionRewardRegistry.class, new RegionRewardRegistry());
        registry.put(RegionEffectRegistry.class, new RegionEffectRegistry());

        File folder = new File(plugin.getDataFolder() + File.separator + "regions");
        if (!folder.exists()) folder.mkdirs();

        File templates = new File(plugin.getDataFolder() + File.separator + "templates");
        if (!templates.exists()) templates.mkdirs();

        // Generate an example config
        List<DiscoveryReward> rewards = new ArrayList<>();
        rewards.add(new ExperienceReward(plugin, 10));
        rewards.add(new ItemReward(plugin, new ItemStack(Material.IRON_BARS)));
        rewards.add(new PlayerCommandReward(plugin, "say I discovered a region!"));
        rewards.add(new ConsoleCommandReward(plugin, "say Server sees you discovered a region!"));
        rewards.add(new MessageReward(plugin, Collections.singletonList("&aExample message as a reward")));
        List<RegionEffect> effects = new ArrayList<>();
        effects.add(new PotionRegionEffect(plugin,
                new PotionEffect(PotionEffectType.GLOWING, 100, 1, true, true, true),
                true,
                Collections.singletonList(new ItemStackBuilder(Material.IRON_CHESTPLATE).build())));

        final ConfiguredRegion configuredRegion = createExampleRegion(rewards, effects);
        configuredRegion.getIconCommand().add(new IconCommand("say", IconCommand.CommandClickType.DISCOVERED, false, 0));
        configuredRegion.save(plugin);

        Stream<Path> files = Files.walk(Paths.get(folder.getPath()));
        files.filter(Files::isRegularFile)
            .toList()
            .forEach(path -> {
                File file = path.toFile();
                plugin.debug("Walking file tree: " + file);
                if (regionsCache.getConfiguredRegions().containsKey(file.getName().replace(".json", ""))) {
                    plugin.getLogger().severe("Duplicate region files have been found for " + file.getName() + ". " +
                            "In order to protect your data, the plugin will NOT load the duplicate region config " +
                            "(which is " + file + ").");
                    return;
                }
                // Exclude non-json files
                if (file.getName().endsWith(".json")) {
                    try (Reader reader = new FileReader(file)) {
                        ConfiguredRegion region = plugin.getGson().fromJson(reader, ConfiguredRegion.class);
                        if (!region.getId().equals("exampleconfig")) regionsCache.addConfiguredRegion(region);
                        warnBlocking(plugin, region);
                        if (region.getEffects() != null)
                            for (RegionEffect effect : region.getEffects()) {
                                if (effect instanceof FogEffect fogEffect) fogEffect.generateBiomes();
                            }
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.SEVERE, "Error loading region config " + file.getName() + ".", e);
                    }
                }
            });
        files.close();

        this.regenerationManager = new RegenerationManager(plugin);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderRegionHook(plugin).register();
        }

        if (Bukkit.getPluginManager().getPlugin("Plan") != null) {
            new PlanRegistryManager(plugin);
        }

        this.guiFieldElementRegistry = new GuiFieldElementRegistry();
        guiFieldElementRegistry.register(new BooleanGuiFieldElement());
        guiFieldElementRegistry.register(new IntegerGuiFieldElement());
        guiFieldElementRegistry.register(new ItemStackGuiFieldElement());
        guiFieldElementRegistry.register(new LocationGuiFieldElement());
        guiFieldElementRegistry.register(new PotionEffectGuiFieldElement());
        guiFieldElementRegistry.register(new CompareTypeGuiFieldElement());
        guiFieldElementRegistry.register(new ListGuiFieldElement());

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getBukkitVersion().contains("1.17")) {
            plugin.getLogger().info("Detected ProtcolLib, enabling fog support!");
            new ProtocolCreator(plugin, this);
        }
    }

    @NotNull
    private static ConfiguredRegion createExampleRegion(List<DiscoveryReward> rewards, List<RegionEffect> effects) {
        ConfiguredRegion configuredRegion = new ConfiguredRegion(null, "exampleconfig", "ExampleConfig", rewards, effects,
                Sound.AMBIENT_UNDERWATER_EXIT,
                Material.WOODEN_AXE);
        configuredRegion.setRegenerate(new Regenerate(50000,
                false,
                Collections.singletonList(
                        new RegeneratingEntity(EntityType.SHULKER, Arrays.asList(
                                Material.PURPUR_BLOCK,
                                Material.PURPUR_PILLAR), 5, 30))));
        return configuredRegion;
    }

    private void warnBlocking(RPGRegions plugin, ConfiguredRegion region) {
        List<Blocking> blocking = new ArrayList<>();
        for (DiscoveryReward reward : region.getRewards()) {
            if (reward instanceof Blocking) {
                blocking.add((Blocking) reward);
            }
        }
        for (RegionRequirement requirement : region.getRequirements()) {
            if (requirement instanceof Blocking) {
                blocking.add((Blocking) requirement);
            }
        }
        for (RegionEffect effect : region.getEffects()) {
            if (effect instanceof Blocking) {
                blocking.add((Blocking) effect);
            }
        }
        for (Blocking blockingClass : blocking) {
            blockingClass.log(plugin);
        }
    }

    @Override
    public IStorageManager getStorageManager() {
        return storageManager;
    }

    @Override
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    @Override
    public IFaunaCache getFaunaCache() {
        return faunaCache;
    }

    @Override
    public IRPGRegionsCache getRegionsCache() {
        return regionsCache;
    }

    @Override
    public IRegenerationManager getRegenerationManager() {
        return regenerationManager;
    }

    @Override
    public IGuiFieldElementRegistry getGuiFieldElementRegistry() {
        return guiFieldElementRegistry;
    }

    public Map<Class<? extends RPGRegionsRegistry<?>>, RPGRegionsRegistry<?>> getRegistry() {
        return registry;
    }

    @Nullable
    @Override
    public <T> IRPGRegionsRegistry<T> getRegistry(Class<? extends IRPGRegionsRegistry<T>> clazz) {
        return (IRPGRegionsRegistry<T>) registry.get(clazz);
    }

    public Collection<RPGRegionsRegistry<?>> getRegistries() {
        return registry.values();
    }
}
