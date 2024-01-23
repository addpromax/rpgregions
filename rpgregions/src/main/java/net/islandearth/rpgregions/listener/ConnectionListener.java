package net.islandearth.rpgregions.listener;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record ConnectionListener(RPGRegions plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getManagers().getStorageManager().getCachedAccounts().synchronous().asMap().containsKey(player.getUniqueId())) {
            player.kickPlayer(ChatColor.RED + "[RPGRegions] Player user data not present! Please contact an administrator.");
            return;
        }

        if (plugin.isFirstTimeSetup() /*|| plugin.getConfig().getBoolean("settings.dev.always", true)*/) {
            final MiniMessage miniMessage = plugin.miniMessage();
            final Audience audience = plugin.adventure().player(player);
            // Don't know if this works with the programs used to remove this stuff
            // Don't care, just making an attempt, as I don't want people who haven't purchased the plugin to see the welcome screen.
            char[] obfChars = new char[]{'%', '%', '_', '_', 'U', 'S', 'E', 'R', '_', '_', '%', '%'};
            boolean valid = !String.valueOf(obfChars).equals("%%__USER__%%");
            if (valid) {
                if (!plugin.isFirstTimeSetup()) return;
                audience.sendMessage(Component.text("Welcome to RPGRegions v" + plugin.getDescription().getVersion() + "!", NamedTextColor.GOLD));
                audience.sendMessage(Component.text("Thank you for purchasing the plugin, your support is appreciated.", NamedTextColor.GREEN));
                audience.sendMessage(Component.empty());
                audience.sendMessage(miniMessage.deserialize(
                        "<gradient:gold:yellow>As the plugin has been newly installed, you will need to:"));
                audience.sendMessage(miniMessage.deserialize("" +
                        "<gradient:gold:yellow> - Set the correct region implementation you wish to use in the config.yml."));
                audience.sendMessage(miniMessage.deserialize("<gradient:gold:yellow> - Make sure you set the correct permissions. If you are OP or have all perms, you will bypass region requirements etc."));
                audience.sendMessage(miniMessage.deserialize(
                        "<gradient:gold:yellow> - Setup a database if you will be dealing with high player counts." +
                              " <red><bold>Note: Migration is currently not possible."));
                audience.sendMessage(Component.empty());
                audience.sendMessage(Component.text("If you have any questions, join our Discord for help: ", NamedTextColor.GOLD)
                        .append(Component.text("https://discord.gg/fh62mxU", NamedTextColor.YELLOW, TextDecoration.UNDERLINED))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to open!", NamedTextColor.WHITE)))
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/fh62mxU")));
                audience.sendMessage(Component.text("This message will not show again after a restart.", NamedTextColor.GREEN));
                return;
            }

            audience.sendMessage(Component.text("Welcome to RPGRegions v" + plugin.getDescription().getVersion() + "!", NamedTextColor.GOLD));
            audience.sendMessage(Component.text("The plugin is running in a development environment.", NamedTextColor.RED));
//            if (!plugin.debug()) {
//                plugin.getConfig().set("settings.dev.debug", true);
//                plugin.markDebugDirty();
//            }
        }
    }

    // PlayerLoginEvent is called AFTER AsyncPlayerPreLoginEvent
    // We can't really avoid loading the user first unfortunately
    @EventHandler(priority = EventPriority.HIGHEST) // Highest so we are always the last called
    public void onValidate(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            // DO NOT save the user if this is due to whitelist!
            // If they are a new user that was prevented from joining due to whitelist
            // Then when they join after whitelist is disabled, they will not be considered new
            // Also saves performance
            plugin.getManagers().getStorageManager().removeCachedAccount(event.getPlayer().getUniqueId(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Highest so we are always the last called
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        // If something else has prevented the player from joining, we don't want to load the user.
        // Otherwise, we will get memory leaks.
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        final UUID uuid = event.getUniqueId();

        try {
            RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(uuid).get();
            if (account != null) {
                plugin.debug("Added user: " + account.getUuid());
                return;
            }
        } catch (Exception e) {
            // If there was an error, don't allow entry!
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "An error occurred whilst loading player user data! Please contact an administrator.");
            e.printStackTrace();
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "RPGRegions account could not be loaded.");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent pqe) {
        Player player = pqe.getPlayer();
        final CompletableFuture<RPGRegionsAccount> future = plugin.getManagers().getStorageManager().getCachedAccounts().getIfPresent(player.getUniqueId());
        if (future == null) return;
        // Always save on quit
        future.thenAccept(account -> {
            plugin.getManagers().getStorageManager().saveAccount(account);
        });
    }
}
