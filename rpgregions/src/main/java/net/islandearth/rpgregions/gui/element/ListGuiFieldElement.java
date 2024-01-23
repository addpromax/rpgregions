package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.gui.IGuiEditable;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        try {
            Object object = field.get(guiEditable);
            if (object == null) object = new ArrayList<>();
            if (object instanceof ArrayList list) {
                list.add(String.valueOf(value));
                field.set(guiEditable, object);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<Class<?>> getType() {
        return List.of(List.class, ArrayList.class, AbstractList.class);
    }

    @Override
    public boolean needsValue() {
        return true;
    }
}
