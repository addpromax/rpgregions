package net.islandearth.rpgregions.managers;

import net.islandearth.rpgregions.api.integrations.IntegrationManager;
import net.islandearth.rpgregions.gui.element.IGuiFieldElementRegistry;
import net.islandearth.rpgregions.managers.data.IRPGRegionsCache;
import net.islandearth.rpgregions.managers.data.IStorageManager;
import net.islandearth.rpgregions.managers.data.fauna.IFaunaCache;
import net.islandearth.rpgregions.managers.registry.IRPGRegionsRegistry;

public interface IRPGRegionsManagers {

    IStorageManager getStorageManager();

    IntegrationManager getIntegrationManager();

    IFaunaCache getFaunaCache();

    IRPGRegionsCache getRegionsCache();

    IRegenerationManager getRegenerationManager();

    IGuiFieldElementRegistry getGuiFieldElementRegistry();

    <T> IRPGRegionsRegistry<T> getRegistry(Class<? extends IRPGRegionsRegistry<T>> clazz);
}
