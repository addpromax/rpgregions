package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public final class RegionRewardRegistry extends RPGRegionsRegistry<DiscoveryReward> {

    @Override
    public @Nullable DiscoveryReward getNew(Class<? extends DiscoveryReward> clazz, IRPGRegionsAPI plugin, Object... data) {
        try {
            Constructor<?> constructor = clazz.getConstructor(IRPGRegionsAPI.class);
            DiscoveryReward reward = (DiscoveryReward) constructor.newInstance(plugin);
            if (reward.getPluginRequirement() != null
                    && Bukkit.getPluginManager().getPlugin(reward.getPluginRequirement()) == null) {
                return null;
            }
            return reward;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "Rewards";
    }

    @Override
    public Class<DiscoveryReward> getImplementation() {
        return DiscoveryReward.class;
    }

    @Override
    public Material getIcon() {
        return Material.CHEST;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Rewards are granted upon region discovery", "&e&lClick &7to edit region rewards.");
    }
}
