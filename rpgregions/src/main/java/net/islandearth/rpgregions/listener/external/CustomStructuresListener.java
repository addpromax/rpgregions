package net.islandearth.rpgregions.listener.external;

import com.ryandw11.structure.api.StructureSpawnEvent;
import com.ryandw11.structure.structure.Structure;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.events.RPGRegionsReloadEvent;
import net.islandearth.rpgregions.managers.data.IRPGRegionsCache;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CustomStructuresListener implements Listener {

    private final RPGRegions plugin;
    private YamlConfiguration config;

    public CustomStructuresListener(RPGRegions plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + "integrations" + File.separator + "custom-structures.yml"));
    }

    @EventHandler
    public void onSpawn(StructureSpawnEvent event) {
        final Structure structure = event.getStructure();
        final String name = structure.getName();
        final Location location = event.getLocation();
        final ConfigurationSection section = config.getConfigurationSection("templates." + name);
        if (section == null) return;

        final String template = section.getString("template");
        File templateFile = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + template);
        if (!templateFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, String.format("Unable to load template '%s' for automatic region generation.", template));
            return;
        }

        final List<String> commands = section.getStringList("commands");
        if (commands.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, String.format("Unable to create region for template '%s' because there are no commands to generate a valid region.", template));
            return;
        }

        try (Reader reader = new FileReader(templateFile)) {
            ConfiguredRegion templateRegion = plugin.getGson().fromJson(reader, ConfiguredRegion.class);
            templateRegion.setId((name + "_" + UUID.randomUUID().toString().replace("-", "")).substring(0, 36));
            templateRegion.setLocation(location);
            templateRegion.setCustomName(name);
            templateRegion.setWorld(location.getWorld().getUID());

            final int preSize = plugin.getManagers().getIntegrationManager().getAllRegionNames(location.getWorld()).size();

            final Location min = event.getMinimumPoint();
            final Location max = event.getMaximumPoint();
            for (String command : commands) {
                command = command.replace("{id}", templateRegion.getId());
                command = command.replace("{world}", location.getWorld().getName());
                command = command.replace("{minX}", "" + min.getBlockX());
                command = command.replace("{minY}", "" + min.getBlockY());
                command = command.replace("{minZ}", "" + min.getBlockZ());
                command = command.replace("{maxX}", "" + max.getBlockX());
                command = command.replace("{maxY}", "" + max.getBlockY());
                command = command.replace("{maxZ}", "" + max.getBlockZ());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

            final int postSize = plugin.getManagers().getIntegrationManager().getAllRegionNames(location.getWorld()).size();
            if (postSize <= preSize) {
                plugin.getLogger().log(Level.SEVERE, String.format("Unable to create region for template '%s' with id '%s' because no region was created from the commands.", template, templateRegion.getId()));
                return;
            }

            final IRPGRegionsCache regionsCache = plugin.getManagers().getRegionsCache();
            regionsCache.addConfiguredRegion(templateRegion);
            plugin.debug("Automatically generated region: " + templateRegion.getId());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading template config " + templateFile.getName() + ".", e);
        }
    }

    @EventHandler
    public void onReload(RPGRegionsReloadEvent event) {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + "integrations" + File.separator + "custom-structures.yml"));
    }
}
