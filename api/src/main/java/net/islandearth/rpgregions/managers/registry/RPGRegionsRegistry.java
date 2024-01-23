package net.islandearth.rpgregions.managers.registry;

import com.google.common.collect.ImmutableMap;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RPGRegionsRegistry<T> implements IRPGRegionsRegistry<T> {

    private final Map<String, Class<? extends T>> registeredClasses;

    protected RPGRegionsRegistry() {
        this.registeredClasses = new ConcurrentHashMap<>();
    }

    @NotNull
    protected Map<String, Class<? extends T>> getRegisteredClasses() {
        return registeredClasses;
    }

    @NotNull
    @Override
    public ImmutableMap<String, Class<? extends T>> get() {
        return ImmutableMap.copyOf(registeredClasses);
    }

    @Override
    public void register(Class<? extends T> clazz) {
        if (registeredClasses.containsKey(clazz.getSimpleName()))
            throw new IllegalArgumentException(clazz.getSimpleName() + " is already registered!");
        registeredClasses.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    @Override
    public T getNew(String name, IRPGRegionsAPI plugin, Object... data) {
        return getNew(registeredClasses.get(name), plugin, data);
    }

    @Nullable
    @Override
    public abstract T getNew(Class<? extends T> clazz, IRPGRegionsAPI plugin, Object... data);

    @Override
    public abstract String getRegistryName();

    @Override
    public abstract Class<T> getImplementation();

    @Override
    public abstract Material getIcon();

    @Override
    public List<String> getDescription() {
        return List.of("");
    }
}
