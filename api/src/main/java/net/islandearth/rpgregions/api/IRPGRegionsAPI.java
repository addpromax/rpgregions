package net.islandearth.rpgregions.api;

import com.convallyria.languagy.api.language.Translator;
import com.google.gson.Gson;
import net.islandearth.rpgregions.api.schedule.PlatformScheduler;
import net.islandearth.rpgregions.managers.IRPGRegionsManagers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.logging.Logger;

public interface IRPGRegionsAPI {

    Translator getTranslator();

    Logger getLogger();

    File getDataFolder();

    void saveResource(String path, boolean replace);

    Gson getGson();

    Configuration getConfig();

    IRPGRegionsManagers getManagers();

    boolean debug();
    
    void debug(String debug);

    boolean hasHeadDatabase();

    /**
     * Gets the adventure implementation for the plugin.
     * @return the adventure implementation
     */
    BukkitAudiences adventure();

    /**
     * Gets the MiniMessage implementation for the plugin.
     * @return the MiniMessage implementation
     */
    MiniMessage miniMessage();

    /**
     * Gets the scheduler used for the current platform.
     * @return the scheduler for this server platform
     */
    PlatformScheduler<? extends IRPGRegionsAPI> getScheduler();
}
