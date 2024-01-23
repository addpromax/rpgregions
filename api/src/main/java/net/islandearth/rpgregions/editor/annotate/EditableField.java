package net.islandearth.rpgregions.editor.annotate;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated in favour of {@link net.islandearth.rpgregions.gui.GuiEditable}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated(forRemoval = true)
public @interface EditableField {

    Material material() default Material.OAK_SIGN;

    String name();

    String description();
}
