package net.islandearth.rpgregions.api.integrations.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WorldGuardIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;

    public WorldGuardIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInRegion(Location location) {
        return !this.getProtectedRegions(location).isEmpty();
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        if (pme.getTo() == null) return;
        plugin.debug("Handling movement");
        Player player = pme.getPlayer();
        int oldX = pme.getFrom().getBlockX();
        int oldY = pme.getFrom().getBlockY();
        int oldZ = pme.getFrom().getBlockZ();
        int x = pme.getTo().getBlockX();
        int y = pme.getTo().getBlockY();
        int z = pme.getTo().getBlockZ();
        Set<ProtectedRegion> oldRegions = this.getProtectedRegions(new Location(player.getWorld(), oldX, oldY, oldZ));
        Set<ProtectedRegion> regions = this.getProtectedRegions(new Location(player.getWorld(), x, y, z));

        Optional<ConfiguredRegion> configuredRegion = getPrioritisedRegion(pme.getTo());
        configuredRegion.ifPresent(prioritisedRegion -> {
            plugin.debug("Priority region found");
            plugin.debug("Old: " + oldRegions);
            plugin.debug("New: " + regions);
            List<String> stringRegions = new ArrayList<>();
            regions.forEach(region -> {
                if (!prioritisedRegion.getId().equals(region.getId())
                        && checkRequirements(pme, prioritisedRegion, region.getId())) stringRegions.add(region.getId());
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
        Set<ProtectedRegion> regions = this.getProtectedRegions(location);
        ProtectedRegion highest = null;
        for (ProtectedRegion region : regions) {
            if (highest == null) {
                highest = region;
                continue;
            }

            if (region.getPriority() >= highest.getPriority()) {
                highest = region;
            }
        }

        if (highest == null) return Optional.empty();
        return plugin.getManagers().getRegionsCache().getConfiguredRegion(highest.getId());
    }

    @Override
    public boolean exists(World world, String region) {
        return WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world))
                .getRegions().containsKey(region);
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        return WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world))
                .getRegions().keySet();
    }

    @Override
    @NonNull
    public List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        List<Location> points = new ArrayList<>();
        final Map<String, ProtectedRegion> regions = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(region.getWorld()))
                .getRegions();
        final ProtectedRegion protectedRegion = regions.get(region.getId());
        for (BlockVector2 point : protectedRegion.getPoints()) {
            points.add(new Location(region.getWorld(), point.getX(), region.getLocation().getY(), point.getZ()));
        }
        return points;
    }

    private Set<ProtectedRegion> getProtectedRegions(Location location) {
        return WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld()))
                .getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                .getRegions();
    }
}
