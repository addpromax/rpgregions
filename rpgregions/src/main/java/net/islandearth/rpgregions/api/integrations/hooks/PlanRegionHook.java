package net.islandearth.rpgregions.api.integrations.hooks;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.annotation.NumberProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@PluginInfo(name = "RPGRegions", iconName = "map", iconFamily = Family.SOLID, color = Color.BLUE)
public class PlanRegionHook implements DataExtension {

    private final RPGRegions plugin;

    public PlanRegionHook(RPGRegions plugin) {
        this.plugin = plugin;
    }

    @NumberProvider(
            text = "Regions discovered",
            description = "How many regions the player has discovered",
            priority = 4,
            iconFamily = Family.SOLID,
            iconColor = Color.NONE,
            format = FormatType.NONE
    )
    public long regionCount(UUID playerUUID) throws InterruptedException, ExecutionException {
        // Have to do blocking operation :(
        RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(playerUUID).get();
        return account.getDiscoveries().size();
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE,
                CallEvents.SERVER_EXTENSION_REGISTER
        };
    }
}
