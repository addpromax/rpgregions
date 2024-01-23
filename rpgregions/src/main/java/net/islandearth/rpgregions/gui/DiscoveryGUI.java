package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import io.papermc.lib.PaperLib;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.command.IconCommand;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.requirements.DependencyRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.Colors;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryGUI extends RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    private ChestGui gui;

    public DiscoveryGUI(RPGRegions plugin, Player player) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
    }

    private Economy getEcoOrNull() {
        return plugin.getEcoProvider();
    }

    @Override
    public void render() {
        this.gui = new ChestGui(plugin.getConfig().getInt("settings.server.gui.general.rows"), ComponentHolder.of(Translations.REGIONS.get(player).get(0)));
        gui.setOnGlobalClick(click -> click.setCancelled(true));
        PaginatedPane pane = new PaginatedPane(paneX, paneY, paneL, paneH, Pane.Priority.HIGHEST);
        OutlinePane oPane = new OutlinePane(oPaneX, oPaneY, oPaneL, oPaneH);
        OutlinePane innerPane = new OutlinePane(iPaneX, iPaneY, iPaneL, iPaneH);
        StaticPane back = new StaticPane(backX, backY, backL, backH, Pane.Priority.HIGHEST);
        StaticPane forward = new StaticPane(forwardX, forwardY, forwardL, forwardH, Pane.Priority.HIGHEST);
        StaticPane exit = new StaticPane(exitX, exitY, exitL, exitH, Pane.Priority.HIGH);

        // Inner pane
        if (plugin.getConfig().getBoolean("settings.server.gui.innerPane.show")) {
            innerPane.setRepeat(true);
            List<String> mask = plugin.getConfig().getStringList("settings.server.gui.innerPane.mask");
            innerPane.applyMask(new Mask(mask.toArray(new String[]{})));
            innerPane.setOnClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));

            innerPane.addItem(new GuiItem(new ItemStackBuilder(Material.valueOf(
                    plugin.getConfig().getString("settings.server.gui.innerPane.innerPane")))
                    .withModel(plugin.getConfig().getInt("settings.server.gui.innerPane.model"))
                    .withName(" ")
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()));

            gui.addPane(innerPane);
        }

        // Outline pane
        if (plugin.getConfig().getBoolean("settings.server.gui.outlinePane.show")) {
            oPane.setRepeat(true);
            List<String> mask = plugin.getConfig().getStringList("settings.server.gui.outlinePane.mask");
            oPane.applyMask(new Mask(mask.toArray(new String[]{})));
            oPane.setOnClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));

            oPane.addItem(new GuiItem(new ItemStackBuilder(Material.valueOf(
                    plugin.getConfig().getString("settings.server.gui.outlinePane.outlinePane")))
                    .withModel(plugin.getConfig().getInt("settings.server.gui.outlinePane.model"))
                    .withName(" ")
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build()));

            gui.addPane(oPane);
        }

        // Back item
        final String bm = plugin.getConfig().getString("settings.server.gui.back.back");
        ItemStack backItem = bm.startsWith("hdb-") && plugin.hasHeadDatabase()
                ?
                new ItemStackBuilder(new HeadDatabaseAPI().getItemHead(bm.replace("hdb-", "")))
                        .withModel(plugin.getConfig().getInt("settings.server.gui.back.model"))
                        .withName(Translations.PREVIOUS_PAGE.get(player).get(0))
                        .lore(Translations.PREVIOUS_PAGE_LORE.get(player))
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
                :
                new ItemStackBuilder(Material.valueOf(bm))
                        .withModel(plugin.getConfig().getInt("settings.server.gui.back.model"))
                        .withName(Translations.PREVIOUS_PAGE.get(player).get(0))
                        .lore(Translations.PREVIOUS_PAGE_LORE.get(player))
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build();

        back.addItem(new GuiItem(backItem, event -> {
            event.setCancelled(true);
            if (pane.getPages() == 0 || pane.getPages() == 1) return;
            if (pane.getPage() == 0) return;

            pane.setPage(pane.getPage() - 1);

            forward.setVisible(true);
            gui.update();
        }), 0, 0);

        // Forward item
        final String fm = plugin.getConfig().getString("settings.server.gui.forward.forward");
        ItemStack forwardItem = fm.startsWith("hdb-") && plugin.hasHeadDatabase()
                ?
                new ItemStackBuilder(new HeadDatabaseAPI().getItemHead(fm.replace("hdb-", "")))
                        .withModel(plugin.getConfig().getInt("settings.server.gui.forward.model"))
                        .withName(Translations.NEXT_PAGE.get(player).get(0))
                        .lore(Translations.NEXT_PAGE_LORE.get(player))
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
                :
                new ItemStackBuilder(Material.valueOf(fm))
                        .withModel(plugin.getConfig().getInt("settings.server.gui.forward.model"))
                        .withName(Translations.NEXT_PAGE.get(player).get(0))
                        .lore(Translations.NEXT_PAGE_LORE.get(player))
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build();

        forward.addItem(new GuiItem(forwardItem, event -> {
            event.setCancelled(true);
            if (pane.getPages() == 0 || pane.getPages() == 1) return;
            if ((pane.getPage() + 1) == pane.getPages()) return;

            pane.setPage(pane.getPage() + 1);

            back.setVisible(true);
            gui.update();
        }), 0, 0);

        // Exit item
        if (plugin.getConfig().getBoolean("settings.server.gui.exit.show")) {
            final String em = plugin.getConfig().getString("settings.server.gui.exit.exit");
            ItemStack item = em.startsWith("hdb-") && plugin.hasHeadDatabase()
                    ?
                    new ItemStackBuilder(new HeadDatabaseAPI().getItemHead(em.replace("hdb-", "")))
                            .withModel(plugin.getConfig().getInt("settings.server.gui.exit.model"))
                            .withName(Translations.EXIT.get(player).get(0))
                            .lore(Translations.EXIT_LORE.get(player))
                            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
                    :
                    new ItemStackBuilder(Material.valueOf(em))
                            .withModel(plugin.getConfig().getInt("settings.server.gui.exit.model"))
                            .withName(Translations.EXIT.get(player).get(0))
                            .lore(Translations.EXIT_LORE.get(player))
                            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                            .build();
            exit.addItem(new GuiItem(item, event -> {
                event.setCancelled(true);
                gui.update();
                player.closeInventory();
                String command = plugin.getConfig().getString("settings.server.gui.exit.command");
                if (!command.isEmpty()) player.performCommand(command
                        .replace("%player%", player.getName()));
            }), 0, 0);

            gui.addPane(exit);
        }

        gui.addPane(back);
        gui.addPane(forward);

        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAcceptAsync(account -> {
            List<GuiItem> items = new ArrayList<>();
            for (ConfiguredRegion configuredRegion : plugin.getManagers().getRegionsCache().getConfiguredRegions().values()) {
                boolean hasDiscovered = account.getDiscoveries().containsKey(configuredRegion.getId());
                if ((!hasDiscovered && !player.hasPermission("rpgregions.show"))
                        || configuredRegion.isHidden()) continue;

                final String colour = hasDiscovered
                        ? plugin.getConfig().getString("settings.server.discoveries.discovered.name-colour")
                        : plugin.getConfig().getString("settings.server.discoveries.undiscovered.name-colour");
                List<Component> lore = account.getDiscoveries().containsKey(configuredRegion.getId())
                        ? Translations.DISCOVERED_ON.get(player,
                        plugin.getDateFormatter().format(account.getDiscoveries().get(configuredRegion.getId()).getDate()))
                        : null;
                List<Component> coordsLore = configuredRegion.showCoords()
                        && player.hasPermission("rpgregions.showloc")
                        ? Translations.COORDINATES.get(player, configuredRegion.getLocation().getBlockX(), configuredRegion.getLocation().getBlockZ())
                        : null;
                List<Component> translatedHint = new ArrayList<>();
                if (configuredRegion.getHints() != null) {
                    configuredRegion.getHints().forEach(hint -> translatedHint.add(plugin.miniMessage().deserialize(hint)));
                }

                List<Component> hint = configuredRegion.showHint()
                        && player.hasPermission("rpgregions.showhint." + configuredRegion.getId()) || player.hasPermission("rpgregions.showhint.*")
                        && !hasDiscovered
                        ? translatedHint
                        : null;
                List<Component> teleport = configuredRegion.isTeleportable()
                        && player.hasPermission("rpgregions.teleport")
                        && hasDiscovered
                        ? account.getCooldowns().contains(RPGRegionsAccount.AccountCooldown.TELEPORT) ? Translations.TELEPORT_COOLDOWN.get(player) : Translations.TELEPORT.get(player)
                        : null;
                List<Component> requirementLore = new ArrayList<>();
                boolean requirements = true;
                if (!player.hasPermission("rpgregions.bypassentry") && configuredRegion.getRequirements() != null) {
                    for (RegionRequirement requirement : configuredRegion.getRequirements()) {
                        boolean meets = requirement.meetsRequirements(player);
                        if (requirement instanceof DependencyRequirement dependencyRequirement) {
                            List<String> discoveries = new ArrayList<>(account.getDiscoveries().keySet());
                            meets = dependencyRequirement.meetsRequirements(discoveries);
                        }
                        if (!meets) {
                            requirements = false;
                            requirementLore.addAll(Translations.REQUIREMENT_NOT_MET.get(player, requirement.getText(player)));
                        } else {
                            requirementLore.addAll(Translations.REQUIREMENT_MET.get(player, requirement.getText(player)));
                        }
                    }
                }

                List<Component> translatedDiscoveredLore = new ArrayList<>();
                if (configuredRegion.getDiscoveredLore() != null) {
                    configuredRegion.getDiscoveredLore().forEach(discoveredLore -> translatedDiscoveredLore.add(Colors.colourModern(discoveredLore)));
                }
                ItemStack item = hasDiscovered
                        ?
                        new ItemStackBuilder(configuredRegion.getIcon())
                                .lore(translatedDiscoveredLore)
                                .withModel(configuredRegion.getIconModel())
                                .build()
                        :
                        new ItemStackBuilder(configuredRegion.getUndiscoveredIcon())
                                .withModel(configuredRegion.getIconModel())
                                .build();

                boolean finalRequirements = requirements;
                double teleportCost = configuredRegion.getTeleportCost(player);
                List<Component> ecoLore = getEcoOrNull() == null ? List.of() : Translations.TELEPORT_COST.get(player, teleportCost, getEcoOrNull().currencyNamePlural());
                items.add(new GuiItem(new ItemStackBuilder(item)
                        .withName(plugin.miniMessage().deserialize(colour + configuredRegion.getCustomName()))
                        .lore(lore)
                        .lore(coordsLore)
                        .lore(hint)
                        .withLore(!requirementLore.isEmpty() || teleport != null || !ecoLore.isEmpty() ? " " : null)
                        .lore(requirementLore)
                        .lore(teleport)
                        .lore(ecoLore)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build(),
                        event -> {
                            event.setCancelled(true);
                            if (configuredRegion.isTeleportable()
                                    && player.hasPermission("rpgregions.teleport")
                                    && hasDiscovered) {
                                if (!account.getCooldowns().contains(RPGRegionsAccount.AccountCooldown.TELEPORT)) {
                                    if (configuredRegion.getWorld() == null || !finalRequirements) {
                                        Translations.CANNOT_TELEPORT.send(player);
                                    } else if (getEcoOrNull() != null && getEcoOrNull().getBalance(player) < teleportCost) {
                                        Translations.TELEPORT_NO_MONEY.send(player);
                                    } else {
                                        if (configuredRegion.getLocation() != null) {
                                            PaperLib.teleportAsync(player, configuredRegion.getLocation());
                                            if (getEcoOrNull() != null) getEcoOrNull().withdrawPlayer(player, teleportCost);
                                        } else player.sendMessage(ChatColor.RED + "Unable to find teleport location.");
                                        if (configuredRegion.getTeleportCooldown() != 0) {
                                            account.getCooldowns().add(RPGRegionsAccount.AccountCooldown.TELEPORT);
                                            plugin.getScheduler().executeDelayed(() -> {
                                                account.getCooldowns().remove(RPGRegionsAccount.AccountCooldown.TELEPORT);
                                            }, configuredRegion.getTeleportCooldown());
                                        }
                                    }
                                } else {
                                    Translations.COOLDOWN.send(player);
                                }
                            }

                            if (!configuredRegion.getIconCommand().isEmpty()) {
                                configuredRegion.getIconCommand().forEach(iconCommand -> {
                                    if (iconCommand.clickType() != IconCommand.CommandClickType.DISCOVERED && hasDiscovered
                                            || iconCommand.clickType() != IconCommand.CommandClickType.UNDISCOVERED && !hasDiscovered) {
                                        return;
                                    }

                                    if (account.getCooldowns().contains(RPGRegionsAccount.AccountCooldown.ICON_COMMAND)) {
                                        Translations.COOLDOWN.send(player);
                                        return;
                                    }

                                    final String command = iconCommand.command().replace("%region%", configuredRegion.getId())
                                            .replace("%player%", player.getName());
                                    if (iconCommand.console()) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                    } else {
                                        player.performCommand(command);
                                    }

                                    if (iconCommand.cooldown() != 0) {
                                        account.getCooldowns().add(RPGRegionsAccount.AccountCooldown.ICON_COMMAND);
                                        plugin.getScheduler().executeDelayed(() -> {
                                            account.getCooldowns().remove(RPGRegionsAccount.AccountCooldown.ICON_COMMAND);
                                        }, iconCommand.cooldown());
                                    }
                                });
                            }
                        }));
            }
            pane.populateWithGuiItems(items);
            gui.addPane(pane);
            gui.update();
        }, (t) -> Bukkit.getScheduler().runTask(plugin, t)).exceptionally(error -> {
            plugin.getLogger().warning("There was an error whilst listing regions");
            error.printStackTrace();
            return null;
        });
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
