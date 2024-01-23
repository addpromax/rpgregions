package net.islandearth.rpgregions.effects;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PotionRegionEffect extends RegionEffect {

	@GuiEditable(value = "Potion", type = GuiEditable.GuiEditableType.CHAT, icon = Material.HONEY_BOTTLE)
	private final PotionEffect potionEffect;

	public PotionRegionEffect(IRPGRegionsAPI api) {
		super(api);
		this.potionEffect = new PotionEffect(PotionEffectType.REGENERATION, 1, 1);
	}

	public PotionRegionEffect(IRPGRegionsAPI api, PotionEffect potionEffect, boolean wearingRequired, List<ItemStack> ignoreItems) {
		super(api, wearingRequired, ignoreItems);
		this.potionEffect = potionEffect;
	}

	public PotionEffect getPotionEffect() {
		return potionEffect;
	}

	@Override
	public void effect(Player player) {
		if (this.isIgnorePerm() || player.hasPermission(this.getPermission())) {
			player.addPotionEffect(potionEffect);
		}
	}

	@Override
	public String getName() {
		return "PotionRegionEffect";
	}
}
