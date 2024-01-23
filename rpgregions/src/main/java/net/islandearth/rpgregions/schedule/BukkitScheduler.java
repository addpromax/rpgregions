package net.islandearth.rpgregions.schedule;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.schedule.PlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduler extends PlatformScheduler<RPGRegions> {

    public BukkitScheduler(RPGRegions api) {
        super(api);
    }

    @Override
    public void executeOnMain(Runnable runnable) {
        Bukkit.getScheduler().runTask(api, runnable);
    }

    @Override
    public void executeOnEntity(Entity entity, Runnable runnable) {
        executeOnMain(runnable);
    }

    @Override
    public RPGRegionsTask executeRepeating(Runnable runnable, long delay, long period) {
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(api, runnable, delay, period);
        return task::cancel;
    }

    @Override
    public void executeDelayed(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(api, runnable, delay);
    }

    @Override
    public void executeAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(api, runnable);
    }

    @Override
    public void registerInitTask(Runnable runnable) {
        executeOnMain(runnable);
    }
}
