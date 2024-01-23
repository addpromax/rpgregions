package net.islandearth.rpgregions.api.integrations.griefprevention;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GriefPreventionIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;

    public GriefPreventionIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInRegion(Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        return claim != null;
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        Player player = pme.getPlayer();
        Claim oldClaim = GriefPrevention.instance.dataStore.getClaimAt(pme.getFrom(), false, null);
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(pme.getTo(), false, null);
        if (oldClaim == null || claim == null) return;

        List<String> stringClaim = new ArrayList<>();
        if (!checkRequirements(pme, getPrioritisedRegion(pme.getTo()).get(), "" + claim.getID())) return;

        stringClaim.add("" + claim.getID());
        Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, stringClaim, !oldClaim.equals(claim)));
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        return plugin.getManagers().getRegionsCache().getConfiguredRegion("" + claim.getID());
    }

    @Override
    public boolean exists(World location, String region) {
        Claim claim = GriefPrevention.instance.dataStore.getClaim(Long.parseLong(region));
        return claim != null;
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        Set<String> claims = new HashSet<>();
        for (Claim claim : GriefPrevention.instance.dataStore.getClaims()) {
            claims.add("" + claim.getID());
        }
        return claims;
    }

    @Override
    public @NonNull List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        List<Location> points = new ArrayList<>();
        Claim claim = GriefPrevention.instance.dataStore.getClaim(Long.parseLong(region.getId()));
        if (claim == null) {
            return points;
        }

        points.add(claim.getGreaterBoundaryCorner());
        points.add(claim.getLesserBoundaryCorner());
        return points;
    }
}
