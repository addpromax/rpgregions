package net.islandearth.rpgregions.fauna.trigger;

import net.islandearth.rpgregions.fauna.FaunaInstance;
import org.bukkit.entity.Player;

public class KillFaunaTrigger extends FaunaTrigger {

    @Override
    public boolean testRepeatable(Player player, FaunaInstance<?> instance) {
        return false;
    }
}
