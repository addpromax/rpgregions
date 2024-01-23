package net.islandearth.rpgregions.chat.preset;

import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.chat.RPGRegionsConversationPrefix;
import net.islandearth.rpgregions.chat.RPGRegionsStringPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ConfirmConversationPreset {

    public ConfirmConversationPreset(Player player, Consumer<Boolean> action) {
        RPGRegions plugin = JavaPlugin.getPlugin(RPGRegions.class);
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(new RPGRegionsConversationPrefix())
                .withFirstPrompt(new RPGRegionsStringPrompt("Are you sure you want to do this? (yes/no)"))
                .withEscapeSequence("quit")
                .withLocalEcho(true)
                .withTimeout(60);
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
        conversation.addConversationAbandonedListener(abandonedEvent -> {
            String input = (String) abandonedEvent.getContext().getSessionData("input");
            if (input == null) return;
            boolean accepted = input.equalsIgnoreCase("yes");
            action.accept(accepted);
        });
        player.closeInventory();
    }
}
