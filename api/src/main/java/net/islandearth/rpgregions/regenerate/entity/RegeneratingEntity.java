package net.islandearth.rpgregions.regenerate.entity;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.List;

public final class RegeneratingEntity {

    private final String entityType;
    private final List<Material> validSpawnSurfaces;
    private final int maxPerChunk;
    private final int rarity;
    private final boolean isMythicEntity;

    public RegeneratingEntity(String entityType, List<Material> validSpawnSurfaces, int maxPerChunk, int rarity) {
        this.entityType = entityType;
        this.validSpawnSurfaces = validSpawnSurfaces;
        this.maxPerChunk = maxPerChunk;
        this.rarity = rarity;
        this.isMythicEntity = false;
    }

    public RegeneratingEntity(EntityType entityType, List<Material> validSpawnSurfaces, int maxPerChunk, int rarity) {
        this(entityType.toString(), validSpawnSurfaces, maxPerChunk, rarity);
    }

    public String getEntity() {
        return entityType;
    }

    public boolean isMythicEntity() {
        return isMythicEntity;
    }

    public List<Material> getValidSpawnSurfaces() {
        return validSpawnSurfaces;
    }

    public int getMaxPerChunk() {
        return maxPerChunk;
    }

    public int getRarity() {
        return rarity;
    }
}
