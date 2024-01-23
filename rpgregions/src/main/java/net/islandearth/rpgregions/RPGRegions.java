package net.islandearth.rpgregions;

import co.aikar.idb.DB;
import com.convallyria.languagy.api.adventure.AdventurePlatform;
import com.convallyria.languagy.api.language.Language;
import com.convallyria.languagy.api.language.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.integrations.rpgregions.RPGRegionsIntegration;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.RPGRegionsRegion;
import net.islandearth.rpgregions.api.schedule.PlatformScheduler;
import net.islandearth.rpgregions.commands.Commands;
import net.islandearth.rpgregions.effects.FogEffect;
import net.islandearth.rpgregions.effects.PotionRegionEffect;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.effects.RegionEffectRegistry;
import net.islandearth.rpgregions.effects.VanishEffect;
import net.islandearth.rpgregions.exception.CouldNotStartException;
import net.islandearth.rpgregions.fauna.FaunaDiscoverer;
import net.islandearth.rpgregions.fauna.MythicFaunaDiscoverer;
import net.islandearth.rpgregions.folia.schedule.FoliaScheduler;
import net.islandearth.rpgregions.gson.AbstractAdapter;
import net.islandearth.rpgregions.gson.ItemStackAdapter;
import net.islandearth.rpgregions.gson.LocationAdapter;
import net.islandearth.rpgregions.gson.PotionEffectAdapter;
import net.islandearth.rpgregions.listener.ConnectionListener;
import net.islandearth.rpgregions.listener.FaunaListener;
import net.islandearth.rpgregions.listener.MoveListener;
import net.islandearth.rpgregions.listener.RegionListener;
import net.islandearth.rpgregions.listener.ServerReloadListener;
import net.islandearth.rpgregions.listener.external.CustomStructuresListener;
import net.islandearth.rpgregions.managers.RPGRegionsManagers;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.registry.IRPGRegionsRegistry;
import net.islandearth.rpgregions.requirements.AlonsoLevelRequirement;
import net.islandearth.rpgregions.requirements.DependencyRequirement;
import net.islandearth.rpgregions.requirements.ItemRequirement;
import net.islandearth.rpgregions.requirements.LevelRequirement;
import net.islandearth.rpgregions.requirements.MMOCoreLevelRequirement;
import net.islandearth.rpgregions.requirements.MoneyRequirement;
import net.islandearth.rpgregions.requirements.PermissionRequirement;
import net.islandearth.rpgregions.requirements.PlaceholderRequirement;
import net.islandearth.rpgregions.requirements.QuestRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirementRegistry;
import net.islandearth.rpgregions.rewards.AlonsoLevelReward;
import net.islandearth.rpgregions.rewards.ConsoleCommandReward;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.rewards.ExperienceReward;
import net.islandearth.rpgregions.rewards.ItemReward;
import net.islandearth.rpgregions.rewards.MMOCoreLevelReward;
import net.islandearth.rpgregions.rewards.MessageReward;
import net.islandearth.rpgregions.rewards.MoneyReward;
import net.islandearth.rpgregions.rewards.PlaceholderConsoleCommandReward;
import net.islandearth.rpgregions.rewards.PlayerCommandReward;
import net.islandearth.rpgregions.rewards.QuestReward;
import net.islandearth.rpgregions.rewards.RegionDiscoverReward;
import net.islandearth.rpgregions.rewards.RegionRewardRegistry;
import net.islandearth.rpgregions.rewards.TeleportReward;
import net.islandearth.rpgregions.schedule.BukkitScheduler;
import net.islandearth.rpgregions.tasks.DynmapTask;
import net.islandearth.rpgregions.translation.Translations;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class RPGRegions extends JavaPlugin implements IRPGRegionsAPI {

    private BukkitAudiences adventure;
    private MiniMessage miniMessage;
    private PlatformScheduler<? extends IRPGRegionsAPI> scheduler;
    private RPGRegionsManagers managers;
    private Economy ecoProvider;

    @Override
    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public MiniMessage miniMessage() {
        return miniMessage;
    }

    @Override
    public Translator getTranslator() {
        return translator;
    }

    private Translator translator;

    private boolean firstTimeSetup;

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.miniMessage();
        this.scheduler = FoliaScheduler.RUNNING_FOLIA ? new FoliaScheduler(this) : new BukkitScheduler(this);
        RPGRegionsAPI.setAPI(this);
        this.createConfig();
        this.generateLang();
        try {
            this.managers = new RPGRegionsManagers(this);
        } catch (ReflectiveOperationException | IOException | CouldNotStartException e) {
            getLogger().log(Level.SEVERE, "Error starting managers, please report this!", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.registerCommands();
        this.registerRewards();
        this.registerRequirements();
        this.registerEffects();
        this.registerListeners();
        this.translator = Translator.of(this, "lang", Language.BRITISH_ENGLISH, debug(), AdventurePlatform.create(miniMessage, adventure));
        this.registerTasks();
        this.registerMetrics();
        this.setupEconomy();

        // Tell integration to enable
        if (managers.getIntegrationManager() instanceof RPGRegionsIntegration rpgRegionsIntegration) {
            rpgRegionsIntegration.onEnable();
        }
    }

    @Override
    public void onDisable() {
        if (translator != null) translator.close();

        if (managers == null || managers.getRegionsCache() == null || managers.getStorageManager() == null) {
            getLogger().warning("Unable to save data as managers were null");
        } else {
            // Save all player data (quit event not called for shutdown)
            Bukkit.getOnlinePlayers().forEach(player -> {
                final @Nullable CompletableFuture<RPGRegionsAccount> account = managers.getStorageManager().getCachedAccounts().getIfPresent(player.getUniqueId());
                if (account == null) return;
                this.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId()).join();
            });

            // Tell integration to save
            if (managers.getIntegrationManager() instanceof RPGRegionsIntegration rpgRegionsIntegration) {
                rpgRegionsIntegration.onDisable();
            }

            // Save all region configs
            managers.getRegionsCache().getConfiguredRegions().forEach((id, region) -> region.save(this));
        }

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        RPGRegionsAPI.setAPI(null);
        DB.close();
    }

    @Override
    public RPGRegionsManagers getManagers() {
        return managers;
    }

    private void generateLang() {
        Translations.generateLang(this);
    }

    private void createConfig() {
        saveDefaultConfig(); // Moved to config.yml

        saveResource("integrations/lands.yml", false);
        saveResource("integrations/custom-structures.yml", false);

        final File setupFile = firstTimeSetupFile();
        firstTimeSetup = !setupFile.exists();
        setupFile.mkdirs();
    }

    public File firstTimeSetupFile() {
        return new File(this.getDataFolder() + File.separator + "integration" + File.separator + ".setup");
    }

    public boolean isFirstTimeSetup() {
        return firstTimeSetup;
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ServerReloadListener(this), this);
        pm.registerEvents(new ConnectionListener(this), this);
        pm.registerEvents(new RegionListener(this), this);
        pm.registerEvents(new FaunaListener(this), this);
        pm.registerEvents(new MoveListener(this), this);
        if (pm.getPlugin("CustomStructures") != null) {
            pm.registerEvents(new CustomStructuresListener(this), this);
        }

        if (pm.getPlugin("MythicMobs") != null) {
            pm.registerEvents(new MythicFaunaDiscoverer(this), this);
        }
    }

    private void registerCommands() {
        new Commands(this);
    }

    private void registerRewards() {
        IRPGRegionsRegistry<DiscoveryReward> registry = managers.getRegistry(RegionRewardRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register rewards");
            return;
        }
        registry.register(ConsoleCommandReward.class);
        registry.register(ExperienceReward.class);
        registry.register(ItemReward.class);
        registry.register(MessageReward.class);
        registry.register(MoneyReward.class);
        registry.register(PlayerCommandReward.class);
        registry.register(AlonsoLevelReward.class);
        registry.register(QuestReward.class);
        registry.register(TeleportReward.class);
        registry.register(RegionDiscoverReward.class);
        registry.register(PlaceholderConsoleCommandReward.class);
        registry.register(MMOCoreLevelReward.class);
    }

    private void registerRequirements() {
        IRPGRegionsRegistry<RegionRequirement> registry = managers.getRegistry(RegionRequirementRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register requirements");
            return;
        }

        registry.register(AlonsoLevelRequirement.class);
        registry.register(ItemRequirement.class);
        registry.register(LevelRequirement.class);
        registry.register(MMOCoreLevelRequirement.class);
        registry.register(MoneyRequirement.class);
        registry.register(PlaceholderRequirement.class);
        registry.register(DependencyRequirement.class);
        registry.register(QuestRequirement.class);
        registry.register(PermissionRequirement.class);
    }

    private void registerEffects() {
        IRPGRegionsRegistry<RegionEffect> registry = managers.getRegistry(RegionEffectRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register effects");
            return;
        }
        registry.register(PotionRegionEffect.class);
        registry.register(FogEffect.class);
        registry.register(VanishEffect.class);
    }

    @Override
    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DiscoveryReward.class, new AbstractAdapter<DiscoveryReward>(null))
                .registerTypeAdapter(RegionEffect.class, new AbstractAdapter<RegionEffect>(null))
                .registerTypeAdapter(RegionRequirement.class, new AbstractAdapter<RegionRequirement>(null))
                .registerTypeAdapter(RPGRegionsRegion.class, new AbstractAdapter<RPGRegionsRegion>(null))
                .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter(this))
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
                .setPrettyPrinting()
                .serializeNulls().create();
    }

    @Override
    public boolean hasHeadDatabase() {
        return Bukkit.getPluginManager().getPlugin("HeadDatabase") != null;
    }

    @Override
    public PlatformScheduler<? extends IRPGRegionsAPI> getScheduler() {
        return scheduler;
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        this.ecoProvider = rsp.getProvider();
    }

    public Economy getEcoProvider() {
        return ecoProvider;
    }

    private void registerTasks() {
        if (Bukkit.getPluginManager().getPlugin("Dynmap") != null
                && getConfig().getBoolean("settings.external.dynmap")) {
            getScheduler().executeRepeating(new DynmapTask(this), 0L, 20L);
            getLogger().info("Registered support for Dynmap.");
        }
        getScheduler().executeRepeating(new FaunaDiscoverer(this), 0L, 10L);
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 2066);
        // regions_discovered chart currently causes lag due to bStats not running it async :(
        if (getConfig().getBoolean("settings.metrics.send_custom_info")) {
			/*metrics.addCustomChart(new SingleLineChart("regions_discovered", () -> {
				debug("Submitting custom metrics on thread: " + Thread.currentThread().getName());
				int discoveries = 0;
				for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
					RPGRegionsAccount account = getManagers().getStorageManager().getAccount(offlinePlayer.getUniqueId()).get();
					discoveries = discoveries + account.getDiscoveredRegions().values().size();
					Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
					if (player == null)
						getManagers().getStorageManager().removeCachedAccount(offlinePlayer.getUniqueId()); // Cleanup so we don't use memory
				}
				return discoveries;
			}));*/
        }
        metrics.addCustomChart(new SingleLineChart("regions_configured", () -> getManagers().getRegionsCache().getConfiguredRegions().size()));
        metrics.addCustomChart(new SimplePie("storage_mode", () -> getConfig().getString("settings.storage.mode")));
        metrics.addCustomChart(new SimplePie("integration_type", () -> getConfig().getString("settings.integration.name")));
    }

    private DateTimeFormatter formatter;

    public DateTimeFormatter getDateFormatter() {
        if (formatter != null) return formatter;
        return formatter = DateTimeFormatter.ofPattern(getConfig().getString("settings.server.discoveries.date.format"));
    }

    private Boolean debugEnabled;

    // Called when the config is reloaded or the debug value was changed
    public void markDebugDirty() {
        this.debugEnabled = null;
        this.formatter = null;
    }

    @Override
    public boolean debug() {
        // This part of the code is called very often, caching the boolean gives a big performance boost at high player counts
        if (this.debugEnabled == null) {
            this.debugEnabled = this.getConfig().getBoolean("settings.dev.debug");
        }
        return debugEnabled;
    }

    @Override
    public void debug(String debug) {
        this.debug(debug, Level.INFO);
    }

    public void debug(String debug, Level level) {
        if (debug()) this.getLogger().log(level, "[Debug] " + debug);
    }
}
