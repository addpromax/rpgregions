package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ExperienceReward extends DiscoveryReward {

	@GuiEditable(value = "Experience", icon = Material.EXPERIENCE_BOTTLE)
	private final int xp;

	public ExperienceReward(IRPGRegionsAPI api) {
		super(api);
		this.xp = 20;
	}

	public ExperienceReward(IRPGRegionsAPI api, int xp) {
		super(api);
		this.xp = xp;
	}
	
	@Override
	public void award(Player player, RPGRegionsAccount account) {
		player.giveExp(xp);
		this.updateAwardTime(account);
	}

	@Override
	public String getName() {
		return "Experience";
	}
}
