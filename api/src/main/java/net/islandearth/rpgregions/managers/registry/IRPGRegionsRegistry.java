package net.islandearth.rpgregions.managers.registry;

import com.google.common.collect.ImmutableMap;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IRPGRegionsRegistry<T> {

    @NotNull
    ImmutableMap<String, Class<? extends T>> get();

    /**
     * Attempts to register a class.
     * @param clazz class to register
     * @throws IllegalArgumentException if class is already registered
     */
    void register(Class<? extends T> clazz);

    @Nullable
    T getNew(String name, IRPGRegionsAPI plugin, Object... data);

    @Nullable
    T getNew(Class<? extends T> clazz, IRPGRegionsAPI plugin, Object... data);

    String getRegistryName();

    Class<T> getImplementation();

    Material getIcon();

    List<String> getDescription();
}
