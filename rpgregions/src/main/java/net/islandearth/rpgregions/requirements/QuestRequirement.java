package net.islandearth.rpgregions.requirements;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.thread.Blocking;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class QuestRequirement extends RegionRequirement implements Blocking {

    @GuiEditable("Quest id")
    private final String questId;

    public QuestRequirement(IRPGRegionsAPI api) {
        super(api);
        this.questId = "test";
    }

    @Override
    public boolean meetsRequirements(Player player) {
        Quests quests = JavaPlugin.getPlugin(Quests.class);
        try { // Have to perform blocking operation.
            Quester quester = (Quester) quests.getStorage().loadQuester(player.getUniqueId()).get();
            for (Quest completedQuest : quester.getCompletedQuests()) {
                if (completedQuest.getId().equals(questId)) return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getName() {
        return "Quest";
    }

    @Override
    public String getText(Player player) {
        return "Quest " + questId;
    }

    @Override
    public String getPluginRequirement() {
        return "Quests";
    }
}
