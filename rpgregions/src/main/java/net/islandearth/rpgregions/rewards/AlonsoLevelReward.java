package net.islandearth.rpgregions.rewards;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.entity.Player;

public class AlonsoLevelReward extends LevelReward {

    public AlonsoLevelReward(IRPGRegionsAPI api) {
        super(api);
    }

    public AlonsoLevelReward(IRPGRegionsAPI api, int level) {
        super(api, level);
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        AlonsoLevelsAPI.addLevel(player.getUniqueId(), getLevel());
        this.updateAwardTime(account);
    }

    @Override
    public String getPluginRequirement() {
        return "AlonsoLevels";
    }
}
