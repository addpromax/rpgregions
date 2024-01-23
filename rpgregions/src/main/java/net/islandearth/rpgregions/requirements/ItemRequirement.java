package net.islandearth.rpgregions.requirements;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRequirement extends RegionRequirement {

    @GuiEditable(value = "Item", icon = Material.GRASS_BLOCK)
    private final ItemStack itemStack;

    @GuiEditable(value = "Inverse", icon = Material.BARRIER)
    private boolean inverse;

    public ItemRequirement(IRPGRegionsAPI api) {
        super(api);
        this.itemStack = new ItemStack(Material.WOODEN_SWORD);
    }

    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean meetsRequirements(Player player) {
        boolean hasItem = false;
        for (ItemStack content : player.getInventory()) {
            if (content != null && content.getType() != Material.AIR && content.isSimilar(itemStack)) {
                hasItem = true;
                break;
            }
        }
        return inverse != hasItem;
    }

    @Override
    public String getText(Player player) {
        return "Item: " + itemStack.getType() + " x" + itemStack.getAmount();
    }
}
