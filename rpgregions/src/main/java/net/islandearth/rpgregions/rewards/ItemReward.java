package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward extends DiscoveryReward {

	@GuiEditable(value = "Item", icon = Material.BARREL)
	private final ItemStack item;

	public ItemReward(IRPGRegionsAPI api) {
		super(api);
		this.item = new ItemStack(Material.DIRT);
	}
	public ItemReward(IRPGRegionsAPI api, ItemStack item) {
		super(api);
		this.item = item;
	}
	
	@Override
	public void award(Player player, RPGRegionsAccount account) {
		player.getInventory().addItem(item).forEach((pos, item) -> {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
		});
		this.updateAwardTime(account);
	}

	@Override
	public String getName() {
		return "Item";
	}
}
