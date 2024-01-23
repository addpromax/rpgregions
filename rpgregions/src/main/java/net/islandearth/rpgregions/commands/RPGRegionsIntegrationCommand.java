package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.integrations.rpgregions.RPGRegionsIntegration;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.CuboidRegion;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.PolyRegion;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.RPGRegionsRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@CommandPermission("rpgregions.integration")
public class RPGRegionsIntegrationCommand {

    private final RPGRegions plugin;
    private final MinecraftHelp<CommandSender> help;

    public RPGRegionsIntegrationCommand(final RPGRegions plugin, PaperCommandManager<CommandSender> manager) {
        this.plugin = plugin;
        this.help = new MinecraftHelp<>(
                "/rpgri help",
                player -> plugin.adventure().sender(player),
                manager
        );
    }

    @CommandDescription("The default RPGRegions integration command.")
    @CommandMethod("rpgri|rpgrintegration")
    public void onDefault(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "RPGRegions region integration is enabled. Type /rpgri help for help.");
    }

    @CommandMethod("rpgri|rpgrintegration help [query]")
    public void onHelp(final CommandSender sender, @Argument("query") @Greedy @Nullable String query) {
        help.queryCommands(query == null ? "" : query, sender);
    }

    @CommandDescription("Lists all existing regions in the integration")
    @CommandMethod("rpgri|rpgrintegration list")
    public void onList(final CommandSender sender) {
        RPGRegionsIntegration integration = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        integration.getAllRegionNames(null).forEach(name -> sender.sendMessage(ChatColor.GREEN + "- " + name));
    }

    @CommandDescription("Shows information about a region")
    @CommandMethod("rpgri|rpgrintegration info <region>")
    public void onInfo(final CommandSender sender,
                       @Argument(value = "region") final RPGRegionsRegion region) {
        sender.sendMessage(ChatColor.GREEN + "Name: " + ChatColor.WHITE + region.getName());
        sender.sendMessage(ChatColor.GREEN + "Priority: " + ChatColor.WHITE + region.getPriority());
        sender.sendMessage(ChatColor.GREEN + "Points for " + region.getName() + ":");
        region.getPoints().forEach(point -> sender.sendMessage(" " + point.toString()));
    }

    @CommandDescription("Saves all regions")
    @CommandMethod("rpgri|rpgrintegration save")
    public void onSave(final CommandSender sender) {
        RPGRegionsIntegration integration = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        integration.save();
        sender.sendMessage(ChatColor.GREEN + "Regions saved.");
    }

    @CommandDescription("Creates a region of the specified name, type, and optionally the world it is in.")
    @CommandMethod("rpgri|rpgrintegration create <name> <type> <world>")
    public void onCreate(final CommandSender sender,
                         @Argument("name") final String name,
                         @Argument(value = "type", suggestions = "region-types") final String regionType,
                         @Argument("world") @Nullable World argWorld) {
        // If worldName is null, try to get from sender if they are a player, else overworld, else worldName world
        World world = argWorld == null ? sender instanceof Player player ? player.getWorld() : Bukkit.getWorlds().get(0) : argWorld;
        RPGRegionsRegion region = switch (regionType.toLowerCase(Locale.ENGLISH)) {
            case "cuboid" -> new CuboidRegion(name, world);
            case "poly" -> new PolyRegion(name, world);
            default -> null;
        };

        if (region == null) {
            sender.sendMessage(ChatColor.RED + "A region of the type " + regionType + " cannot be found!");
            return;
        }

        if (argWorld == null && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "WARNING: World name was not provided and you are not a player. Defaulted to '" + world.getName() + "'.");
        }

        RPGRegionsIntegration integration = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        integration.addRegion(region);
        sender.sendMessage(ChatColor.GREEN + "Created region " + name + ".");
    }

    @CommandDescription("Deletes a region.")
    @CommandMethod("rpgri|rpgrintegration delete <region>")
    public void onDelete(final CommandSender sender,
                         @Argument("region") final RPGRegionsRegion region) {
        RPGRegionsIntegration integration = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        integration.removeRegion(region);
        sender.sendMessage(ChatColor.GREEN + "Region " + region.getName() + " has been removed.");
    }

    @CommandDescription("Adds a position to a region. Cuboid regions require 2 points to form a cuboid. Poly regions may have any number.")
    @CommandMethod("rpgri|rpgrintegration addpos <region> [location]")
    public void onAddPos(final CommandSender sender,
                         @Argument("region") final RPGRegionsRegion region,
                         @Argument("location") @Nullable Location location) {
        if (sender instanceof Player player && location == null) {
            if (!region.addPoint(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "Could not add a point to that region because it exceeds the size for its type.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Added point to " + region.getName() + ".");
            }
        } else {
            if (location == null) {
                sender.sendMessage(ChatColor.RED + "You need to specify the world, x, y, z of the point.");
                return;
            }

            if (!region.addPoint(location)) {
                sender.sendMessage(ChatColor.RED + "Could not add a point to that region because it exceeds the size for its type.");
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "Added point to " + region.getName() + ".");
        }
    }

    @CommandDescription("Removes a position from a region.")
    @CommandMethod("rpgri|rpgrintegration removepos <region> [location]")
    public void onRemovePos(final CommandSender sender,
                         @Argument("region") final RPGRegionsRegion region,
                         @Argument("location") @Nullable Location location) {
        if (sender instanceof Player player && location == null) {
            for (Location point : region.getPoints()) {
                if (point.distanceSquared(player.getLocation()) <= 2) {
                    player.sendMessage(ChatColor.GREEN + "Removed point from " + region.getName() + ".");
                    return;
                }
            }
            player.sendMessage(ChatColor.RED + "Could not remove that point from the region because it does not exist.");
        } else {
            if (location == null) {
                sender.sendMessage(ChatColor.RED + "You need to specify the world, x, y, z of the point.");
                return;
            }

            if (!region.removePoint(location)) {
                sender.sendMessage(ChatColor.RED + "Could not remove a point from the region because it does not exist.");
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "Removed point from " + region.getName() + ".");
        }
    }

    @CommandDescription("Sets the priority of a region. Higher priority regions override lower priority ones in the same location.")
    @CommandMethod("rpgri|rpgrintegration setpriority <region> <priority>")
    public void onSetPriority(final Player player,
                              @Argument("region") final RPGRegionsRegion region,
                              @Argument("priority") final int priority) {
        region.setPriority(priority);
        player.sendMessage(ChatColor.GREEN + "Set priority of " + region.getName() + " to " + priority + ".");
    }

    @CommandDescription("Sets the world a region is in.")
    @CommandMethod("rpgri|rpgrintegration setworld <region> <world>")
    public void onSetWorld(final CommandSender sender,
                           @Argument("region") final RPGRegionsRegion region,
                           @Argument("world") final World world) {
        region.setWorld(world.getUID());
        sender.sendMessage(ChatColor.GREEN + "Set region '" + region.getName() + "' world to '" + world.getName() + "'.");
    }

    @CommandDescription("Visualises the boundaries of a region.")
    @CommandMethod("rpgri|rpgrintegration visualise <region>")
    public void onVisualise(final Player sender, @Argument("region") final RPGRegionsRegion region) {
        region.visualise(sender);
    }

    @CommandDescription("Tells you what region you are in.")
    @CommandMethod("rpgri|rpgrintegration whereami")
    public void onWhereAmI(final Player sender) {
        final RPGRegionsIntegration manager = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        List<RPGRegionsRegion> regions = new ArrayList<>();
        for (RPGRegionsRegion region : manager.getRegions()) {
            if (region.isWithinBounds(sender)) {
                regions.add(region);
            }
        }

        if (regions.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You are not currently inside any region.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You are currently inside these regions:");
            for (RPGRegionsRegion region : regions) {
                sender.sendMessage(ChatColor.GREEN + " - " + region.getName() + " (p: " + region.getPriority() + ")");
            }
        }
    }

    @CommandDescription("Migrates all regions to a world.")
    @CommandMethod("rpgri|rpgrintegration migrate <world>")
    public void onMigrate(final CommandSender sender,
                          @Argument("world") final World world) {
        RPGRegionsIntegration integration = (RPGRegionsIntegration) plugin.getManagers().getIntegrationManager();
        for (RPGRegionsRegion region : integration.getRegions()) {
            region.setWorld(world.getUID());
        }

        sender.sendMessage(ChatColor.GREEN + "Set all regions to world '" + world.getName() + "'.");
    }
}
