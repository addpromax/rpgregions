package net.islandearth.rpgregions.requirements;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PermissionRequirement extends RegionRequirement {

    @GuiEditable(value = "Permission", icon = Material.PAPER)
    private final String permission;

    public PermissionRequirement(IRPGRegionsAPI api) {
        super(api);
        this.permission = "rpgregions.admin";
    }

    @Override
    public boolean meetsRequirements(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public String getName() {
        return "Permission";
    }

    @Override
    public String getText(Player player) {
        return "Permission " + permission;
    }
}
