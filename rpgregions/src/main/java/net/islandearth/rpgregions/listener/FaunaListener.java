package net.islandearth.rpgregions.listener;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.events.FaunaDiscoverEvent;
import net.islandearth.rpgregions.api.events.RPGRegionsReloadEvent;
import net.islandearth.rpgregions.fauna.FaunaInstance;
import net.islandearth.rpgregions.translation.Translations;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public record FaunaListener(RPGRegions plugin) implements Listener {

    @EventHandler
    public void onDiscover(FaunaDiscoverEvent event) {
        final Player player = event.getPlayer();
        final Audience audience = plugin.adventure().player(player);
        final FaunaInstance<?> fauna = event.getFauna();
        audience.showTitle(Title.title(Translations.FAUNA_DISCOVER_TITLE.get(player, fauna.getDisplayName()).get(0), Translations.FAUNA_DISCOVER_SUBTITLE.get(player, fauna.getDisplayName()).get(0)));
    }

    @EventHandler
    public void onReload(RPGRegionsReloadEvent event) {
        plugin.getManagers().getFaunaCache().reload();
    }
}
