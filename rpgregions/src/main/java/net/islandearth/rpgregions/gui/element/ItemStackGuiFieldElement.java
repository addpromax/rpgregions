package net.islandearth.rpgregions.gui.element;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.islandearth.rpgregions.gui.IGuiEditable;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemStackGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        ChestGui gui = new ChestGui(1, "Inventory");
        StaticPane pane = new StaticPane(4, 0, 1, 1);
        ItemStack info = new ItemStackBuilder(Material.OAK_SIGN)
                .withName("&6Information")
                .withLore("&7Click on an item in your inventory below to set the value.",
                        "&7You are editing: " + field.getName() + ".")
                .build();
        pane.addItem(new GuiItem(info), 0, 0);
        gui.addPane(pane);
        gui.setOnGlobalClick(click -> {
            click.setCancelled(true);
            try {
                FieldUtils.writeField(field, guiEditable, click.getCurrentItem());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            completableFuture.complete(null);
        });
        gui.update();
        gui.show(player);
        player.sendMessage(ChatColor.GREEN + "Click on an item in your inventory.");
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(ItemStack.class);
    }

    @Override
    public boolean needsValue() {
        return false;
    }
}
