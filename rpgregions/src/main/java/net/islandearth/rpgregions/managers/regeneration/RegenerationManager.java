package net.islandearth.rpgregions.managers.regeneration;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.schedule.PlatformScheduler;
import net.islandearth.rpgregions.managers.IRegenerationManager;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.regenerate.Regenerate;
import net.islandearth.rpgregions.tasks.RegenerationTask;

import java.util.ArrayList;
import java.util.List;

public class RegenerationManager implements IRegenerationManager {

    private final RPGRegions plugin;
    private final List<PlatformScheduler.RPGRegionsTask> tasks;

    public RegenerationManager(RPGRegions plugin) {
        this.plugin = plugin;
        this.tasks = new ArrayList<>();
    }

    @Override
    public void reload() {
        tasks.forEach(PlatformScheduler.RPGRegionsTask::cancel);
        tasks.clear();
        for (ConfiguredRegion configuredRegion1 : plugin.getManagers().getRegionsCache().getConfiguredRegions().values()) {
            if (configuredRegion1.getRegenerate() != null) {
                Regenerate regenerate = configuredRegion1.getRegenerate();
                if (regenerate.isOnDiscover()) continue;
                if (regenerate.getRegenerateInterval() < 5000) plugin.getLogger().warning("Region " + configuredRegion1.getId() + " has a very low regenerate interval! This may lag your server.");
                tasks.add(plugin.getScheduler().executeRepeating(new RegenerationTask(plugin, configuredRegion1), 0L, regenerate.getRegenerateInterval()));
            }
        }
    }
}
