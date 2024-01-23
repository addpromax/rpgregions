package net.islandearth.rpgregions.managers.data.account;

import net.islandearth.rpgregions.managers.data.region.Discovery;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.utils.TimeEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RPGRegionsAccount {

    private final UUID uuid;
    private final Map<String, Discovery> discoveries;
    private final List<AccountCooldown> cooldowns;
    private final Map<String, TimeEntry> secondsInRegion;
    private final Map<DiscoveryReward, Long> lastAwards = new HashMap<>();

    public RPGRegionsAccount(UUID uuid, Map<String, Discovery> discoveries) {
        this.uuid = uuid;
        this.discoveries = discoveries;
        this.cooldowns = new ArrayList<>();
        this.secondsInRegion = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Discovery> getDiscoveries() {
        return discoveries;
    }

    public boolean hasDiscovered(String discoveryName) {
        return discoveries.containsKey(discoveryName);
    }

    public void addDiscovery(Discovery discovery) {
        discoveries.put(discovery.getDiscoveredName(), discovery);
    }

    public List<AccountCooldown> getCooldowns() {
        return cooldowns;
    }

    public Map<String, TimeEntry> getTimedEntries() {
        return Map.copyOf(secondsInRegion);
    }

    public Optional<TimeEntry> getTimeEntryInRegion(String region) {
        return Optional.ofNullable(secondsInRegion.getOrDefault(region, null));
    }

    public TimeEntry addTimeEntryInRegion(String region, long time) {
        System.out.println("added time entry: " + region + ", " + time);
        final TimeEntry timeEntry = new TimeEntry(time);
        secondsInRegion.put(region, timeEntry);
        return timeEntry;
    }

    public void removeStartTimeInRegion(String region) {
        secondsInRegion.remove(region);
    }

    public void updateAwardTime(DiscoveryReward reward) {
        this.lastAwards.put(reward, System.currentTimeMillis());
    }

    public Optional<Long> getLastAwardTime(DiscoveryReward reward) {
        return Optional.ofNullable(this.lastAwards.get(reward));
    }

    public enum AccountCooldown {
        ICON_COMMAND,
        TELEPORT
    }
}