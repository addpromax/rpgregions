package net.islandearth.rpgregions.requirements;

import com.google.common.collect.ImmutableList;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DependencyRequirement extends RegionRequirement {

    @GuiEditable(value = "Required regions", type = GuiEditable.GuiEditableType.CHAT)
    private final List<String> requiredRegions;

    public DependencyRequirement(IRPGRegionsAPI api) {
        this(api, new ArrayList<>());
    }

    public DependencyRequirement(IRPGRegionsAPI api, List<String> requiredRegions) {
        super(api);
        this.requiredRegions = requiredRegions;
    }
    
    /**
     * Returns an immutable list of required regions by their ID.
     * @return immutable list of required regions
     */
    public ImmutableList<String> getRequiredRegions() {
        return ImmutableList.copyOf(requiredRegions);
    }
    
    public boolean meetsRequirements(List<String> discoveries) {
        return new HashSet<>(discoveries).containsAll(requiredRegions);
    }
    
    @Override
    @Deprecated
    public boolean meetsRequirements(Player player) {
        return meetsRequirements(Collections.emptyList());
    }
    
    @Override
    public String getName() {
        return "Dependency";
    }
    
    @Override
    public String getText(Player player) {
        List<String> convertedIds = new ArrayList<>();
        for (String requiredRegion : requiredRegions) {
            RPGRegionsAPI.getAPI().getManagers().getRegionsCache().getConfiguredRegion(requiredRegion).ifPresent(configured -> {
                convertedIds.add(configured.getCustomName());
            });
        }

        final boolean plural = convertedIds.size() > 1;
        return (plural ? "Regions: " : "Region: ") + String.join(", ", convertedIds);
    }

    @Override
    public boolean feedback(Player player, String input) {
        boolean flag = super.feedback(player, input);
        if (!flag) {
            if (!requiredRegions.contains(input)) {
                requiredRegions.add(input);
            } else {
                requiredRegions.remove(input);
            }
            return true;
        }
        return flag;
    }

    @Override
    public String info(String field) {
        if (field.equals("requiredRegions")) {
            return "Enter a region ID to add/remove a dependency on it.";
        }
        return super.info(field);
    }
}
