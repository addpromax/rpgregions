package net.islandearth.rpgregions.fauna;

import net.islandearth.rpgregions.fauna.trigger.FaunaTrigger;
import org.bukkit.entity.EntityType;

import java.util.List;

public class VanillaMobFaunaInstance extends FaunaInstance<EntityType> {

    public VanillaMobFaunaInstance(String identifier, String name, List<String> description, EntityType type, List<FaunaTrigger> triggers) {
        super(identifier, name, description, type, triggers);
    }
}
