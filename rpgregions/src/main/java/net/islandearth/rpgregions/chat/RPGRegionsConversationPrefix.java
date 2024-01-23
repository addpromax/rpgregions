package net.islandearth.rpgregions.chat;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.jetbrains.annotations.NotNull;

public class RPGRegionsConversationPrefix implements ConversationPrefix {

    @NotNull
    @Override
    public String getPrefix(@NotNull ConversationContext context) {
        return ChatColor.GOLD + "RPGRegions > " + ChatColor.AQUA;
    }
}
