package net.islandearth.rpgregions.managers;

import com.djrapitops.plan.extension.ExtensionService;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.integrations.hooks.PlanRegionHook;

public class PlanRegistryManager {

    public PlanRegistryManager(RPGRegions plugin) {
        try {
            PlanRegionHook planRegionHook = new PlanRegionHook(plugin);
            ExtensionService.getInstance().register(planRegionHook);
        } catch (NoClassDefFoundError | IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
