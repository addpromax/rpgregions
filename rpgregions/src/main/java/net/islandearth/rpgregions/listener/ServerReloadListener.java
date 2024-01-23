package net.islandearth.rpgregions.listener;

import net.islandearth.rpgregions.RPGRegions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public record ServerReloadListener(RPGRegions plugin) implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) {
            plugin.getLogger().severe("RPGRegions does not support reloading. Please use /rpgregions reload or restart your server instead.");
        }
    }
}
