package net.islandearth.rpgregions.api.integrations.rpgregions.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PolyRegion extends RPGRegionsRegion {

    public PolyRegion(final String name, final World world) {
        super(name, world);
    }

    @Override
    public boolean isWithinBounds(Location location) {
        if (!this.isWithinWorldBounds(location.getWorld())) return false;
        if (getPoints().size() < 2) return false;
        List<Integer> xPoints = new ArrayList<>();
        List<Integer> yPoints = new ArrayList<>();
        getPoints().forEach(point -> {
            xPoints.add(point.getBlockX());
            yPoints.add(point.getBlockZ());
        });
        Polygon polygon = new Polygon(xPoints.stream().mapToInt(Integer::intValue).toArray(), yPoints.stream().mapToInt(Integer::intValue).toArray(), xPoints.size());
        return polygon.contains(location.getX(), location.getZ());
    }

    @Override
    public void visualise(Player player) {
        //TODO make this better somehow?
        for (Location point : getPoints()) {
            player.sendBlockChange(point, Material.GREEN_STAINED_GLASS.createBlockData());
        }
    }
}
