package net.islandearth.rpgregions.utils;

public class TimeEntry {

    private long start, latestEntry;

    public TimeEntry(long start) {
        this.start = start;
        this.latestEntry = start;
    }

    public long getStart() {
        return start;
    }

    public long getLatestEntry() {
        return latestEntry;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setLatestEntry(long latestEntry) {
        this.latestEntry = latestEntry;
    }
}
