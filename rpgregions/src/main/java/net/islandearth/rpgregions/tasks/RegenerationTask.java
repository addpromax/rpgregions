package net.islandearth.rpgregions.tasks;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.utils.RegenUtils;

public class RegenerationTask implements Runnable {

    private final RPGRegions plugin;
    private final ConfiguredRegion region;

    public RegenerationTask(RPGRegions plugin, ConfiguredRegion region) {
        this.plugin = plugin;
        this.region = region;
    }

    @Override
    public void run() {
        RegenUtils.regenerate(region);
    }
}
