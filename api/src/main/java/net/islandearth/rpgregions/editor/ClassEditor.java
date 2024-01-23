package net.islandearth.rpgregions.editor;

import net.islandearth.rpgregions.editor.annotate.EditableField;
import net.islandearth.rpgregions.editor.field.EditField;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated in favour of {@link net.islandearth.rpgregions.gui.GuiEditable}
 */
@Deprecated
public class ClassEditor {
    
    private final List<EditField> editable = new ArrayList<>();

    public ClassEditor(ConfiguredRegion clazz) {
        for (Field declaredField : clazz.getClass().getDeclaredFields()) {
            if (declaredField.getAnnotation(EditableField.class) != null) {
                editable.add(new EditField(clazz, declaredField));
            }
        }
    }

    public List<EditField> getEditable() {
        return editable;
    }
}
