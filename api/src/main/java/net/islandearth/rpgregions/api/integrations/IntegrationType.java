package net.islandearth.rpgregions.api.integrations;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public enum IntegrationType {
    WORLDGUARD("WorldGuard", "worldguard.WorldGuardIntegration"),
    RESIDENCE("Residence", "residence.ResidenceIntegration"),
    GRIEFPREVENTION("GriefPrevention", "griefprevention.GriefPreventionIntegration"),
    GRIEFDEFENDER("GriefDefender", "griefdefender.GriefDefenderIntegration"),
    LANDS("Lands", "lands.LandsIntegration"),
    ULTRAREGIONS("UltraRegions", "ultraregions.UltraRegionsIntegration"),
    RPGREGIONS("RPGRegions", "rpgregions.RPGRegionsIntegration");

    private final String plugin;
    private final String path;

    IntegrationType(String plugin, String path) {
        this.plugin = plugin;
        this.path = path;
    }

    public Optional<IntegrationManager> get(IRPGRegionsAPI plugin) throws ClassNotFoundException {
        if (plugin.getManagers() != null && plugin.getManagers().getIntegrationManager() != null) throw new UnsupportedOperationException("IntegrationManager already loaded");
        plugin.getLogger().info("Loading IntegrationManager implementation...");
        if (!this.plugin.equals("RPGRegions") && Bukkit.getPluginManager().getPlugin(this.plugin) == null) {
            return Optional.empty();
        }

        Class<? extends IntegrationManager> clazz = (Class<? extends IntegrationManager>) Class
                .forName("net.islandearth.rpgregions.api.integrations." + path);
        IntegrationManager generatedClazz = null;
        try {
            generatedClazz = clazz.getConstructor(IRPGRegionsAPI.class).newInstance(plugin);
            if (generatedClazz instanceof Listener) { // Register events if applicable
                Bukkit.getPluginManager().registerEvents((Listener) generatedClazz, (JavaPlugin) plugin);
            }
            plugin.getLogger().info("Loaded IntegrationManager implementation " + clazz.getName() + ".");
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Unable to load IntegrationManager (" + clazz.getName() + ")! Plugin will disable.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin((JavaPlugin) plugin);
        }

        return Optional.ofNullable(generatedClazz);
    }
}
