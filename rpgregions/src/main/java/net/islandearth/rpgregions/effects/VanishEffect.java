package net.islandearth.rpgregions.effects;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VanishEffect extends RegionEffect {

    public VanishEffect(IRPGRegionsAPI api) {
        super(api);
    }

    @Override
    public void effect(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer((Plugin) RPGRegionsAPI.getAPI(), player);
            player.hidePlayer((Plugin) RPGRegionsAPI.getAPI(), onlinePlayer);
        }
    }

    @Override
    public void uneffect(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer((Plugin) RPGRegionsAPI.getAPI(), player);
            player.showPlayer((Plugin) RPGRegionsAPI.getAPI(), onlinePlayer);
        }
    }

    @Override
    public String getName() {
        return "Vanish";
    }
}
