package net.islandearth.rpgregions.api.integrations.rpgregions.region;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CuboidRegion extends RPGRegionsRegion {

    public CuboidRegion(final String name, final World world) {
        super(name, world);
    }

    @Nullable
    public Location getFirstCorner() {
        return getPoints().isEmpty() ? null : getPoints().get(0);
    }

    @Nullable
    public Location getSecondCorner() {
        return getPoints().size() < 2 ? null : getPoints().get(1);
    }

    @Override
    public boolean addPoint(final Location location) {
        if (this.getPoints().size() == 2) return false;
        return super.addPoint(location);
    }

    @Override
    public boolean isWithinBounds(Location location) {
        if (!this.isWithinWorldBounds(location.getWorld())) return false;
        final List<Location> points = getPoints();
        if (points.size() != 2) return false;
        Location first = getFirstCorner();
        Location second = getSecondCorner();
        final double x1 = first.getX();
        final double x2 = second.getX();
        final double y1 = first.getY();
        final double y2 = second.getY();
        final double z1 = first.getZ();
        final double z2 = second.getZ();
        Vector min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        Vector max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        return location.toVector().isInAABB(min, max);
    }

    @Override
    public void visualise(Player player) {
        final Location firstCorner = getFirstCorner();
        final Location secondCorner = getSecondCorner();
        if (firstCorner == null || secondCorner == null) {
            player.sendMessage(ChatColor.RED + "This region is incomplete, so only the existing corners shall be visualised.");
            if (firstCorner != null) player.sendBlockChange(firstCorner, Material.YELLOW_STAINED_GLASS.createBlockData());
            if (secondCorner != null) player.sendBlockChange(secondCorner, Material.YELLOW_STAINED_GLASS.createBlockData());
            return;
        }

        for (Location location : getHollowCube(firstCorner, secondCorner)) {
            player.sendBlockChange(location, Material.GREEN_STAINED_GLASS.createBlockData());
        }
    }

    public List<Location> getHollowCube(Location corner1, Location corner2) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x += 1) {
            for (double y = minY; y <= maxY; y += 1) {
                for (double z = minZ; z <= maxZ; z += 1) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }
}
