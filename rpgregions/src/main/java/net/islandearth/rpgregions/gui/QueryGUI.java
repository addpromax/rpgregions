package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.registry.IRPGRegionsRegistry;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueryGUI extends RPGRegionsGUI {

    private final List<GuiItem> items;
    private ChestGui gui;

    public QueryGUI(RPGRegions plugin, Player player, ConfiguredRegion region, IRPGRegionsRegistry<?> registry) {
        super(plugin, player);
        this.items = new ArrayList<>();
        ItemStack add = new ItemStackBuilder(Material.WRITABLE_BOOK)
                .withName("&6Add " + registry.getRegistryName())
                .build();
        ItemStack edit = new ItemStackBuilder(Material.WRITTEN_BOOK)
                .withName("&6Edit " + registry.getRegistryName())
                .build();
        GuiItem addRegistryGuiItem = new GuiItem(add, click -> {
            new AddRegionElementGUI(plugin, player, region, registry).open();
            player.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
        });
        GuiItem editRegistryGuiItem = new GuiItem(edit, click -> {
            new EditRegionElementGUI(plugin, player, region, registry).open();
            player.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
        });
        items.add(addRegistryGuiItem);
        items.add(editRegistryGuiItem);
    }

    @Override
    public void render() {
        this.gui = new ChestGui(1, "What would you like to do?");
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        StaticPane options = new StaticPane(0, 0, 9, 1);
        int x = 0;
        for (GuiItem item : items) {
            options.addItem(item, x, 0);
            x++;
        }
        gui.addPane(options);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
