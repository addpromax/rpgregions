package net.islandearth.rpgregions.api.integrations.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.events.RegionsEnterEvent;
import net.islandearth.rpgregions.api.integrations.IntegrationManager;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResidenceIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;

    public ResidenceIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInRegion(Location location) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(location);
        return res != null;
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        Player player = pme.getPlayer();
        ClaimedResidence oldResidence = Residence.getInstance().getResidenceManager().getByLoc(pme.getFrom());
        ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(pme.getTo());
        if (oldResidence == null || residence == null) return;
        if (!checkRequirements(pme, getPrioritisedRegion(pme.getTo()).get(), residence.getName())) return;

        Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, residence.getName(), !oldResidence.equals(residence)));
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(location);
        return plugin.getManagers().getRegionsCache().getConfiguredRegion(res.getName());
    }

    @Override
    public boolean exists(World location, String region) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByName(region);
        return res != null;
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        return Residence.getInstance().getResidenceManager().getResidences().keySet();
    }

    @Override
    public @NonNull List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        return new ArrayList<>();
    }
}
