package net.islandearth.rpgregions.listener;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public record MoveListener(RPGRegions plugin) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        plugin.debug("Entrypoint move for player '" + player.getName() + "'.");
        this.move(event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        plugin.debug("Entrypoint teleport for player '" + player.getName() + "'.");
        this.move(event);
    }

    private void move(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        // RANT!
        // Citizens is STUPID AS HELL and is activating PlayerTeleportEvent for NPCs.
        // This causes issues with my plugin because that player doesn't exist for me and
        // it can result in regions with requirement preventing NPCs from entering!!!
        // So now I have to do this STUPID check to see if the player actually exists!?!?!
        // IT'S AN NPC!!!!
        // /RANT
        final boolean isNpcPlayer = Bukkit.getPlayer(player.getUniqueId()) == null;
        if (isNpcPlayer) {
            plugin.debug("A stupid plugin tried to call move event for an NPC. Ignoring.");
            return;
        }

        plugin.getManagers().getIntegrationManager().handleMove(event);

        if (!plugin.getManagers().getIntegrationManager().isInRegion(event.getTo()) && plugin.getManagers().getIntegrationManager().isInRegion(event.getFrom())) {
            for (ConfiguredRegion configuredRegion : plugin.getManagers().getRegionsCache().getConfiguredRegions().values()) {
                configuredRegion.getEffects().forEach(effect -> effect.uneffect(player));
            }
        }
    }
}