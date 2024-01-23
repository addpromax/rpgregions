package net.islandearth.rpgregions.api.schedule;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.entity.Entity;

public abstract class PlatformScheduler<T extends IRPGRegionsAPI> {

    protected final T api;

    public PlatformScheduler(T api) {
        this.api = api;
    }

    public abstract void executeOnMain(Runnable runnable);

    public abstract void executeOnEntity(Entity entity, Runnable runnable);

    public abstract RPGRegionsTask executeRepeating(Runnable runnable, long delay, long period);

    public abstract void executeDelayed(Runnable runnable, long delay);

    public abstract void executeAsync(Runnable runnable);

    public abstract void registerInitTask(Runnable runnable);

    @FunctionalInterface
    public interface RPGRegionsTask {
        void cancel();
    }
}
