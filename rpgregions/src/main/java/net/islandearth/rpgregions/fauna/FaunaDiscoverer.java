package net.islandearth.rpgregions.fauna;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.events.FaunaDiscoverEvent;
import net.islandearth.rpgregions.fauna.trigger.FaunaTrigger;
import net.islandearth.rpgregions.fauna.trigger.KillFaunaTrigger;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.WorldDiscovery;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.time.LocalDateTime;

public class FaunaDiscoverer implements Runnable, Listener {

    private final RPGRegions plugin;

    public FaunaDiscoverer(RPGRegions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("rpgregions.bestiary")) continue;
            plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
                for (FaunaInstance<?> fauna : plugin.getManagers().getFaunaCache().getFauna()) {
                    if (account.hasDiscovered(fauna.getIdentifier())) continue;
                    for (FaunaTrigger trigger : fauna.getTriggers()) {
                        if (trigger.testRepeatable(player, fauna)) {
                            discover(player, account, fauna);
                            break;
                        }
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer == null || !killer.hasPermission("rpgregions.bestiary")) return;

        plugin.getManagers().getStorageManager().getAccount(killer.getUniqueId()).thenAccept(account -> {
            for (FaunaInstance<?> fauna : plugin.getManagers().getFaunaCache().getFauna()) {
                if (fauna instanceof VanillaMobFaunaInstance vanillaInstance) {
                    if (vanillaInstance.getType() != entity.getType()) continue;
                    if (account.hasDiscovered(fauna.getIdentifier())) continue;
                    if (!fauna.hasTrigger(KillFaunaTrigger.class)) continue;
                    discover(killer, account, fauna);
                    return;
                }
            }
        });
    }

    public static void discover(Player player, RPGRegionsAccount account, FaunaInstance<?> fauna) {
        WorldDiscovery discovery = new WorldDiscovery(LocalDateTime.now(), fauna.getIdentifier());
        account.addDiscovery(discovery);
        Bukkit.getPluginManager().callEvent(new FaunaDiscoverEvent(player, fauna, discovery));
    }
}
