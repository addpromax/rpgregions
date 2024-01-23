package net.islandearth.rpgregions.api.integrations.lands;

import me.angeschossen.lands.api.events.ChunkDeleteEvent;
import me.angeschossen.lands.api.events.LandCreateEvent;
import me.angeschossen.lands.api.events.LandDeleteEvent;
import me.angeschossen.lands.api.land.Land;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.managers.data.IRPGRegionsCache;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.logging.Level;

public class LandsListener implements Listener {

    private final LandsIntegration landsIntegration;
    private final IRPGRegionsAPI plugin;
    private final YamlConfiguration config;

    public LandsListener(final LandsIntegration landsIntegration) {
        this.landsIntegration = landsIntegration;
        this.plugin = landsIntegration.getPlugin();
        this.config = landsIntegration.getConfig();
    }

    @EventHandler
    public void onCreate(LandCreateEvent event) {
        final Land land = event.getLand();
        final int landSize = land.getSize();
        final int minSize = config.getInt("min-land-size");
        if (landSize < minSize) return;

        final ConfigurationSection templates = config.getConfigurationSection("templates");
        // Key is like '10', '20' etc.
        for (String key : templates.getKeys(false)) {
            final int size = Integer.parseInt(key);
            if (landSize >= size) {
                final String template = templates.getString(key + ".template");
                File templateFile = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + template);
                if (!templateFile.exists()) {
                    plugin.getLogger().log(Level.SEVERE, String.format("Unable to load template '%s' for automatic region generation.", template));
                    continue;
                }

                try (Reader reader = new FileReader(templateFile)) {
                    ConfiguredRegion templateRegion = plugin.getGson().fromJson(reader, ConfiguredRegion.class);
                    templateRegion.setId(land.getName());
                    if (land.getSpawn() != null) templateRegion.setWorld(land.getSpawn().getWorld().getUID());

                    final IRPGRegionsCache regionsCache = plugin.getManagers().getRegionsCache();
                    if (regionsCache.getConfiguredRegion(land.getName()).isPresent()) {
                        regionsCache.removeConfiguredRegion(land.getName());
                    }
                    regionsCache.addConfiguredRegion(templateRegion);
                    plugin.debug("Automatically generated region: " + templateRegion.getId());
                    break;
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error loading template config " + templateFile.getName() + ".", e);
                }
            }
        }
    }

    @EventHandler
    public void onDelete(LandDeleteEvent event) {
        final Land land = event.getLand();

        final IRPGRegionsCache regionsCache = plugin.getManagers().getRegionsCache();
        if (regionsCache.getConfiguredRegion(land.getName()).isPresent()) {
            regionsCache.removeConfiguredRegion(land.getName());
        }
    }

    @EventHandler
    public void onDelete(ChunkDeleteEvent event) {
        final Land land = event.getLand();
        final int landSize = land.getSize() - 1;
        final int minSize = config.getInt("min-land-size");
        if (landSize >= minSize) return;

        final IRPGRegionsCache regionsCache = plugin.getManagers().getRegionsCache();
        if (regionsCache.getConfiguredRegion(land.getName()).isPresent()) {
            regionsCache.removeConfiguredRegion(land.getName());
        }
    }
}
