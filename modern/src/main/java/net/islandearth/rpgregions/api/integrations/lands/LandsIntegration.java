package net.islandearth.rpgregions.api.integrations.lands;

import me.angeschossen.lands.api.land.Land;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.events.RegionsEnterEvent;
import net.islandearth.rpgregions.api.integrations.IntegrationManager;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LandsIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;
    private final YamlConfiguration config;
    private final me.angeschossen.lands.api.integration.LandsIntegration lands;

    public LandsIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + "integrations" + File.separator + "lands.yml"));
        this.lands = new me.angeschossen.lands.api.integration.LandsIntegration((Plugin) plugin);
        if (config.getBoolean("auto-generate")) {
            Bukkit.getPluginManager().registerEvents(new LandsListener(this), (Plugin) plugin);
        }
    }

    public IRPGRegionsAPI getPlugin() {
        return plugin;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public boolean isInRegion(Location location) {
        return lands.isClaimed(location);
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        Player player = pme.getPlayer();
        if (pme.getTo() == null) return;
        Land oldLand = lands.getLand(pme.getFrom());
        Land land = lands.getLand(pme.getTo());
        if (oldLand == null || land == null || !land.exists()) return;
        if (!checkRequirements(pme, getPrioritisedRegion(pme.getTo()).get(), land.getName())) return;

        Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, land.getName(), !oldLand.equals(land)));
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        Land land = lands.getLand(location);
        return land == null || !land.exists()
                ? Optional.empty()
                : plugin.getManagers().getRegionsCache().getConfiguredRegion(land.getName());
    }

    @Override
    public boolean exists(World location, String region) {
        Land land = lands.getLand(region);
        return land != null && land.exists();
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        Set<String> landNames = new HashSet<>();
        for (Land land : lands.getLands()) {
            final String name = land.getName();
            landNames.add(name);
        }
        return landNames;
    }

    @Override
    public @NonNull List<Location> getBoundingBoxPoints(ConfiguredRegion region) {
        //TODO
        return new ArrayList<>();
    }
}
