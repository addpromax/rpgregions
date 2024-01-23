package net.islandearth.rpgregions.api.integrations.ultraregions;

import me.TechsCode.UltraRegions.UltraRegions;
import me.TechsCode.UltraRegions.selection.XYZ;
import me.TechsCode.UltraRegions.storage.ManagedWorld;
import me.TechsCode.UltraRegions.storage.Region;
import me.TechsCode.UltraRegions.storage.RegionQuery;
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
import java.util.stream.Collectors;

public class UltraRegionsIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;

    public UltraRegionsIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInRegion(Location location) {
        return !this.getProtectedRegions(location).isEmpty();
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        Player player = pme.getPlayer();
        double oldX = pme.getFrom().getX();
        double oldY = pme.getFrom().getY();
        double oldZ = pme.getFrom().getZ();
        if (pme.getTo() == null) return;
        double x = pme.getTo().getX();
        double y = pme.getTo().getY();
        double z = pme.getTo().getZ();
        List<Region> oldRegions = this.getProtectedRegions(new Location(player.getWorld(), oldX, oldY, oldZ));
        List<Region> regions = this.getProtectedRegions(new Location(player.getWorld(), x, y, z));

        getPrioritisedRegion(pme.getTo()).ifPresent(prioritisedRegion -> {
            List<String> stringRegions = new ArrayList<>();
            regions.forEach(region -> {
                if (!prioritisedRegion.getId().equals(region.getName())
                        && checkRequirements(pme, prioritisedRegion, region.getName())) stringRegions.add(region.getName());
            });

            if (checkRequirements(pme, prioritisedRegion, prioritisedRegion.getId())) {
                plugin.debug("Requirements passed, calling");
                stringRegions.add(0, prioritisedRegion.getId());
                Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, stringRegions, !oldRegions.equals(regions)));
            }
        });
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        List<Region> regions = this.getProtectedRegions(location);
        if (regions.isEmpty()) return Optional.empty();
        return plugin.getManagers().getRegionsCache().getConfiguredRegion(regions.get(0).getName());
    }

    @Override
    public boolean exists(World location, String region) {
        Optional<ManagedWorld> world = UltraRegions.getAPI().getWorlds().find(location);
        if (world.isPresent()) {
            List<Region> regions = UltraRegions.getAPI().newRegionQuery(world.get()).getRegions().stream()
                    .filter(region1 -> !region1.getName().equals("Global") && region1.getName().equals(region))
                    .toList();
            return !regions.isEmpty();
        }
        return false;
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        Optional<ManagedWorld> managedWorld = UltraRegions.getAPI().getWorlds().find(world);
        Set<String> regions = new HashSet<>();
        if (managedWorld.isPresent()) {
            for (Region region : UltraRegions.getAPI().newRegionQuery(managedWorld.get()).getRegions()) {
                regions.add(region.getName());
            }
        }
        return regions;
    }

    @Override
    public @NonNull List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        List<Location> points = new ArrayList<>();
        final Optional<ManagedWorld> managedWorld = UltraRegions.getAPI().getWorlds().find(region.getWorld());
        if (managedWorld.isEmpty()) {
            return points;
        }

        for (Region ultraRegion : managedWorld.get().getRegions()) {
            if (!ultraRegion.getName().equals(region.getId())) continue;
            for (XYZ point : ultraRegion.getSelectionList().getTracePoints()) {
                points.add(new Location(region.getWorld(), point.getX(), point.getY(), point.getZ()));
            }
        }
        return points;
    }

    private List<Region> getProtectedRegions(Location location) {
        Optional<ManagedWorld> world = UltraRegions.getAPI().getWorlds().find(location.getWorld());
        if (world.isPresent()) {
            RegionQuery query = UltraRegions.getAPI().newRegionQuery(world.get()).location(new XYZ(location.getX(), location.getY(), location.getZ()));
            return query.getRegions().stream().filter(region -> !region.getName().equals("Global")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
