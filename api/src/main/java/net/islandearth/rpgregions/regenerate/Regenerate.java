package net.islandearth.rpgregions.regenerate;

import net.islandearth.rpgregions.regenerate.entity.RegeneratingEntity;
import org.bukkit.Location;

import java.util.List;

public class Regenerate {

    private String schematicName;
    private Location origin;
    private final int regenerateInterval;
    private final boolean loadChunks;
    private final List<RegeneratingEntity> regeneratingEntities;
    private final boolean onDiscover;
    private final boolean onlyEntities;

    public Regenerate(int regenerateInterval, boolean loadChunks, List<RegeneratingEntity> regeneratingEntities) {
        this.regenerateInterval = regenerateInterval;
        this.loadChunks = loadChunks;
        this.regeneratingEntities = regeneratingEntities;
        this.onDiscover = false;
        this.onlyEntities = true;
    }

    public int getRegenerateInterval() {
        return regenerateInterval;
    }

    public boolean isLoadChunks() {
        return loadChunks;
    }

    public List<RegeneratingEntity> getRegeneratingEntities() {
        return regeneratingEntities;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public boolean isOnDiscover() {
        return onDiscover;
    }

    public boolean isOnlyEntities() {
        return onlyEntities;
    }
}
