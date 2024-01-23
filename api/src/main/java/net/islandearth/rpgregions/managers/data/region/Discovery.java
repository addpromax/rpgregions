package net.islandearth.rpgregions.managers.data.region;

import java.time.LocalDateTime;

public interface Discovery {

    /**
     * Gets the date this discovery was made.
     * @return date of discovery
     */
    LocalDateTime getDate();

    /**
     * Gets the name of what was discovered.
     * @return name of discovered
     */
    String getDiscoveredName();
}
