package net.islandearth.rpgregions.effects;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.IGuiEditable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RegionEffect implements IGuiEditable {
	
	private final boolean wearingRequired;
	private final List<ItemStack> ignoreItems;
	private final boolean ignorePerm;

	public RegionEffect(IRPGRegionsAPI api) {
		this(api, false, new ArrayList<>());
	}

	public RegionEffect(IRPGRegionsAPI api, boolean wearingRequired, List<ItemStack> ignoreItems) {
		this.wearingRequired = wearingRequired;
		this.ignoreItems = ignoreItems;
		this.ignorePerm = true;
	}

	/**
	 * Effects the specified player
	 * @param player
	 */
	public abstract void effect(Player player);

	/**
	 * Called when a player exits all regions and effects should be entirely removed.
	 * @param player
	 */
	public void uneffect(Player player) {}

	/**
	 * Gets the required Minecraft version for this effect to function.
	 * @return minecraft version
	 */
	@Nullable
	public String getRequiredVersion() {
		return null;
	}

	/**
	 * Whether the items are required to be worn in armour slots
	 * @return whether items should be worn in armour slots
	 */
	public boolean isWearingRequired() {
		return wearingRequired;
	}

	/**
	 * The items that a player should have to not be effected
	 * @return ignored items
	 */
	public List<ItemStack> getIgnoreItems() {
		return ignoreItems;
	}

	/**
	 * Whether the ItemStack is within #getIgnoreItems()
	 * @param item the {@link ItemStack}
	 * @return true if item is ignored
	 */
	public boolean shouldIgnore(ItemStack item) {
		return ignoreItems.contains(item);
	}
	
	/**
	 * Gets the permission for this effect to apply.
	 * @return permission the player requires
	 */
	public String getPermission() {
		return "rpgregions.effect." + getName();
	}
	
	/**
	 * Whether the permission should be ignored or not.
	 * @return true if permission is ignored
	 */
	public boolean isIgnorePerm() {
		return ignorePerm;
	}
}
