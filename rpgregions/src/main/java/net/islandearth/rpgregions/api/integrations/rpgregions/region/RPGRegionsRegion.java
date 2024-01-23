package net.islandearth.rpgregions.api.integrations.rpgregions.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class RPGRegionsRegion {

    private final String name;
    private UUID world;
    private final List<Location> points;
    private int priority;

    public RPGRegionsRegion(final String name, final World world) {
        this.name = name;
        this.world = world.getUID();
        this.points = new ArrayList<>();
        this.priority = 1;
    }

    public String getName() {
        return name;
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<Location> getPoints() {
        return points;
    }

    public boolean addPoint(final Location location) {
        return this.points.add(location);
    }

    public boolean removePoint(final Location location) {
        return this.points.remove(location);
    }

    public boolean isWithinBounds(final Player player) {
        return this.isWithinBounds(player.getLocation());
    }

    public boolean isWithinWorldBounds(final World world) {
        return this.world != null && this.world.equals(world.getUID());
    }

    public abstract boolean isWithinBounds(final Location location);

    public abstract void visualise(Player player);

    @Override
    public String toString() {
        return "{name=" + name + ", priority=" + priority + ", points=" + points + "}";
    }
}
