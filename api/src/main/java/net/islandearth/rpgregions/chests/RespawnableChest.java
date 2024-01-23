package net.islandearth.rpgregions.chests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RespawnableChest extends RegionChest {

    private final Location location;
    private final int respawnTime;
    private final List<ItemStack> items;

    public RespawnableChest(Location location, int respawnTime, List<ItemStack> items) {
        this.location = location;
        this.respawnTime = respawnTime;
        this.items = items;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    @Override
    public Location getChestLocation() {
        return location;
    }
}
