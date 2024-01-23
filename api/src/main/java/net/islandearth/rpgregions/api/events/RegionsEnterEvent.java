package net.islandearth.rpgregions.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collections;
import java.util.List;

public class RegionsEnterEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private final Player player;
	private final List<String> regions;
	private final boolean hasChanged;

	public RegionsEnterEvent(Player player, List<String> regions, boolean hasChanged) {
		this.player = player;
		this.regions = regions;
		this.hasChanged = hasChanged;
	}

	public RegionsEnterEvent(Player player, String region, boolean hasChanged) {
		this.player = player;
		this.regions = Collections.singletonList(region);
		this.hasChanged = hasChanged;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets a list of all regions that have been entered.
	 * @return {@link List} of regions
	 */
	public List<String> getRegions() {
		return regions;
	}

	/**
	 * Gets the prioritised region. This will be the first element in the regions array.
	 * This is the region that will be prioritised for particles. Effects etc will stack.
	 * @return the region at the start of the regions array
	 */
	public String getPriority() {
		return regions.get(0);
	}

	/**
	 * Returns whether the player has truly moved into a new region.
	 * @return whether player has truly moved into a new region
	 */
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}