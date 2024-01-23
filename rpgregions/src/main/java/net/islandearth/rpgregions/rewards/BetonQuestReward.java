package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.entity.Player;

public class BetonQuestReward extends QuestReward {

    public BetonQuestReward(IRPGRegionsAPI api) {
        super(api);
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        //todo: According to the developer, BetonQuest has no such concept of "starting" or "completing" quests?
    }

    @Override
    public String getName() {
        return "Quest";
    }

    @Override
    public String getPluginRequirement() {
        return "BetonQuest";
    }
}
