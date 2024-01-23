package net.islandearth.rpgregions.requirements;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public enum PreventType {
    TELEPORT,
    TELEPORT_SYNC,
    PUSH,
    CANCEL;

    private static final String[] SPLIT_VERSION = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
    private static final int VERSION_INT = Integer.parseInt(SPLIT_VERSION[1]);

    private static final Particle BARRIER_PARTICLE = VERSION_INT <= 17 ? Particle.valueOf("BARRIER") : Particle.BLOCK_MARKER;
    private static final BlockData BARRIER_DATA = Material.BARRIER.createBlockData();

    public void prevent(PlayerMoveEvent event) {
        if (event.getTo() == null) return;

        // Teleport events must be simply cancelled
        if (event instanceof PlayerTeleportEvent) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        switch (this) {
            case TELEPORT -> PaperLib.teleportAsync(player, event.getFrom());
            case TELEPORT_SYNC -> player.teleport(event.getFrom());
            case PUSH -> player.setVelocity(event.getTo().toVector().subtract(event.getFrom().toVector()).multiply(-3));
            case CANCEL -> event.setCancelled(true);
        }

        player.spawnParticle(BARRIER_PARTICLE, event.getTo().getBlock().getLocation().add(0.5, 0.5, 0.5), 1, BARRIER_DATA);
    }
}
