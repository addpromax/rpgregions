package net.islandearth.rpgregions.api.integrations;

import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.requirements.DependencyRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface IntegrationManager {

    /**
     * Checks if the specified location is within a region.
     * @param location location to check
     * @return true if location is within a region, false otherwise
     */
    boolean isInRegion(Location location);

    /**
     * Handles a move event to perform related region checks.
     * @param pme PlayerMoveEvent
     */
    void handleMove(PlayerMoveEvent pme);

    /**
     * Gets the highest priority region at given location
     * @param location location to check
     * @return highest prioritised region
     */
    Optional<ConfiguredRegion> getPrioritisedRegion(Location location);

    /**
     * Checks whether this region exists within the world specified
     * @param location {@link World} to check
     * @param region the region id
     * @return true if region exists in world, false otherwise
     */
    boolean exists(World location, String region);

    /**
     * Gets an immutable set of all region names in the specified world.
     * @param world {@link World} to check
     * @return set of all region names
     */
    Set<String> getAllRegionNames(World world);

    default boolean checkRequirements(PlayerMoveEvent event, ConfiguredRegion priority, String region) {
        if (priority.disablesPassthrough() && !region.equals(priority.getId())) return true;
        Player player = event.getPlayer();
        IRPGRegionsAPI plugin = RPGRegionsAPI.getAPI();
        Optional<ConfiguredRegion> configuredRegion = plugin.getManagers().getRegionsCache().getConfiguredRegion(region);
        if (configuredRegion.isEmpty()) return false;
        if (configuredRegion.get().getRequirements() != null) {
            boolean hasBypass = player.hasPermission("rpgregions.bypassentry");
            if (hasBypass) plugin.debug("Player has bypass permission");
            for (RegionRequirement requirement : configuredRegion.get().getRequirements()) {
                boolean flag = !requirement.meetsRequirements(player) && !hasBypass;
                if (requirement instanceof DependencyRequirement dependencyRequirement) {
                    List<String> discoveries = new ArrayList<>();
                    try {
                        RPGRegionsAccount account = RPGRegionsAPI.getAPI().getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
                        discoveries.addAll(account.getDiscoveries().keySet());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    flag = !hasBypass && !dependencyRequirement.meetsRequirements(discoveries);
                }

                if (flag) {
                    requirement.getPreventType().prevent(event);
                    if (requirement.getPreventMessage() != null) {
                        for (String preventCommand : requirement.getPreventCommands()) {
                            final String converted = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, preventCommand) : preventCommand;
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), converted.replace("[player]", player.getName()));
                        }

                        String requirementMessage = MessageUtils.setPapi(player, requirement.getPreventMessage());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', requirementMessage));
                    } else Translations.CANNOT_ENTER.send(player, requirement.getText(player));
                    return false;
                }
            }
        }
        return true;
    }

    @NotNull
    List<Location> getBoundingBoxPoints(@NotNull ConfiguredRegion region);
}
