package net.islandearth.rpgregions.gui;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a {@link java.lang.reflect.Field} as an editable value in the editor GUI.
 * Only primitive values are supported at this time.
 * @see net.islandearth.rpgregions.gui.element.IGuiFieldElement
 * @see net.islandearth.rpgregions.gui.element.IGuiFieldElementRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GuiEditable {
    String value();

    GuiEditableType type() default GuiEditableType.DEFAULT;

    Material icon() default Material.WRITTEN_BOOK;

    enum GuiEditableType {
        CHAT,
        ANVIL,
        DEFAULT
    }
}
