package net.islandearth.rpgregions.managers.data;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.sql.SqlStorage;
import net.islandearth.rpgregions.managers.data.sqlite.SqliteStorage;
import net.islandearth.rpgregions.managers.data.yml.YamlStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum StorageType {
    FILE(YamlStorage.class),
    SQL(SqlStorage.class),
    SQLITE(SqliteStorage.class);

    private final Class<? extends IStorageManager> clazz;

    StorageType(Class<? extends IStorageManager> clazz) {
        this.clazz = clazz;
    }

    public Optional<IStorageManager> get() {
        RPGRegions plugin = JavaPlugin.getPlugin(RPGRegions.class);
        plugin.getLogger().info("Loading StorageManager implementation...");
        IStorageManager generatedClazz = null;
        try {
            generatedClazz = clazz.getConstructor(RPGRegions.class).newInstance(plugin);
            plugin.getLogger().info("Loaded StorageManager implementation " + clazz.getName() + ".");
            if (generatedClazz instanceof YamlStorage) {
                plugin.getLogger().warning("You are using the YamlStorage implementation which is " +
                        "not recommended for performance if dealing with lots of players.");
            }
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            plugin.getLogger().severe("Unable to load StorageManager (" + clazz.getName() + ")! Plugin will disable.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        return Optional.ofNullable(generatedClazz);
    }
}
