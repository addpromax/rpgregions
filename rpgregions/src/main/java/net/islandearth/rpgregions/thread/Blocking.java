package net.islandearth.rpgregions.thread;

import net.islandearth.rpgregions.RPGRegions;

/**
 * Used to indicate a class has main-thread blocking code.
 */
public interface Blocking {

    default void log(RPGRegions plugin) {
        plugin.getLogger().warning("This class (" + this.getClass().getSimpleName() + ") has main-thread blocking code.");
    }
}
