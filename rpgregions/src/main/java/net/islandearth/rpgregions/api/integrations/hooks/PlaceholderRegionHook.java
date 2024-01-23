package net.islandearth.rpgregions.api.integrations.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.thread.Blocking;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.TimeEntry;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlaceholderRegionHook extends PlaceholderExpansion implements Blocking {

    private final RPGRegions plugin;

    public PlaceholderRegionHook(RPGRegions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "rpgregions";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        if (identifier.startsWith("discovered_region_")) {
            // We have to do a blocking operation :(
            try {
                RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
                String region = identifier.replace("discovered_region_", "");
                boolean discovered = account.getDiscoveries().containsKey(region);
                return String.valueOf(discovered);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return "";
        }

        final int totalRegionsConfigured = plugin.getManagers().getRegionsCache().getConfiguredRegions().size();
        switch (identifier.toLowerCase()) {
            case "region":
                if (plugin.getManagers().getIntegrationManager().getPrioritisedRegion(player.getLocation()).isPresent())
                    return plugin.getManagers().getIntegrationManager().getPrioritisedRegion(player.getLocation()).get().getCustomName();
                else
                    return LegacyComponentSerializer.legacyAmpersand().serialize(Translations.UNKNOWN_REGION.get(player).get(0));
            case "discovered_count":
                // We have to do a blocking operation :(
                try {
                    RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
                    return String.valueOf(account.getDiscoveries().size());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            case "discovered_percentage":
                // We have to do a blocking operation :(
                try {
                    RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
                    int percent = totalRegionsConfigured == 0 ? 0 : (account.getDiscoveries().size() / totalRegionsConfigured) * 100;
                    return String.valueOf(percent);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            case "region_total":
                return String.valueOf(totalRegionsConfigured);
            case "region_timed": {
                final Optional<ConfiguredRegion> region = plugin.getManagers().getIntegrationManager().getPrioritisedRegion(player.getLocation());
                if (region.isEmpty()) {
                    return LegacyComponentSerializer.legacyAmpersand().serialize(Translations.UNKNOWN_REGION.get(player).get(0));
                }

                final ConfiguredRegion configuredRegion = region.get();
                if (!configuredRegion.isTimedRegion()) return region.get().getCustomName();

                // We have to do a blocking operation :(
                try {
                    RPGRegionsAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
                    if (account.getDiscoveries().containsKey(region.get().getId())) {
                        return region.get().getCustomName();
                    }

                    final Optional<TimeEntry> timeEntry = account.getTimeEntryInRegion(region.get().getId());
                    if (timeEntry.isEmpty()) return region.get().getCustomName();

                    final double entry = timeEntry.get().getStart();
                    final double time = TimeUnit.MILLISECONDS.toSeconds((long) (timeEntry.get().getLatestEntry() - entry));
                    final double secondsInsideToDiscover = configuredRegion.getSecondsInsideToDiscover();
                    final double percent = (time / secondsInsideToDiscover) * 100;
                    return LegacyComponentSerializer.legacyAmpersand().serialize(Translations.DISCOVERING_AREA_PLACEHOLDER.get(player, (int) percent, region.get().getId()).get(0));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            default:
                return null;
        }
    }
}
