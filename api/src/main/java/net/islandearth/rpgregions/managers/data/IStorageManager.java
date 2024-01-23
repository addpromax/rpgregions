package net.islandearth.rpgregions.managers.data;

import com.github.benmanes.caffeine.cache.AsyncCache;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.configuration.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStorageManager {

    List<Long> TIMINGS = new LinkedList<>();

    /**
     * Gets a player's account from the storage.
     * This will return an account stored in the cache.
     * If no account is found in the cache a new account will be fetched and added to the cache.
     * @param uuid player's UUID
     * @return player's account
     */
    CompletableFuture<RPGRegionsAccount> getAccount(UUID uuid);

    /**
     * Gets a map of currently cached accounts.
     * @return map of cached accounts
     */
    AsyncCache<UUID, RPGRegionsAccount> getCachedAccounts();

    /**
     * Removes all discoveries matching a player.
     * @param uuid player uuid
     */
    void clearDiscoveries(UUID uuid);

    /**
     * Removes a specific discovery matching a player.
     * @param uuid player uuid
     * @param regionId region id
     */
    void clearDiscovery(UUID uuid, String regionId);

    void deleteAccount(UUID uuid);

    /**
     * Removes an account from the storage cache and optionally saves its data.
     * @param uuid player's UUID
     * @param save whether to save the player's data
     */
    CompletableFuture<Void> removeCachedAccount(UUID uuid, boolean save);

    default CompletableFuture<Void> removeCachedAccount(UUID uuid) {
        return removeCachedAccount(uuid, true);
    }

    CompletableFuture<Void> saveAccount(RPGRegionsAccount account);

    /**
     * Gets a UUID safe to use in databases.
     * @param uuid player's UUID
     * @return new string uuid to use in databases
     */
    default String getDatabaseUuid(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    default void timing(long ms) {
        if (TIMINGS.size() >= 3) TIMINGS.remove(2);
        TIMINGS.add(ms);

        final IRPGRegionsAPI api = RPGRegionsAPI.getAPI();
        final Configuration config = api.getConfig();
        if (!config.getBoolean("settings.dev.disable-slow-storage-warn") && ms >= 20) {
            api.getLogger().warning("Grabbing accounts is taking a long time! (" + ms + "ms)");
        }
    }

    default List<Long> getTimings() {
        return TIMINGS;
    }

    default long getTimingsAverage() {
        if (TIMINGS.size() < 3) {
            final int size = TIMINGS.size();
            for (int i = 0; i < 3 - size; i++) {
                TIMINGS.add(0L);
            }
        }
        return (TIMINGS.get(0) + TIMINGS.get(1) + TIMINGS.get(2)) / 3;
    }
}
