package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import co.aikar.idb.DB;
import co.aikar.idb.Database;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.managers.data.IStorageManager;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.thread.Blocking;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.SQLException;

@CommandPermission("rpgregions.debug")
public class RPGRegionsDebugCommand {

    private final RPGRegions plugin;

    public RPGRegionsDebugCommand(final RPGRegions plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("rpgregionsdebug|rpgrd")
    public void onDefault(CommandSender sender) throws SQLException {
        IStorageManager storageManager = plugin.getManagers().getStorageManager();
        sender.sendMessage(ChatColor.GOLD + "Database status:");
        sender.sendMessage(ChatColor.GRAY + "Storage implementation " + storageManager.getClass().getSimpleName() + ".");
        sender.sendMessage(ChatColor.GRAY + "" + storageManager.getCachedAccounts().synchronous().asMap().size()
                + " cached players.");
        sender.sendMessage(ChatColor.GRAY + "Performance (" + storageManager.getTimingsAverage() + " avg. ms)" +
                " (last 3 retrievals): " + StringUtils.join(storageManager.getTimings(), "ms, ") + "ms");
        Database database = DB.getGlobalDatabase();
        if (database != null) {
            Connection connection = database.getConnection();
            sender.sendMessage(ChatColor.GRAY + "Product: " + connection.getMetaData().getDatabaseProductName());
            sender.sendMessage(ChatColor.GRAY + "Version: " + connection.getMetaData().getDatabaseProductVersion());
            sender.sendMessage(ChatColor.GRAY + "Driver: " + connection.getMetaData().getDriverName());
            sender.sendMessage(ChatColor.GRAY + "Version: " + connection.getMetaData().getDriverVersion());
            database.closeConnection(connection);
        }

        plugin.getManagers().getRegionsCache().getConfiguredRegions().forEach((name, configuredRegion) -> {
            for (DiscoveryReward reward : configuredRegion.getRewards()) {
                if (reward instanceof Blocking) {
                    sender.sendMessage(ChatColor.RED + "Region " + name + " has blocking class " + reward + ".");
                }
            }
            for (RegionRequirement requirement : configuredRegion.getRequirements()) {
                if (requirement instanceof Blocking) {
                    sender.sendMessage(ChatColor.RED + "Region " + name + " has blocking class " + requirement + ".");
                }
            }
            for (RegionEffect effect : configuredRegion.getEffects()) {
                if (effect instanceof Blocking) {
                    sender.sendMessage(ChatColor.RED + "Region " + name + " has blocking class " + effect + ".");
                }
            }
        });
    }

    @CommandMethod("rpgregionsdebug|rpgrd enable|disable|toggle")
    public void onEnable(CommandSender sender) {
        final boolean newValue = !plugin.debug();
        plugin.getConfig().set("settings.dev.debug", newValue);
        plugin.markDebugDirty();
        if (newValue) sender.sendMessage(ChatColor.GREEN + "Debug enabled.");
        else sender.sendMessage(ChatColor.RED + "Debug disabled.");
    }

    @CommandMethod("rpgregionsdebug|rpgrd cache")
    public void onCache(CommandSender sender) {
        IStorageManager storageManager = plugin.getManagers().getStorageManager();
        sender.sendMessage(ChatColor.GOLD + "Database cache:");
        storageManager.getCachedAccounts().synchronous().asMap().forEach((uuid, account) -> {
            sender.sendMessage(ChatColor.GRAY + " - " + uuid.toString());
        });
    }

    @CommandMethod("rpgregionsdebug|rpgrd removecached <player>")
    public void onRemoveCached(CommandSender sender, @Argument("player") OfflinePlayer player) {
        IStorageManager storageManager = plugin.getManagers().getStorageManager();
        sender.sendMessage(ChatColor.GREEN + "Removing from cache...");
        storageManager.removeCachedAccount(player.getUniqueId()).thenAccept((a) -> {
            sender.sendMessage(ChatColor.GREEN + "Successfully removed and saved " + player.getName() + "'s account from the cache.");
        });
    }

    @CommandMethod("rpgregionsdebug|rpgrd worldid <world>")
    public void onGetWorldId(CommandSender sender, @Argument("world") World world) {
        sender.sendMessage(ChatColor.GREEN + String.valueOf(world.getUID()));
    }
}
