package net.islandearth.rpgregions.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {

    @NotNull
    public static String replaceVariables(String message, String... values) {
        String modifiedMessage = message;
        for (int i = 0; i < 10; i++) {
            if (values.length > i) modifiedMessage = modifiedMessage.replaceAll("%" + i, values[i]);
            else break;
        }

        return modifiedMessage;
    }

    @NotNull
    public static String setPapi(Player player, String message) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }
}
