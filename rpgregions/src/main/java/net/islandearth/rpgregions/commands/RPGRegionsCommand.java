package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Hidden;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.events.RPGRegionsReloadEvent;
import net.islandearth.rpgregions.api.integrations.IntegrationType;
import net.islandearth.rpgregions.gui.DiscoveryGUI;
import net.islandearth.rpgregions.gui.RegionCreateGUI;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.regenerate.Regenerate;
import net.islandearth.rpgregions.rewards.ItemReward;
import net.islandearth.rpgregions.utils.ChatUtils;
import net.islandearth.rpgregions.utils.Colors;
import net.islandearth.rpgregions.utils.RegenUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RPGRegionsCommand {

    private final RPGRegions plugin;
    private final MinecraftHelp<CommandSender> help;
    private final List<UUID> regenerateConfirm = new ArrayList<>();
    private static final String WARNING_MESSAGE = ChatColor.RED + "" + ChatColor.ITALIC + "" + ChatColor.BOLD
            + "The regenerate configuration is very dangerous and can delete world sections if used wrongly.";

    public RPGRegionsCommand(RPGRegions plugin, PaperCommandManager<CommandSender> manager) {
        this.plugin = plugin;
        this.help = new MinecraftHelp<>(
                "/rpgregions help",
                player -> plugin.adventure().sender(player),
                manager
        );
    }

    @CommandDescription("The default RPGRegions command")
    @CommandMethod("rpgregions|rpgr")
    public void onDefault(CommandSender sender) {
        if (sender.hasPermission("rpgregions.noview") && !sender.isOp()) return;

        final Audience audience = plugin.adventure().sender(sender);
        final MiniMessage mm = plugin.miniMessage();

        final String urlOpen = "<hover:show_text:\"<white>Click to open!\">";
        final String wiki = plugin.getDescription().getWebsite();
        final String bugs = "https://gitlab.com/SamB440/rpgregions-2";
        final String discord = "https://discord.gg/fh62mxU";
        audience.sendMessage(mm.deserialize("<gradient:yellow:gold>Wiki > " + urlOpen + "<click:open_url:" + wiki + ">" + wiki));
        audience.sendMessage(mm.deserialize("<gradient:red:gold>Bugs > " + urlOpen + "<click:open_url:" + bugs + ">" + bugs));
        audience.sendMessage(mm.deserialize("<gradient:#7289da:light_purple>Discord > " + urlOpen + "<click:open_url:" + discord + ">" + discord));
        int rewards = 0;
        for (ConfiguredRegion configuredRegion : plugin.getManagers().getRegionsCache().getConfiguredRegions().values()) {
            rewards += configuredRegion.getRewards().size();
        }
        audience.sendMessage(mm.deserialize("<light_purple>"
                + plugin.getManagers().getRegionsCache().getConfiguredRegions().size()
                + " <dark_purple>regions are loaded with <light_purple>" + rewards + " <dark_purple>rewards, " +
                "<light_purple>" + plugin.getManagers().getFaunaCache().getFauna().size() + " <dark_purple>fauna is loaded."));
    }

    @CommandMethod("rpgregions|rpgr help [query]")
    public void onHelp(final CommandSender sender, @Argument("query") @Greedy @Nullable String query) {
        help.queryCommands(query == null ? "" : query, sender);
    }

    @CommandDescription("Debug information about the plugin")
    @CommandMethod("rpgregions|rpgr about")
    @Hidden
    public void onAbout(CommandSender sender) {
        sender.sendMessage(Colors.colour("&eRPGRegions v" + plugin.getDescription().getVersion() + "."));
        sender.sendMessage(Colors.colour("&eOwner: https://www.spigotmc.org/members/%%__USER__%%/"));
        sender.sendMessage(Colors.colour("&eStorage: " + plugin.getManagers().getStorageManager().getClass().getName()));
        sender.sendMessage(Colors.colour("&eIntegration: " + plugin.getManagers().getIntegrationManager().getClass().getName()));
    }

    @CommandDescription("Creates a configured region from a region created in your integration")
    @CommandPermission("rpgregions.add")
    @CommandMethod("rpgregions|rpgr add <region> [world]")
    public void onAdd(CommandSender sender,
                      @Argument(value = "region", suggestions = "integration-regions") String region,
                      @Argument("world") @Nullable World world) {
        if (plugin.getManagers().getRegionsCache().getConfiguredRegion(region).isPresent()) {
            Colors.sendColourful(sender, Component.text("That region is already configured.", Colors.BRIGHT_RED));
            return;
        }

        Location location = null;
        if (sender instanceof Player player) {
            location = player.getLocation();
        }

        if (world != null) {
            if (location == null) location = new Location(world, 0, 100, 0);
            else location.setWorld(world);
        }

        if (location == null) {
            sender.sendMessage(ChatColor.RED + "Console needs to provide a world name!");
            return;
        }

        if (!plugin.getManagers().getIntegrationManager().exists(location.getWorld(), region)) {
            Colors.sendColourful(sender, Component.text("That region does not exist in your protection plugin.", Colors.BRIGHT_RED));
            return;
        }

        add(location, region);
        ChatUtils.eventOk(sender, "Added configured region " + region + "!");
        sender.sendMessage(Colors.colour("&e&oNow use /rpgregions edit "
                + region
                + " to edit it!"));
        sender.sendMessage(Colors.colour("&e&oUse /rpgregions save to save this to file for editing."));
    }

    private void add(@NotNull final Location location, final String region) {
        ConfiguredRegion configuredRegion = new ConfiguredRegion(location.getWorld(), region, region, new ArrayList<>(),
                new ArrayList<>());
        configuredRegion.setLocation(location);
        plugin.getManagers().getRegionsCache().addConfiguredRegion(configuredRegion);
    }

    @CommandDescription("Sets the display name of a region")
    @CommandPermission("rpgregions.setname")
    @CommandMethod("rpgregions|rpgr setname <region> <name>")
    public void onSetName(CommandSender sender,
                          @Argument("region") ConfiguredRegion region,
                          @Argument("name") @Greedy String name) {
        region.setCustomName(name);
        Colors.sendColourful(sender, Component.text("Set name of region '" + region.getId() + "' to: " + name, Colors.EREBOR_GREEN));
    }

    @CommandDescription("Removes a configured region. Does not delete it from your integration.")
    @CommandPermission("rpgregions.remove")
    @CommandMethod("rpgregions|rpgr remove <region>")
    public void onRemove(CommandSender sender, @Argument("region") ConfiguredRegion region) {
        region.delete(plugin);
        plugin.getManagers().getRegionsCache().removeConfiguredRegion(region.getId());
        Colors.sendColourful(sender, Component.text("Removed configured region " + region.getId() + "!", Colors.BRIGHT_RED));
    }

    @CommandDescription("Opens the editor GUI for a region")
    @CommandPermission("rpgregions.edit")
    @CommandMethod("rpgregions|rpgr edit <region>")
    public void onEdit(Player player, @Argument("region") ConfiguredRegion region) {
        new RegionCreateGUI(plugin, player, region).open();
    }

    @CommandDescription("Opens the /discovery GUI")
    @CommandPermission("rpgregions.list")
    @CommandMethod("rpgregions|rpgr list|discoveries")
    public void onList(Player player) {
        new DiscoveryGUI(plugin, player).open();
    }

    @CommandDescription("Adds an item reward to a region (util command)")
    @CommandPermission("rpgregions.additem")
    @CommandMethod("rpgregions|rpgr additem <region>")
    public void onAddItem(Player player, @Argument("region") ConfiguredRegion region) {
        region.getRewards().add(new ItemReward(plugin, player.getInventory().getItemInMainHand()));
        Colors.sendColourful(player, Component.text("Item added to configuration!", Colors.EREBOR_GREEN));
    }

    @CommandDescription("Reloads configured regions from the `/plugins/RPGRegions/regions` folder.")
    @CommandPermission("rpgregions.reload")
    @CommandMethod("rpgregions|rpgr reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Reloading region files...");
        long startTime = System.currentTimeMillis();
        File folder = new File(plugin.getDataFolder() + "/regions/");
        plugin.getManagers().getRegionsCache().clear();

        for (File file : folder.listFiles()) {
            // Exclude non-json files
            if (file.getName().endsWith(".json")) {
                try (Reader reader = new FileReader(file)) {
                    ConfiguredRegion region = plugin.getGson().fromJson(reader, ConfiguredRegion.class);
                    if (!region.getId().equals("exampleconfig"))
                        plugin.getManagers().getRegionsCache().addConfiguredRegion(region);
                } catch (Exception e) {
                    plugin.getLogger().severe("Error loading region config " + file.getName() + ":");
                    e.printStackTrace();
                }
            }
        }

        plugin.reloadConfig();
        plugin.markDebugDirty();
        plugin.getManagers().getRegenerationManager().reload();
        Bukkit.getPluginManager().callEvent(new RPGRegionsReloadEvent());
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Colors.sendColourful(sender, Component.text("Done! (" + totalTime + "ms)", Colors.EREBOR_GREEN));
    }

    @CommandDescription("Saves configured regions to the `/plugins/RPGRegions/regions` folder. Also saves user data.")
    @CommandPermission("rpgregions.save")
    @CommandMethod("rpgregions|rpgr save [async]")
    public void onSave(CommandSender sender,
                       @Argument(value = "async", suggestions = "async") @Nullable String asyncArg) {
        boolean async = asyncArg != null && asyncArg.equals("--async");
        sender.sendMessage(ChatColor.GREEN + "Saving data..." + (async ? ChatColor.GOLD + " (async)" : ""));
        long startTime = System.currentTimeMillis();

        // Save all player data (quit event not called for shutdown)
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (plugin.getManagers().getStorageManager().getCachedAccounts().synchronous().asMap().containsKey(player.getUniqueId()))
                plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
        });

        // Save all region configs
        long asyncStartTime = System.currentTimeMillis();
        CompletableFuture<Boolean> saveFuture = plugin.getManagers().getRegionsCache().saveAll(async);
        long mainEndTime = System.currentTimeMillis();
        long mainTotalTime = mainEndTime - startTime;
        saveFuture.thenAccept(saved -> {
           long asyncTotalTime = System.currentTimeMillis() - asyncStartTime;
           StringBuilder sb = new StringBuilder();
           sb.append(ChatColor.GREEN).append("Done! (%sms)".formatted(mainTotalTime));
           if (async) sb.append(' ').append(ChatColor.GOLD).append("(async execution took %sms)".formatted(asyncTotalTime));
           sender.sendMessage(sb.toString());
        });
    }

    @CommandDescription("Resets the data of a player")
    @CommandPermission("rpgregions.reset")
    @CommandMethod("rpgregions|rpgr reset <player> [region]")
    public void onReset(CommandSender sender,
                        @Argument("player") @NonNull OfflinePlayer player,
                        @Argument("region") @Nullable ConfiguredRegion region) {
        if (region == null) {
            plugin.getManagers().getStorageManager().clearDiscoveries(player.getUniqueId());
            if (!player.isOnline())
                plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Players discoveries has been cleared.");
        } else {
            if (plugin.getManagers().getRegionsCache().getConfiguredRegions().containsKey(region.getId())) {
                plugin.getManagers().getStorageManager().clearDiscovery(player.getUniqueId(), region.getId());
                if (!player.isOnline())
                    plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
                Colors.sendColourful(sender, Component.text("Discovery cleared.", Colors.EREBOR_GREEN));
            } else {
                Colors.sendColourful(sender, Component.text("Region does not exist or is not configured.", Colors.BRIGHT_RED));
            }
        }
    }


    @CommandDescription("Deletes user accounts from the database")
    @CommandPermission("rpgregions.delete")
    @CommandMethod("rpgregions|rpgr delete <player>")
    public void onDelete(CommandSender sender,
                         @Argument("player") @NotNull OfflinePlayer player) {
        plugin.getManagers().getStorageManager().deleteAccount(player.getUniqueId());
        Colors.sendColourful(sender, Component.text("Deleted account of player.", Colors.EREBOR_GREEN));
    }

    @CommandDescription("Sets the teleport location of a region")
    @CommandPermission("rpgregions.setlocation")
    @CommandMethod("rpgregions|rpgr setlocation <region>")
    public void onSetLocation(Player player,
                              @Argument("region") ConfiguredRegion region) {
        Location location = player.getLocation();
        region.setLocation(location);
        Colors.sendColourful(player, Component.text("Location has been updated.", Colors.EREBOR_GREEN));
    }

    @CommandDescription("Sets or removes the sound of a region when it is discovered")
    @CommandPermission("rpgregions.edit")
    @CommandMethod("rpgregions|rpgr setsound <region> [sound]")
    public void onSetSound(CommandSender sender,
                           @Argument("region") ConfiguredRegion region,
                           @Argument(value = "sound", suggestions = "sound_names") @Nullable String soundName) {
        region.setSoundName(soundName);
        Colors.sendColourful(sender, Component.text("Set the sound name to " + soundName + ".", Colors.EREBOR_GREEN));
    }

    @Suggestions("sound_names")
    public List<String> soundNameSuggestions(CommandContext<CommandSender> context, String input) {
        return Arrays.stream(Sound.values()).map(Enum::name).collect(Collectors.toList());
    }

    //TODO: use confirmation api
    @CommandDescription("If configured, regenerates a region to the set schematic")
    @CommandPermission("rpgregions.regenerate")
    @CommandMethod("rpgregions|rpgr regenerate <region>")
    public void onRegenerate(Player player,
                             @Argument("region") ConfiguredRegion region) {
        IntegrationType integrationType = IntegrationType.valueOf(plugin.getConfig().getString("settings.integration.name").toUpperCase());
        if (integrationType != IntegrationType.WORLDGUARD) {
            player.sendMessage(ChatColor.RED + "Regeneration only supports WorldGuard integrations.");
            return;
        }

        if (!regenerateConfirm.contains(player.getUniqueId())) {
            regenerateConfirm.add(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Run /rpgregions regenerate " + region.getId() + " again to confirm. Only use if you know what you are doing!");
            player.sendMessage(WARNING_MESSAGE);
        } else {
            regenerateConfirm.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Regenerating region...");
            long startTime = System.currentTimeMillis();
            boolean done = RegenUtils.regenerate(region);
            if (!done) {
                player.sendMessage(ChatColor.RED + "Unable to regenerate region. Check console for details.");
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            player.sendMessage(ChatColor.GREEN + "Done! (" + totalTime + "ms)");
        }
    }

    @CommandDescription("Sets the regeneration schematic for a region")
    @CommandPermission("rpgregions.setschematic")
    @CommandMethod("rpgregions|rpgr setschematic <region> <schematicName>")
    public void onAddSchematic(Player player,
                               @Argument("region") ConfiguredRegion region,
                               @Argument("schematicName") @Greedy String schematicName) {
        IntegrationType integrationType = IntegrationType.valueOf(plugin.getConfig().getString("settings.integration.name").toUpperCase());
        if (integrationType != IntegrationType.WORLDGUARD) {
            player.sendMessage(ChatColor.RED + "Regeneration only supports WorldGuard integrations.");
            return;
        }

        if (region != null) {
            if (!regenerateConfirm.contains(player.getUniqueId())) {
                regenerateConfirm.add(player.getUniqueId());
                player.sendMessage(ChatColor.YELLOW + "Run /rpgregions addschematic " + schematicName + " again to confirm. Only use if you know what you are doing!");
                player.sendMessage(ChatColor.RED + "MAKE SURE YOU ARE STANDING WHERE YOU CREATED THE SCHEMATIC ORIGINALLY (//copy, //schematic save), OTHERWISE IT WILL NOT PASTE CORRECTLY.");
            } else {
                regenerateConfirm.remove(player.getUniqueId());
                Regenerate regenerate = region.getRegenerate();
                if (regenerate == null) regenerate = new Regenerate(Integer.MAX_VALUE, false, new ArrayList<>());
                regenerate.setSchematicName(schematicName);
                regenerate.setOrigin(player.getLocation());
                region.setRegenerate(regenerate);
                player.sendMessage(ChatColor.GREEN + "This region has had a regenerate section added, and schematicName set to " + schematicName + " and origin set to " + player.getLocation() + ".");
                player.sendMessage(ChatColor.YELLOW + "Run /rpgregions save and " + ChatColor.BOLD + "CONFIGURE BEFORE RELOADING OR RESTARTING THE SERVER.");
            }
            player.sendMessage(WARNING_MESSAGE);
        }
    }

    @CommandDescription("Resets the icons of all configured regions to the config default")
    @CommandPermission("rpgregions.forceupdate")
    @CommandMethod("rpgregions|rpgr forceupdateicons")
    public void onForceUpdateIcons(Player player) {
        Optional<Material> defaultIcon = Optional.of(Material.valueOf(RPGRegionsAPI.getAPI().getConfig().getString("settings.server.gui.default_region_icon")));
        defaultIcon.ifPresent(xMaterial -> {
            plugin.getManagers().getRegionsCache().getConfiguredRegions().forEach((name, region) -> {
                region.setUndiscoveredIcon(xMaterial);
                region.setIcon(xMaterial);
                player.sendMessage(ChatColor.GREEN + "Updated icons for: " + name + ".");
            });
        });
    }
}
