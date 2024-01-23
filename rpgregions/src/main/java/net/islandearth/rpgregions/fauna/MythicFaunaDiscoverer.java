package net.islandearth.rpgregions.fauna;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.fauna.trigger.KillFaunaTrigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static net.islandearth.rpgregions.fauna.FaunaDiscoverer.discover;

public record MythicFaunaDiscoverer(RPGRegions plugin) implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(MythicMobDeathEvent event) {
        final ActiveMob mob = event.getMob();
        if (!(event.getKiller() instanceof Player killer) || !killer.hasPermission("rpgregions.bestiary")) return;
        plugin.getManagers().getStorageManager().getAccount(killer.getUniqueId()).thenAccept(account -> {
            for (FaunaInstance<?> fauna : plugin.getManagers().getFaunaCache().getFauna()) {
                if (fauna instanceof MythicMobFaunaInstance mythicInstance) {
                    if (!mythicInstance.getType().equals(mob.getType())) continue;
                    if (account.hasDiscovered(fauna.getIdentifier())) continue;
                    if (!fauna.hasTrigger(KillFaunaTrigger.class)) continue;
                    discover(killer, account, fauna);
                    return;
                }
            }
        });
    }
}
