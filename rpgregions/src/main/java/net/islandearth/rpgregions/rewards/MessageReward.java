package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Reward to send messages to the player. More complex messages can use the tellraw command in {@link ConsoleCommandReward}.
 */
public class MessageReward extends DiscoveryReward {

    @GuiEditable(value = "Messages", type = GuiEditable.GuiEditableType.CHAT, icon = Material.PAPER)
    private final List<String> messages;

    public MessageReward(IRPGRegionsAPI api) {
        super(api);
        this.messages = new ArrayList<>();
    }

    public MessageReward(IRPGRegionsAPI api, List<String> messages) {
        super(api);
        this.messages = messages;
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        messages.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
        this.updateAwardTime(account);
    }

    @Override
    public String getName() {
        return "Message";
    }
}
