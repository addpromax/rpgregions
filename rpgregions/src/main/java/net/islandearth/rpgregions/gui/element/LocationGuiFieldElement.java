package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.gui.IGuiEditable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocationGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        try {
            FieldUtils.writeField(field, guiEditable, player.getLocation());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(Location.class);
    }

    @Override
    public boolean needsValue() {
        return true;
    }
}
