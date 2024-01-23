package net.islandearth.rpgregions.api.integrations.rpgregions;

import com.google.gson.Gson;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.events.RegionsEnterEvent;
import net.islandearth.rpgregions.api.integrations.IntegrationManager;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.RPGRegionsRegion;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RPGRegionsIntegration implements IntegrationManager {

    private final IRPGRegionsAPI plugin;
    private final Map<String, RPGRegionsRegion> regions;

    public RPGRegionsIntegration(IRPGRegionsAPI plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        plugin.getLogger().info("RPGRegions region integration has been enabled.");
    }

    /*
     * API METHODS
     */

    public Collection<RPGRegionsRegion> getRegions() {
        return regions.values();
    }

    public Set<RPGRegionsRegion> getRegions(final Location location) {
        List<RPGRegionsRegion> foundRegions = new ArrayList<>();
        for (RPGRegionsRegion region : regions.values()) {
            if (region.isWithinBounds(location)) foundRegions.add(region);
        }
        return Set.copyOf(foundRegions);
    }

    public Optional<RPGRegionsRegion> getRegion(final String name) {
        return Optional.ofNullable(regions.get(name));
    }

    public void addRegion(final RPGRegionsRegion region) {
        regions.put(region.getName(), region);
    }

    public void removeRegion(final RPGRegionsRegion region) {
        this.removeRegion(region.getName());
    }

    public void removeRegion(final String name) {
        regions.remove(name);
    }
    /*
     * OVERRIDES
     */

    @Override
    public boolean isInRegion(Location location) {
        for (RPGRegionsRegion region : regions.values()) {
            if (region.isWithinBounds(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleMove(PlayerMoveEvent pme) {
        if (pme.getTo() == null) return;
        plugin.debug("Handling movement");
        Player player = pme.getPlayer();
        double oldX = pme.getFrom().getX();
        double oldY = pme.getFrom().getY();
        double oldZ = pme.getFrom().getZ();
        double x = pme.getTo().getX();
        double y = pme.getTo().getY();
        double z = pme.getTo().getZ();
        Set<RPGRegionsRegion> oldRegions = this.getRegions(new Location(player.getWorld(), oldX, oldY, oldZ));
        Set<RPGRegionsRegion> regions = this.getRegions(new Location(player.getWorld(), x, y, z));

        Optional<ConfiguredRegion> configuredRegion = getPrioritisedRegion(pme.getTo());
        configuredRegion.ifPresent(prioritisedRegion -> {
            plugin.debug("Priority region found");
            plugin.debug("Old: " + oldRegions);
            plugin.debug("New: " + regions);
            List<String> stringRegions = new ArrayList<>();
            regions.forEach(region -> {
                if (!prioritisedRegion.getId().equals(region.getName())
                        && checkRequirements(pme, prioritisedRegion, region.getName())) stringRegions.add(region.getName());
            });

            if (checkRequirements(pme, prioritisedRegion, prioritisedRegion.getId())) {
                plugin.debug("Requirements passed, calling");
                stringRegions.add(0, prioritisedRegion.getId());
                Bukkit.getPluginManager().callEvent(new RegionsEnterEvent(player, stringRegions, !oldRegions.equals(regions)));
            }
        });
    }

    @Override
    public Optional<ConfiguredRegion> getPrioritisedRegion(Location location) {
        RPGRegionsRegion highest = null;
        for (String key : regions.keySet()) {
            final RPGRegionsRegion region = regions.get(key);
            if ((highest == null || region.getPriority() > highest.getPriority()) && region.isWithinBounds(location)) {
                highest = region;
            }
        }
        return highest == null ? Optional.empty() : plugin.getManagers().getRegionsCache().getConfiguredRegion(highest.getName());
    }

    @Override
    public boolean exists(World location, String region) {
        return regions.containsKey(region);
    }

    @Override
    public Set<String> getAllRegionNames(World world) {
        return regions.keySet();
    }

    @Override
    public @NotNull List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region) {
        for (RPGRegionsRegion rpgRegionsRegion : regions.values()) {
            if (rpgRegionsRegion.getName().equals(region.getId())) {
                return rpgRegionsRegion.getPoints();
            }
        }
        return new ArrayList<>();
    }

    public void onEnable() {
        File regionsDir = new File(plugin.getDataFolder() + File.separator + "integration" + File.separator + "regions");
        if (!regionsDir.exists()) regionsDir.mkdirs();
        for (File file : regionsDir.listFiles()) {
            try (Reader reader = new FileReader(file)) {
                Gson gson = plugin.getGson();
                RPGRegionsRegion region = gson.fromJson(reader, RPGRegionsRegion.class);
                if (region == null) continue;
                regions.put(region.getName(), region);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDisable() {
        save();
    }

    public void save() {
        File regionsDir = new File(plugin.getDataFolder() + File.separator + "integration" + File.separator + "regions");
        if (!regionsDir.exists()) regionsDir.mkdirs();

        regions.forEach((name, region) -> {
            File file = new File(regionsDir + File.separator + name + ".json");
            try (Writer writer = new FileWriter(file)) {
                Gson gson = plugin.getGson();
                gson.toJson(region, RPGRegionsRegion.class, writer);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
