package net.islandearth.rpgregions.api.events;

import net.islandearth.rpgregions.fauna.FaunaInstance;
import net.islandearth.rpgregions.managers.data.region.Discovery;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FaunaDiscoverEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private final Player player;
	private final FaunaInstance<?> fauna;
	private final Discovery discovery;

	public FaunaDiscoverEvent(Player player, FaunaInstance<?> fauna, Discovery discovery) {
		this.player = player;
		this.fauna = fauna;
		this.discovery = discovery;
	}

	/**
	 * The player involved in this event.
	 * @return the player involved
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the fauna that has been discovered.
	 * @return {@link FaunaInstance} that was discovered
	 */
	public FaunaInstance<?> getFauna() {
		return fauna;
	}

	/**
	 * Gets the discovery involved. Contains useful information such as the date.
	 * @return the fauna {@link Discovery}
	 */
	public Discovery getDiscovery() {
		return discovery;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}