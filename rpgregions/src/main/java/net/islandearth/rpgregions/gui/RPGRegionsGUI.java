package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    protected final int backX;
    protected final int backY;
    protected final int backL;
    protected final int backH;
    protected final int forwardX;
    protected final int forwardY;
    protected final int forwardL;
    protected final int forwardH;
    protected final int exitX;
    protected final int exitY;
    protected final int exitL;
    protected final int exitH;
    protected final int paneX;
    protected final int paneY;
    protected final int paneL;
    protected final int paneH;
    protected final int oPaneX;
    protected final int oPaneY;
    protected final int oPaneL;
    protected final int oPaneH;
    protected final int iPaneX;
    protected final int iPaneY;
    protected final int iPaneL;
    protected final int iPaneH;

    protected RPGRegionsGUI(RPGRegions plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        // I wish there was a cleaner way to do this :(
        this.backX = plugin.getConfig().getInt("settings.server.gui.back.posX");
        this.backY = plugin.getConfig().getInt("settings.server.gui.back.posY");
        this.backL = plugin.getConfig().getInt("settings.server.gui.back.length");
        this.backH = plugin.getConfig().getInt("settings.server.gui.back.height");
        this.forwardX = plugin.getConfig().getInt("settings.server.gui.forward.posX");
        this.forwardY = plugin.getConfig().getInt("settings.server.gui.forward.posY");
        this.forwardL = plugin.getConfig().getInt("settings.server.gui.forward.length");
        this.forwardH = plugin.getConfig().getInt("settings.server.gui.forward.height");
        this.exitX = plugin.getConfig().getInt("settings.server.gui.exit.posX");
        this.exitY = plugin.getConfig().getInt("settings.server.gui.exit.posY");
        this.exitL = plugin.getConfig().getInt("settings.server.gui.exit.length");
        this.exitH = plugin.getConfig().getInt("settings.server.gui.exit.height");
        this.paneX = plugin.getConfig().getInt("settings.server.gui.pane.posX");
        this.paneY = plugin.getConfig().getInt("settings.server.gui.pane.posY");
        this.paneL = plugin.getConfig().getInt("settings.server.gui.pane.length");
        this.paneH = plugin.getConfig().getInt("settings.server.gui.pane.height");
        this.oPaneX = plugin.getConfig().getInt("settings.server.gui.outlinePane.posX");
        this.oPaneY = plugin.getConfig().getInt("settings.server.gui.outlinePane.posY");
        this.oPaneL = plugin.getConfig().getInt("settings.server.gui.outlinePane.length");
        this.oPaneH = plugin.getConfig().getInt("settings.server.gui.outlinePane.height");
        this.iPaneX = plugin.getConfig().getInt("settings.server.gui.innerPane.posX");
        this.iPaneY = plugin.getConfig().getInt("settings.server.gui.innerPane.posY");
        this.iPaneL = plugin.getConfig().getInt("settings.server.gui.innerPane.length");
        this.iPaneH = plugin.getConfig().getInt("settings.server.gui.innerPane.height");
    }

    public RPGRegions getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void render();

    public PaginatedPane generateDefaultConfig() {
        ChestGui gui = getGui();

        gui.setOnGlobalClick(click -> click.setCancelled(true));

        PaginatedPane pane = new PaginatedPane(paneX, paneY, paneL, paneH);
        OutlinePane oPane = new OutlinePane(oPaneX, oPaneY, oPaneL, oPaneH);
        OutlinePane innerPane = new OutlinePane(iPaneX, iPaneY, iPaneL, iPaneH);
        StaticPane back = new StaticPane(backX, backY, backL, backH);
        StaticPane forward = new StaticPane(forwardX, forwardY, forwardL, forwardH);
        StaticPane exit = new StaticPane(exitX, exitY, exitL, exitH);

        pane.setPriority(Pane.Priority.HIGHEST);
        back.setPriority(Pane.Priority.HIGH);
        forward.setPriority(Pane.Priority.HIGH);
        exit.setPriority(Pane.Priority.HIGH);

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
        Material bm = Material.valueOf(plugin.getConfig().getString("settings.server.gui.back.back"));
        ItemStack backItem = new ItemStackBuilder(bm)
                .withModel(plugin.getConfig().getInt("settings.server.gui.back.model"))
                .withName(Translations.PREVIOUS_PAGE.get(player).get(0))
                .lore(Translations.PREVIOUS_PAGE_LORE.get(player))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        back.addItem(new GuiItem(backItem, event -> {
            event.setCancelled(true);
            if (pane.getPage() == 0) return;

            pane.setPage(pane.getPage() - 1);

            forward.setVisible(true);
            gui.update();
        }), 0, 0);

        // Forward item
        Material fm = Material.valueOf(plugin.getConfig().getString("settings.server.gui.forward.forward"));
        ItemStack forwardItem = new ItemStackBuilder(fm)
                .withModel(plugin.getConfig().getInt("settings.server.gui.forward.model"))
                .withName(Translations.NEXT_PAGE.get(player).get(0))
                .lore(Translations.NEXT_PAGE_LORE.get(player))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        forward.addItem(new GuiItem(forwardItem, event -> {
            event.setCancelled(true);
            if (pane.getPages() == 0 || pane.getPages() == 1) return;

            pane.setPage(pane.getPage() + 1);

            back.setVisible(true);
            gui.update();
        }), 0, 0);

        // Exit item
        Material em = Material.valueOf(plugin.getConfig().getString("settings.server.gui.exit.exit"));
        ItemStack item = new ItemStackBuilder(em)
                .withModel(plugin.getConfig().getInt("settings.server.gui.exit.model"))
                .withName(Translations.EXIT.get(player).get(0))
                .lore(Translations.EXIT_LORE.get(player))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        exit.addItem(new GuiItem(item, event -> {
            gui.update();
            player.closeInventory();
        }), 0, 0);

        gui.addPane(exit);
        gui.addPane(back);
        gui.addPane(forward);
        gui.addPane(pane);

        return pane;
    }

    public abstract ChestGui getGui();

    public void open() {
        player.closeInventory();
        render();
        getGui().show(player);
    }
}
