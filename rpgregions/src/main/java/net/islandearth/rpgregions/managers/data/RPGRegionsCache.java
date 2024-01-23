package net.islandearth.rpgregions.managers.data;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RPGRegionsCache implements IRPGRegionsCache {

    private final RPGRegions plugin;
    private final Map<String, ConfiguredRegion> configuredRegions = new ConcurrentHashMap<>();

    public RPGRegionsCache(RPGRegions plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<ConfiguredRegion> getConfiguredRegion(String id) {
        return Optional.ofNullable(configuredRegions.get(id));
    }

    @Override
    public void addConfiguredRegion(ConfiguredRegion region) {
        configuredRegions.put(region.getId(), region);
    }

    @Override
    public void removeConfiguredRegion(String id) {
        configuredRegions.remove(id);
    }

    @Override
    public Map<String, ConfiguredRegion> getConfiguredRegions() {
        return Map.copyOf(configuredRegions);
    }

    @Override
    public void clear() {
        configuredRegions.clear();
    }

    @Override
    public CompletableFuture<Boolean> saveAll(boolean async) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (async) {
            plugin.getScheduler().executeAsync(() -> {
                configuredRegions.forEach((id, region) -> region.save(plugin));
                future.complete(true);
            });
        } else {
            configuredRegions.forEach((id, region) -> region.save(plugin));
            future.complete(true);
        }
        return future;
    }
}
