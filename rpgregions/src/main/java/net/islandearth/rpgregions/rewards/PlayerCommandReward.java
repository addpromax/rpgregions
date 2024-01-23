package net.islandearth.rpgregions.rewards;

import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerCommandReward extends DiscoveryReward {

	@GuiEditable(value = "Command", icon = Material.STICK)
	private final String command;

	public PlayerCommandReward(IRPGRegionsAPI api) {
		super(api);
		this.command = "say example";
	}

	public PlayerCommandReward(IRPGRegionsAPI api, String command) {
		super(api);
		this.command = command;
	}

	@Override
	public void award(Player player, RPGRegionsAccount account) {
		String command = this.command.replace("%player%", player.getName());
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			command = PlaceholderAPI.setPlaceholders(player, command);
		}
		player.performCommand(command);
		this.updateAwardTime(account);
	}

	@Override
	public String getName() {
		return "Player Command";
	}
}