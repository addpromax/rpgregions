package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Material;

public abstract class LevelReward extends DiscoveryReward {

    @GuiEditable(value = "Level", icon = Material.EXPERIENCE_BOTTLE)
    private final int level;

    public LevelReward(IRPGRegionsAPI api) {
        super(api);
        this.level = 1;
    }

    public LevelReward(IRPGRegionsAPI api, int level) {
        super(api);
        this.level = level;
    }

    @Override
    public String getName() {
        return "Level";
    }

    public int getLevel() {
        return level;
    }
}
