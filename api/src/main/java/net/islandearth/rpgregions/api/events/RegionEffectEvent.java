package net.islandearth.rpgregions.api.events;

import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * @deprecated This event is currently not implemented and is under consideration
 */
@Deprecated
public class RegionEffectEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private final Player player;
	private final ConfiguredRegion region;

	public RegionEffectEvent(Player player, ConfiguredRegion region) {
		this.player = player;
		this.region = region;
	}

	/**
	 * The player involved in this event.
	 * @return the player involved
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the region that will give effects to the player.
	 * @return {@link List} of regions
	 */
	public ConfiguredRegion getRegion() {
		return region;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
