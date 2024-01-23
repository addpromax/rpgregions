package net.islandearth.rpgregions.fauna.trigger;

import net.islandearth.rpgregions.fauna.FaunaInstance;
import org.bukkit.entity.Player;

public abstract class FaunaTrigger {

    /**
     * Tests whether this trigger is met by the specified player.
     * @param player the player to check
     * @return true if met, false otherwise
     */
    public abstract boolean testRepeatable(Player player, FaunaInstance<?> instance);
}
