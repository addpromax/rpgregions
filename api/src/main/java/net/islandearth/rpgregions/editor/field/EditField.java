package net.islandearth.rpgregions.editor.field;

import net.islandearth.rpgregions.editor.annotate.EditableField;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @deprecated in favour of {@link net.islandearth.rpgregions.gui.GuiEditable}
 */
@Deprecated
public class EditField {

    private final ConfiguredRegion clazz;
    private final Field field;
    private final Material material;
    private final String name;
    private final String description;

    public EditField(ConfiguredRegion clazz, Field field) {
        field.setAccessible(true);
        this.clazz = clazz;
        this.field = field;
        EditableField editableField = field.getAnnotation(EditableField.class);
        this.name = editableField.name();
        this.material = editableField.material();
        this.description = editableField.description();
    }

    public Field getField() {
        return field;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        try {
            return this.field.get(clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String setValue(Object value) {
        if (Modifier.isTransient(field.getModifiers())) {
            return ChatColor.RED + "Field " + field.getName() + " is a non-modifiable field.";
        }

        try {
            if (getValue().getClass().isEnum()) {
                field.set(clazz, Enum.valueOf((Class<Enum>) field.getType(), String.valueOf(value)));
            } else if (getValue() instanceof Boolean) {
                field.setBoolean(clazz, (boolean) value);
            } else {
                try {
                    field.set(clazz, Integer.valueOf(String.valueOf(value)));
                } catch (NumberFormatException e2) {
                    try {
                        field.set(clazz, Double.valueOf(String.valueOf(value)));
                    } catch (NumberFormatException e3) {
                        field.set(clazz, field.getType().cast(value));
                    }
                }
            }

            return ChatColor.GREEN + "Field " + field.getName() + " has been updated with new value " + value;
        } catch (ReflectiveOperationException e) {
            return ChatColor.RED + "Could not set field: " + e.getMessage();
        }
    }
}
