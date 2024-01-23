package net.islandearth.rpgregions.fauna;

import io.lumine.mythic.api.mobs.MythicMob;
import net.islandearth.rpgregions.fauna.trigger.FaunaTrigger;

import java.util.List;

public class MythicMobFaunaInstance extends FaunaInstance<MythicMob> {

    public MythicMobFaunaInstance(String identifier, String name, List<String> description, MythicMob type, List<FaunaTrigger> triggers) {
        super(identifier, name, description, type, triggers);
    }
}
