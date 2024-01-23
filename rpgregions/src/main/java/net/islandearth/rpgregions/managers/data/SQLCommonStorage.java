package net.islandearth.rpgregions.managers.data;

import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.Discovery;
import net.islandearth.rpgregions.managers.data.region.WorldDiscovery;
import net.islandearth.rpgregions.utils.TimeEntry;
import org.bukkit.Bukkit;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.ValidateOutput;
import org.flywaydb.core.api.output.ValidateResult;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class SQLCommonStorage implements IStorageManager {

    protected static final String SELECT_REGION = "SELECT * FROM rpgregions_discoveries WHERE uuid = ?";
    protected static final String INSERT_DISCOVERY = "INSERT INTO rpgregions_discoveries (uuid, discovery_id, time) VALUES (?, ?, ?)";
    protected static final String DELETE_DISCOVERIES = "DELETE FROM rpgregions_discoveries WHERE uuid = ?";
    protected static final String DELETE_DISCOVERY = "DELETE FROM rpgregions_discoveries WHERE uuid = ? AND discovery_id = ?";

    // Timed region discoveries
    protected static final String SELECT_TIMED_REGION = "SELECT * FROM rpgregions_timed_discoveries WHERE uuid = ?";
    protected static final String INSERT_TIMED_REGION = "INSERT INTO rpgregions_timed_discoveries (uuid, region, start, latest) VALUES (?, ?, ?)";
    protected static final String DELETE_TIMED_REGIONS = "DELETE FROM rpgregions_timed_discoveries WHERE uuid = ?";
    protected static final String DELETE_TIMED_REGION = "DELETE FROM rpgregions_timed_discoveries WHERE uuid = ? AND region = ?";

    private final AsyncCache<UUID, RPGRegionsAccount> cachedAccounts;

    private final RPGRegions plugin;
    private final DatabaseOptions options;

    public SQLCommonStorage(RPGRegions plugin, DatabaseOptions options) {
        this.options = options;
        this.plugin = plugin;

        migrate();

        this.cachedAccounts = Caffeine.newBuilder()
                .initialCapacity(Bukkit.getMaxPlayers())
                .maximumSize(1_000) // Realistically no server can support higher than this, even Folia
                .scheduler(Scheduler.systemScheduler())
                .expireAfterAccess(plugin.getConfig().getInt("settings.storage.cache-expiry-time", 180), TimeUnit.SECONDS)
                .removalListener((k, v, c) -> {
                    plugin.debug("Removed user from cache, cause: " + c.name());
                    // If the user was manually removed, don't save, let other code handle how it wants
                    if (v == null || c == RemovalCause.EXPLICIT) return;
                    saveAccount(((RPGRegionsAccount) v));
                })
                .buildAsync((key, executor) -> getAccount(key));
    }

    private void migrate() {
        // Time to migrate!
        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure(plugin.getClass().getClassLoader())
                .baselineOnMigrate(true)
                .dataSource("jdbc:" + options.getDsn(), options.getUser(), options.getPass()).load();
        // Start the migration
        flyway.migrate();
        final ValidateResult result = flyway.validateWithResult();
        if (!result.validationSuccessful) {
            panic(result);
            throw new IllegalStateException("Could not migrate the database!");
        }
    }

    private void panic(ValidateResult result) {
        plugin.getLogger().severe("=== UNABLE TO MIGRATE DATABASE, ERROR AS FOLLOWS ===");
        plugin.getLogger().severe("=== BASIC INFO ===");
        plugin.getLogger().severe("Flyway Version: " + result.flywayVersion);
        plugin.getLogger().severe("Plugin Version: " + plugin.getDescription().getVersion());
        plugin.getLogger().severe("Server Version: " + Bukkit.getServer().getVersion());
        plugin.getLogger().severe("=== MIGRATION INFO ===");
        plugin.getLogger().severe("Operation: " + result.operation);
        plugin.getLogger().severe("Error messages (combined): " + result.getAllErrorMessages());
        plugin.getLogger().severe("Error code: " + result.errorDetails.errorCode);
        plugin.getLogger().severe("Error message: " + result.errorDetails.errorMessage);
        plugin.getLogger().severe("=== INVALID MIGRATIONS ===");
        for (ValidateOutput invalidMigration : result.invalidMigrations) {
            plugin.getLogger().severe("-");
            plugin.getLogger().severe("Error code: " + invalidMigration.errorDetails.errorCode);
            plugin.getLogger().severe("Error message: " + invalidMigration.errorDetails.errorMessage);
            plugin.getLogger().severe("Description: " + invalidMigration.description);
            plugin.getLogger().severe("Version: " + invalidMigration.version);
            plugin.getLogger().severe("Path: " + invalidMigration.filepath);
            plugin.getLogger().severe("-");
        }
        plugin.getLogger().severe("=== MIGRATION WARNINGS ===");
        for (String warning : result.warnings) {
            plugin.getLogger().warning(warning);
        }
        plugin.getLogger().severe("=== END ERROR, EXITING ===");
    }

    protected DatabaseOptions getDatabaseOptions() {
        return options;
    }

    @Override
    public CompletableFuture<RPGRegionsAccount> getAccount(UUID uuid) {
        // Check if cached
        final CompletableFuture<RPGRegionsAccount> possibly = cachedAccounts.getIfPresent(uuid);
        if (possibly != null) {
            plugin.debug("Using cached user: " + uuid);
            return possibly;
        }

        // Add a check to ensure accounts aren't taking a long time
        long startTime = System.currentTimeMillis();
        CompletableFuture<RPGRegionsAccount> future = new CompletableFuture<>();
        future.thenAccept(account -> {
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            timing(totalTime);
        });

        cachedAccounts.put(uuid, CompletableFuture.supplyAsync(() -> {
            final List<DbRow> results = DB.getResultsAsync(SELECT_REGION, getDatabaseUuid(uuid)).join();
            Map<String, Discovery> regions = new HashMap<>();
            for (DbRow row : results) {
                String discoveryId = row.getString("discovery_id");
                LocalDateTime time;
                try {
                    time = LocalDateTime.parse(row.getString("time"));
                } catch (DateTimeParseException e) {
                    plugin.getLogger().info("Migrating legacy stored date for " + uuid + ": " + e.getMessage());
                    time = plugin.getDateFormatter().parse(row.getString("time"), LocalDateTime::from);
                }
                regions.put(discoveryId, new WorldDiscovery(time, discoveryId));
            }

            final RPGRegionsAccount account = new RPGRegionsAccount(uuid, regions);

            final List<DbRow> timedResults = DB.getResultsAsync(SELECT_TIMED_REGION, getDatabaseUuid(uuid)).join();
            for (DbRow row : timedResults) {
                final String regionId = row.getString("region");
                final Long start = row.getLong("start");
                final Long latest = row.getLong("latest");
                final TimeEntry timeEntry = account.addTimeEntryInRegion(regionId, start);
                timeEntry.setLatestEntry(latest);
            }

            plugin.debug("Created user account: " + uuid);
            future.complete(account);
            return account;
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        }));
        return future;
    }

    @Override
    public AsyncCache<UUID, RPGRegionsAccount> getCachedAccounts() {
        return cachedAccounts;
    }

    @Override
    public void clearDiscoveries(UUID uuid) {
        getAccount(uuid).thenAccept(account -> account.getDiscoveries().clear()).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });

        DB.executeUpdateAsync(DELETE_DISCOVERIES, getDatabaseUuid(uuid));
    }

    @Override
    public void clearDiscovery(UUID uuid, String regionId) {
        getAccount(uuid).thenAccept(account -> account.getDiscoveries().remove(regionId)).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });

        DB.executeUpdateAsync(DELETE_DISCOVERY, getDatabaseUuid(uuid), regionId);
    }

    @Override
    public void deleteAccount(UUID uuid) {
        this.clearDiscoveries(uuid);
        cachedAccounts.synchronous().invalidate(uuid);
    }

    @Override
    public CompletableFuture<Void> removeCachedAccount(UUID uuid, boolean save) {
        if (!save) {
            cachedAccounts.synchronous().invalidate(uuid);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            final RPGRegionsAccount account = cachedAccounts.synchronous().getIfPresent(uuid);
            if (account == null) return null;
            saveAccount(account).join();
            cachedAccounts.synchronous().invalidate(uuid);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> saveAccount(RPGRegionsAccount account) {
        final UUID uuid = account.getUuid();
        return CompletableFuture.supplyAsync(() -> {
            try {
                DB.executeUpdate(DELETE_DISCOVERIES, getDatabaseUuid(uuid));
                for (Discovery discovery : account.getDiscoveries().values()) {
                    executeInsert(INSERT_DISCOVERY, getDatabaseUuid(uuid), discovery.getDiscoveredName(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(discovery.getDate()));
                }

                // Remove all existing timed regions first
                DB.executeUpdate(DELETE_TIMED_REGIONS, getDatabaseUuid(uuid));
                account.getTimedEntries().forEach((regionId, entry) -> {
                    if (account.getDiscoveries().containsKey(regionId)) return; // Ignore if this somehow happened
                    executeInsert(INSERT_TIMED_REGION, getDatabaseUuid(uuid), regionId, entry.getStart(), entry.getLatestEntry());
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    protected void executeInsert(String query, Object... params) {
        try {
            DB.executeInsert(query, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
