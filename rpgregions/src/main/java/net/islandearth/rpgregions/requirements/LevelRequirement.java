package net.islandearth.rpgregions.requirements;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LevelRequirement extends RegionRequirement {

    @GuiEditable(value = "Level", icon = Material.EXPERIENCE_BOTTLE)
    private final int level;

    @GuiEditable(value = "Maximum Level", icon = Material.EXPERIENCE_BOTTLE)
    private final int maxLevel;

    public LevelRequirement(IRPGRegionsAPI api) {
        this(api, 1);
    }

    public LevelRequirement(IRPGRegionsAPI api, int level) {
        super(api);
        this.level = level;
        this.maxLevel = -1;
    }

    @Override
    public boolean meetsRequirements(Player player) {
        return player.getLevel() >= level && (maxLevel == -1 || player.getLevel() < maxLevel);
    }

    @Override
    public String getName() {
        return "Level";
    }

    @Override
    public String getText(Player player) {
        return "Level " + level;
    }

    public int getLevel() {
        return level;
    }
}
