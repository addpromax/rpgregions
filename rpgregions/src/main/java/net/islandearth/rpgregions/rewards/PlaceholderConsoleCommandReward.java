package net.islandearth.rpgregions.rewards;

import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.islandearth.rpgregions.utils.PlaceholderCompareType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlaceholderConsoleCommandReward extends DiscoveryReward {

    @GuiEditable(value = "Command", icon = Material.STICK)
    private final String command;

    @GuiEditable(value = "Compare type", icon = Material.COMMAND_BLOCK)
    private final PlaceholderCompareType integerCompareType;

    @GuiEditable(value = "The Placeholder", icon = Material.PAPER)
    private final String placeholder;

    @GuiEditable(value = "Value to equal, or compare (e.g 5-10). Supports doubles.", icon = Material.JUNGLE_SIGN)
    private final String equal;

    public PlaceholderConsoleCommandReward(IRPGRegionsAPI api) {
        this(api, "", "");
    }

    public PlaceholderConsoleCommandReward(IRPGRegionsAPI api, String placeholder, String equal) {
        super(api);
        this.integerCompareType = PlaceholderCompareType.GREATER_THAN_OR_EQUAL_TO;
        this.placeholder = placeholder;
        this.equal = equal;
        this.command = "say example";
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        if (hasPlaceholder(player)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName())));
            this.updateAwardTime(account);
        }
    }

    private boolean hasPlaceholder(Player player) {
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
        return "Placeholder Console Command Reward";
    }
}
