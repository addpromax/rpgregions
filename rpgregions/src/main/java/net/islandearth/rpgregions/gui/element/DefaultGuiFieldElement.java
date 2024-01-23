package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.gui.IGuiEditable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        Object casted = field.getType().cast(value);
        try {
            FieldUtils.writeField(field, guiEditable, casted);
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<Class<?>> getType() {
        return Collections.emptyList(); // Accept all as default
    }

    @Override
    public boolean needsValue() {
        return true;
    }
}
