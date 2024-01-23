package net.islandearth.rpgregions.managers.data;

import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IRPGRegionsCache {

    Optional<ConfiguredRegion> getConfiguredRegion(String id);

    void addConfiguredRegion(ConfiguredRegion region);

    void removeConfiguredRegion(String id);

    Map<String, ConfiguredRegion> getConfiguredRegions();

    void clear();

    CompletableFuture<Boolean> saveAll(boolean async);
}
