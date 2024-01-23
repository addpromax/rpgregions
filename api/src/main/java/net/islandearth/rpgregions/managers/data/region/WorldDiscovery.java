package net.islandearth.rpgregions.managers.data.region;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorldDiscovery(LocalDateTime date, String region) implements Discovery {

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String getDiscoveredName() {
        return region;
    }
}
