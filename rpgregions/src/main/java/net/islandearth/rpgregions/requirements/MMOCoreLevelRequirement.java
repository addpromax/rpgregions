package net.islandearth.rpgregions.requirements;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.entity.Player;

public class MMOCoreLevelRequirement extends LevelRequirement {

    public MMOCoreLevelRequirement(IRPGRegionsAPI api) {
        this(api, 1);
    }

    public MMOCoreLevelRequirement(IRPGRegionsAPI api, int level) {
        super(api, level);
    }

    @Override
    public boolean meetsRequirements(Player player) {
        final PlayerData playerData = PlayerData.get(player.getUniqueId());
        return playerData.getLevel() >= this.getLevel();
    }

    @Override
    public String getPluginRequirement() {
        return "MMOCore";
    }
}
