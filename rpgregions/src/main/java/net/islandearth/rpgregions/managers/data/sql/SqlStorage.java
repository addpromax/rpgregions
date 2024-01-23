package net.islandearth.rpgregions.managers.data.sql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.SQLCommonStorage;

public class SqlStorage extends SQLCommonStorage {

    public SqlStorage(RPGRegions plugin) {
        super(plugin,
            DatabaseOptions.builder().mysql(plugin.getConfig().getString("settings.sql.user"),
                plugin.getConfig().getString("settings.sql.pass"),
                plugin.getConfig().getString("settings.sql.db"),
                plugin.getConfig().getString("settings.sql.host") + ":" + plugin.getConfig().getString("settings.sql.port")).build());
        Database db = PooledDatabaseOptions.builder().options(getDatabaseOptions()).createHikariDatabase();
        DB.setGlobalDatabase(db);
    }
}
