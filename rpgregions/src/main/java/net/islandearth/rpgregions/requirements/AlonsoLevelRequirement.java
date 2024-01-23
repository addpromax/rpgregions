package net.islandearth.rpgregions.requirements;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.entity.Player;

public class AlonsoLevelRequirement extends LevelRequirement {

    public AlonsoLevelRequirement(IRPGRegionsAPI api) {
        this(api, 1);
    }

    public AlonsoLevelRequirement(IRPGRegionsAPI api, int level) {
        super(api, level);
    }

    @Override
    public boolean meetsRequirements(Player player) {
        return AlonsoLevelsAPI.getLevel(player.getUniqueId()) >= this.getLevel();
    }

    @Override
    public String getPluginRequirement() {
        return "AlonsoLevels";
    }
}
