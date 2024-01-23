package net.islandearth.rpgregions.tasks;

import net.islandearth.rpgregions.RPGRegions;
import org.bukkit.Location;
import org.dynmap.DynmapAPI;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynmapTask implements Runnable {

    private final RPGRegions plugin;
    private final DynmapAPI dynmapAPI;
    private final Map<String, AreaMarker> markers;
    private final MarkerSet markerSet;

    public DynmapTask(final RPGRegions plugin) {
        this.plugin = plugin;
        this.dynmapAPI = DynmapPlugin.plugin;
        this.markers = new HashMap<>();
        this.markerSet = dynmapAPI.getMarkerAPI().createMarkerSet("rpgregions.regions", "Regions", dynmapAPI.getMarkerAPI().getMarkerIcons(), false);
    }

    @Override
    public void run() {
        plugin.getManagers().getRegionsCache().getConfiguredRegions().forEach((name, region) -> {
            if (!region.dynmap()) return;
            List<Location> boundingBox = region.getBoundingBox();
            if (boundingBox == null) return;
            if (!markers.containsKey(name)) {
                if (region.getWorld() == null) return;
                plugin.debug("Generated bounding box for dynmap integration (" + name + "): " + boundingBox);
                String markerid = region.getWorld() + "_" + name;
                AreaMarker am = markerSet.createAreaMarker(markerid, region.getCustomName(), true, region.getWorld().getName(), new double[1000], new double[1000], false);
                markers.put(name, am);
            }

            // Update the marker information
            AreaMarker am = markers.get(name);
            double[] x = new double[boundingBox.size()];
            double[] z = new double[boundingBox.size()];
            for(int i = 0; i < boundingBox.size(); i++) {
                Location point = boundingBox.get(i);
                x[i] = point.getX(); z[i] = point.getZ();
            }

            am.setCornerLocations(x, z);
            am.setLabel(region.getCustomName(), true);
            if (!region.getColour().equals("0")) am.setFillStyle(region.getOpacity(), Integer.parseInt(region.getColour()));
            if (!region.getLineColour().equals("0")) am.setLineStyle(1, region.getLineOpacity(), Integer.parseInt(region.getLineColour()));
            am.setDescription("<b>" + region.getCustomName() + "</b>");
        });
    }
}
