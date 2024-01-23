package net.islandearth.rpgregions.commands.caption;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class RPGRegionsCaptionRegistryFactory<C> {

    /**
     * Create a new RPGRegions caption registry instance
     *
     * @return Created instance
     */
    public @NonNull RPGRegionsCaptionRegistry<C> create() {
        return new RPGRegionsCaptionRegistry<>();
    }

}
