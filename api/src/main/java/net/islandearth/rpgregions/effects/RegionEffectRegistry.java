package net.islandearth.rpgregions.effects;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public final class RegionEffectRegistry extends RPGRegionsRegistry<RegionEffect> {

    @Override
    public @Nullable RegionEffect getNew(Class<? extends RegionEffect> clazz, IRPGRegionsAPI plugin, Object... data) {
        try {
            try {
                Constructor<?> constructor = clazz.getConstructor(IRPGRegionsAPI.class);
                RegionEffect effect = (RegionEffect) constructor.newInstance(plugin);
                if (effect.getRequiredVersion() != null && !Bukkit.getVersion().contains(effect.getRequiredVersion())) {
                    return null;
                }
                return effect;
            } catch (NoClassDefFoundError e) {
                return null;
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "Effects";
    }

    @Override
    public Class<RegionEffect> getImplementation() {
        return RegionEffect.class;
    }

    @Override
    public Material getIcon() {
        return Material.WRITABLE_BOOK;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Effects that are present within a region.", "&e&lClick &7to edit region effects.");
    }
}
