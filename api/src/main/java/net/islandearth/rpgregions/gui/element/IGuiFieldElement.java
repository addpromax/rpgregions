package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.gui.IGuiEditable;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGuiFieldElement {

    CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value);

    List<Class<?>> getType();

    boolean needsValue();
}
