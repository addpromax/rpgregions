package net.islandearth.rpgregions.rewards;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import me.blackvein.quests.player.IQuester;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.thread.Blocking;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class QuestReward extends DiscoveryReward implements Blocking {

    @GuiEditable("Quest id")
    private final String questId;

    @GuiEditable("Start or complete?")
    private final boolean start;

    @GuiEditable("Ignore requirements for starting?")
    private final boolean ignoreRequirements;

    public QuestReward(IRPGRegionsAPI api) {
        super(api);
        this.questId = "test";
        this.start = true;
        this.ignoreRequirements = false;
    }

    public String getQuestId() {
        return questId;
    }

    public boolean startOrComplete() {
        return start;
    }

    public boolean ignoreRequirements() {
        return ignoreRequirements;
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        Quests quests = JavaPlugin.getPlugin(Quests.class);
        try { // Have to perform blocking operation.
            IQuester quester = quests.getStorage().loadQuester(player.getUniqueId()).get();
            Quest quest = quests.getQuestById(questId);
            if (quest == null) return;
            if (start) quester.takeQuest(quest, ignoreRequirements);
            else quest.completeQuest(quester);
            this.updateAwardTime(account);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Quest";
    }

    @Override
    public String getPluginRequirement() {
        return "Quests";
    }
}
