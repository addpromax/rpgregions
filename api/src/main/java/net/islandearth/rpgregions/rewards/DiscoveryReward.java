package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.gui.IGuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.entity.Player;

public abstract class DiscoveryReward implements IGuiEditable {

	private final transient IRPGRegionsAPI api;

	@GuiEditable("Always Reward")
	private boolean alwaysAward;

	@GuiEditable("Time Between Reward (s)")
	private int timeBetweenReward;

	public DiscoveryReward(IRPGRegionsAPI api) {
		this.api = api;
	}

	public IRPGRegionsAPI getAPI() {
		return api;
	}

	/**
	 * Awards this reward to the specified player
	 * @param player player to award to
	 */
	public abstract void award(Player player, RPGRegionsAccount account);

	protected void updateAwardTime(RPGRegionsAccount account) {
		account.updateAwardTime(this);
	}

	public boolean isAlwaysAward() {
		return alwaysAward;
	}

	public void setAlwaysAward(boolean alwaysAward) {
		this.alwaysAward = alwaysAward;
	}

	public int getTimeBetweenReward() {
		return timeBetweenReward;
	}

	public void setTimeBetweenReward(int timeBetweenReward) {
		this.timeBetweenReward = timeBetweenReward;
	}

	public boolean canAward(RPGRegionsAccount account) {
		return (System.currentTimeMillis() - account.getLastAwardTime(this).orElse(0L)) >= (timeBetweenReward * 1000L);
	}

	public String getPluginRequirement() {
		return null;
	}
}
