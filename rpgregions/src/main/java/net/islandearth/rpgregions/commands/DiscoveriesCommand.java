package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.events.FaunaDiscoverEvent;
import net.islandearth.rpgregions.api.events.RegionDiscoverEvent;
import net.islandearth.rpgregions.fauna.FaunaInstance;
import net.islandearth.rpgregions.gui.DiscoveryGUI;
import net.islandearth.rpgregions.managers.data.fauna.IFaunaCache;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.data.region.WorldDiscovery;
import net.islandearth.rpgregions.translation.Translations;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscoveriesCommand {

    private final RPGRegions plugin;

    public DiscoveriesCommand(final RPGRegions plugin) {
        this.plugin = plugin;
    }

    @CommandDescription("Opens the discovery GUI")
    @CommandPermission("rpgregions.list")
    @CommandMethod("discoveries|discovery")
    public void onDefault(Player player) {
        new DiscoveryGUI(plugin, player).open();
    }

    @CommandDescription("Opens the bestiary book")
    @CommandPermission("rpgregions.bestiary")
    @CommandMethod("bestiary|flora|fauna")
    public void onBestiary(Player player) {
        openBestiary(player);
    }

    @CommandDescription("Shows a specific bestiary entry")
    @CommandPermission("rpgregions.bestiary")
    @CommandMethod("bestiary|flora|fauna show <fauna>")
    public void onBestiaryShow(Player player,
                           @Argument(value = "fauna") FaunaInstance<?> fauna) {
        fauna.openDescription(player);
    }

    private static final Component BULLET_POINT = Component.text("◦ ");

    public static void openBestiary(Player player) {
        player.playSound(player.getEyeLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
        final IRPGRegionsAPI plugin = RPGRegionsAPI.getAPI();
        final Audience audience = plugin.adventure().player(player);
        final Book.Builder builder = Book.builder();
        builder.title(Component.text("Bestiary", NamedTextColor.DARK_PURPLE));
        // Lol, I wonder if some redistribution sites will auto-remove this line?
        // Would cause errors.
        builder.author(Component.text("RPGRegions %%__USER__%%"));
        List<Component> pages = new ArrayList<>();
        int index = 0;
        index += Translations.BESTIARY_PAGE_HEADER.get(player).size();
        TextComponent.Builder pageBuilder = Component.text();
        pageBuilder.append(Component.join(JoinConfiguration.newlines(), Translations.BESTIARY_PAGE_HEADER.get(player)));
        if (index != 0) pageBuilder.appendNewline();
        final IFaunaCache faunaCache = plugin.getManagers().getFaunaCache();
        for (FaunaInstance<?> fauna : faunaCache.getFauna()) {
            if (index > 12) {
                if (!PlainTextComponentSerializer.plainText().serialize(pageBuilder.build()).isEmpty()) pages.add(pageBuilder.build());
                pageBuilder = Component.text();
                index = 0;
            }

            Component displayName = BULLET_POINT.append(plugin.miniMessage().deserialize(fauna.getDisplayName()));
            // If there is no configured hover, set our own
            if (displayName.hoverEvent() == null) {
                displayName = displayName.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), Translations.BESTIARY_DEFAULT_HOVER.get(player))));
            }
            pageBuilder.append(displayName.clickEvent(ClickEvent.runCommand("/bestiary show " + fauna.getIdentifier())));
            pageBuilder.appendNewline();
            index++;
        }

        if (!PlainTextComponentSerializer.plainText().serialize(pageBuilder.build()).isEmpty()) {
            pages.add(pageBuilder.build());
        }

        builder.pages(pages);
        audience.openBook(builder.build());
    }

    @CommandDescription("Discovers a region for a player")
    @CommandPermission("rpgregions.discover")
    @CommandMethod("discoveries|discovery discover region <region> <player>")
    public void onDiscover(CommandSender sender,
                           @Argument("region") ConfiguredRegion configuredRegion,
                           @Argument("player") OfflinePlayer target) {
        plugin.getManagers().getStorageManager().getAccount(target.getUniqueId()).thenAccept(account -> {
            LocalDateTime date = LocalDateTime.now();
            final WorldDiscovery worldDiscovery = new WorldDiscovery(date, configuredRegion.getId());
            account.addDiscovery(worldDiscovery);
            if (target.getPlayer() != null) {
                Player player = target.getPlayer();
                player.sendMessage(ChatColor.GREEN + "An administrator added a discovery to your account.");
                Bukkit.getPluginManager().callEvent(new RegionDiscoverEvent(player, account, configuredRegion, worldDiscovery));
            }
            plugin.getManagers().getStorageManager().removeCachedAccount(target.getUniqueId());

            sender.sendMessage(ChatColor.GREEN + "The player " + target.getName() + " has had the discovery added.");
        });
    }

    @CommandDescription("Discovers fauna for a player")
    @CommandPermission("rpgregions.discover")
    @CommandMethod("discoveries|discovery discover fauna <fauna> <player>")
    public void onDiscover(CommandSender sender,
                           @Argument("fauna") FaunaInstance<?> fauna,
                           @Argument("player") OfflinePlayer target) {
        plugin.getManagers().getStorageManager().getAccount(target.getUniqueId()).thenAccept(account -> {
            LocalDateTime date = LocalDateTime.now();
            final WorldDiscovery worldDiscovery = new WorldDiscovery(date, fauna.getIdentifier());
            account.addDiscovery(worldDiscovery);
            if (target.getPlayer() != null) {
                Player player = target.getPlayer();
                final FaunaDiscoverEvent event = new FaunaDiscoverEvent(player, fauna, worldDiscovery);
                Bukkit.getPluginManager().callEvent(event);
                player.sendMessage(ChatColor.GREEN + "An administrator added a discovery to your account.");
            }
            plugin.getManagers().getStorageManager().removeCachedAccount(target.getUniqueId());

            sender.sendMessage(ChatColor.GREEN + "The player " + target.getName() + " has had the discovery added.");
        });
    }
}
