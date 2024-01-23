package net.islandearth.rpgregions.api.integrations.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.lib.flowpowered.math.vector.Vector3i;
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
import java.util.UUID;

public class GriefDefenderIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;

    public GriefDefenderIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInRegion(Location location) {
        Claim claim = GriefDefender.getCore().getClaimAt(location);
        return claim != null && !claim.isWilderness();
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        Player player = pme.getPlayer();
        if (pme.getTo() == null) return;
        Optional<ConfiguredRegion> getPrioritisedRegion = getPrioritisedRegion(pme.getTo());
        if (!getPrioritisedRegion.isPresent()) return;
        Claim oldClaim = GriefDefender.getCore().getClaimAt(pme.getFrom());
        if (oldClaim == null || oldClaim.isWilderness()) return;
        Claim claim = GriefDefender.getCore().getClaimAt(pme.getTo());

        List<String> stringClaim = new ArrayList<>();
        if (!checkRequirements(pme, getPrioritisedRegion.get(), "" + claim.getUniqueId())) return;

        stringClaim.add("" + claim.getUniqueId());
        Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, stringClaim, !oldClaim.equals(claim)));
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        Claim claim = GriefDefender.getCore().getClaimAt(location);
        if (claim == null || claim.isWilderness()) return Optional.empty();
        return plugin.getManagers().getRegionsCache().getConfiguredRegion("" + claim.getUniqueId());
    }

    @Override
    public boolean exists(World world, String region) {
        final Claim claim = GriefDefender.getCore().getClaim(UUID.fromString(region));
        return claim != null && !claim.isWilderness();
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        Set<String> claimsString = new HashSet<>();
        if (!GriefDefender.getCore().isEnabled(world.getUID())) return claimsString;
        for (Claim claim : GriefDefender.getCore().getClaimManager(world.getUID()).getWorldClaims()) {
            claimsString.add("" + claim.getUniqueId());
        }
        return claimsString;
    }

    @Override
    public @NonNull List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        List<Location> points = new ArrayList<>();
        for (Claim claim : GriefDefender.getCore().getAllClaims()) {
            if (!claim.getUniqueId().toString().equals(region.getId())) continue;
            final Vector3i max = claim.getGreaterBoundaryCorner();
            final Vector3i min = claim.getLesserBoundaryCorner();
            World world = Bukkit.getWorld(claim.getWorldUniqueId());
            points.add(new Location(world, max.getX(), max.getY(), max.getZ()));
            points.add(new Location(world, min.getX(), min.getY(), min.getZ()));
            break;
        }
        return points;
    }
}
