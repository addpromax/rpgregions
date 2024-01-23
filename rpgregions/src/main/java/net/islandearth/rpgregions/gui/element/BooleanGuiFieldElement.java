package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.gui.IGuiEditable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BooleanGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        try {
            Object currentValue = field.get(guiEditable);
            FieldUtils.writeField(field, guiEditable, !(Boolean) currentValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(Boolean.class, boolean.class);
    }

    @Override
    public boolean needsValue() {
        return false;
    }
}
