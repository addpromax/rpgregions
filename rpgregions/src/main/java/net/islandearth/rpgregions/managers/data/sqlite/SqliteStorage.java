package net.islandearth.rpgregions.managers.data.sqlite;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.SQLCommonStorage;

public class SqliteStorage extends SQLCommonStorage {

    public SqliteStorage(RPGRegions plugin) {
        super(plugin, DatabaseOptions.builder().sqlite(plugin.getDataFolder() + "/regions.sqlite").build());
        Database db = PooledDatabaseOptions.builder().options(getDatabaseOptions()).createHikariDatabase();
        DB.setGlobalDatabase(db);
    }
}
