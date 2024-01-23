package net.islandearth.rpgregions.requirements;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public final class RegionRequirementRegistry extends RPGRegionsRegistry<RegionRequirement> {

    @Override
    public @Nullable RegionRequirement getNew(Class<? extends RegionRequirement> clazz, IRPGRegionsAPI plugin, Object... data) {
        try {
            Constructor<?> constructor = clazz.getConstructor(IRPGRegionsAPI.class);
            RegionRequirement requirement = (RegionRequirement) constructor.newInstance(plugin);
            if (requirement.getPluginRequirement() != null
                    && Bukkit.getPluginManager().getPlugin(requirement.getPluginRequirement()) == null) {
                return null;
            }
            return requirement;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "Requirements";
    }

    @Override
    public Class<RegionRequirement> getImplementation() {
        return RegionRequirement.class;
    }

    @Override
    public Material getIcon() {
        return Material.COMPARATOR;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Requirements before a region can", "&7be entered by a player", "&e&lClick &7to edit region requirements.");
    }
}
