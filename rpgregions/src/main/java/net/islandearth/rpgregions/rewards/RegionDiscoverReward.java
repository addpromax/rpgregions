package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.events.RegionDiscoverEvent;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.data.region.WorldDiscovery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Optional;

public class RegionDiscoverReward extends DiscoveryReward {

    @GuiEditable("Region")
    private String region;

    public RegionDiscoverReward(IRPGRegionsAPI api) {
        super(api);
    }

    @Override
    public String getName() {
        return "Region Discovery";
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        IRPGRegionsAPI api = RPGRegionsAPI.getAPI();
        final Optional<ConfiguredRegion> region = api.getManagers().getRegionsCache().getConfiguredRegion(this.region);
        if (region.isEmpty()) {
            api.getLogger().warning("Unable to find region '" + region + "' for discover reward.");
            return;
        }

        LocalDateTime date = LocalDateTime.now();
        final WorldDiscovery worldDiscovery = new WorldDiscovery(date, this.region);
        account.addDiscovery(worldDiscovery);
        Bukkit.getPluginManager().callEvent(new RegionDiscoverEvent(player, account, region.get(), worldDiscovery));
        this.updateAwardTime(account);
    }
}
