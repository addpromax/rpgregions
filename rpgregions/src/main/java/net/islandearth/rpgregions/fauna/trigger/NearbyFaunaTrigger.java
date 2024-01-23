package net.islandearth.rpgregions.fauna.trigger;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.islandearth.rpgregions.fauna.FaunaInstance;
import net.islandearth.rpgregions.fauna.MythicMobFaunaInstance;
import net.islandearth.rpgregions.fauna.VanillaMobFaunaInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NearbyFaunaTrigger extends FaunaTrigger {

    private final double radius;

    public NearbyFaunaTrigger(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean testRepeatable(Player player, FaunaInstance<?> instance) {
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (instance instanceof VanillaMobFaunaInstance vanilla) {
                if (vanilla.getType() == entity.getType()) {
                    return true;
                }
            } else if (instance instanceof MythicMobFaunaInstance mythic) {
                final ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
                if (mythicMob.getType().equals(instance.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
