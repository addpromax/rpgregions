package net.islandearth.rpgregions.api;

public final class RPGRegionsAPI {

    private RPGRegionsAPI() {}

    private static IRPGRegionsAPI api;

    public static IRPGRegionsAPI getAPI() {
        return api;
    }

    public static void setAPI(IRPGRegionsAPI api) {
        if (RPGRegionsAPI.api != null && api != null) throw new IllegalStateException("API already set");
        RPGRegionsAPI.api = api;
    }
}
