package net.islandearth.rpgregions.requirements;

import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.utils.PlaceholderCompareType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlaceholderRequirement extends RegionRequirement {

    @GuiEditable(value = "Compare type", icon = Material.COMMAND_BLOCK)
    private final PlaceholderCompareType integerCompareType;
    @GuiEditable(value = "The Placeholder", icon = Material.PAPER)
    private final String placeholder;
    @GuiEditable(value = "Placeholder Display Name", icon = Material.PAPER)
    private final String placeholderName;
    @GuiEditable(value = "Value to equal, or compare (e.g 5-10). Supports doubles.", icon = Material.JUNGLE_SIGN)
    private final String equal;

    public PlaceholderRequirement(IRPGRegionsAPI api) {
        this(api, "", "");
    }

    public PlaceholderRequirement(IRPGRegionsAPI api, String placeholder, String equal) {
        super(api);
        this.integerCompareType = PlaceholderCompareType.GREATER_THAN_OR_EQUAL_TO;
        this.placeholder = placeholder;
        this.placeholderName = placeholder;
        this.equal = equal;
    }
    
    @Override
    public boolean meetsRequirements(Player player) {
        try {
            double parsedNumber = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, placeholder));
            if (integerCompareType == PlaceholderCompareType.RANGE) {
                double lower = Double.parseDouble(equal.split("-")[0]);
                double upper = Double.parseDouble(equal.split("-")[1]);
                return parsedNumber >= lower && parsedNumber <= upper;
            }

            double number = Double.parseDouble(equal);
            switch (integerCompareType) {
                case GREATER_THAN:
                    if (parsedNumber > number) {
                        return true;
                    }
                    break;
                case GREATER_THAN_OR_EQUAL_TO:
                    if (parsedNumber >= number) {
                        return true;
                    }
                    break;
                case EQUAL_TO:
                    if (parsedNumber == number) {
                        return true;
                    }
                    break;
                case LESS_THAN:
                    if (parsedNumber < number) {
                        return true;
                    }
                    break;
                case LESS_THAN_OR_EQUAL_TO:
                    if (parsedNumber <= number) {
                        return true;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            return PlaceholderAPI.setPlaceholders(player, placeholder).equals(equal);
        }
        return false;
    }

    @Override
    public String getPluginRequirement() {
        return "PlaceholderAPI";
    }

    @Override
    public String getName() {
        return "Placeholder";
    }
    
    @Override
    public String getText(Player player) {
        return placeholderName == null ? "(null)" : placeholderName;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public String getEqual() {
        return equal;
    }
}
