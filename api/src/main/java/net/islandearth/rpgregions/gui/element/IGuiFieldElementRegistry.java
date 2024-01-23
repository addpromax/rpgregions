package net.islandearth.rpgregions.gui.element;

public interface IGuiFieldElementRegistry {

    void register(IGuiFieldElement guiFieldElement);

    void unregister(IGuiFieldElement guiFieldElement);

    IGuiFieldElement fromClass(Class<?> clazz);
}
