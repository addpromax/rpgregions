package net.islandearth.rpgregions.api.events;

import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.data.region.Discovery;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionDiscoverEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();
	private final Player player;
	private final RPGRegionsAccount account;
	private final ConfiguredRegion region;
	private final Discovery discovery;
	
	public RegionDiscoverEvent(Player player, RPGRegionsAccount account, ConfiguredRegion region, Discovery discovery) {
		this.player = player;
		this.account = account;
		this.region = region;
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
	 * Gets the account of the player involved in this event.
	 * @return the account of the player involved
	 */
	public RPGRegionsAccount getAccount() {
		return account;
	}

	/**
	 * Gets the region that has been discovered.
	 * @return {@link ConfiguredRegion} that was discovered
	 */
	public ConfiguredRegion getRegion() {
		return region;
	}

	/**
	 * Gets the discovery involved. Contains useful information such as the date.
	 * @return the region {@link Discovery}
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