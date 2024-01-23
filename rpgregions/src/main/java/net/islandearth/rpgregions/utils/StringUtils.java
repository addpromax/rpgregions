package net.islandearth.rpgregions.utils;

import net.md_5.bungee.api.ChatColor;

public class StringUtils {

    private StringUtils() {}

    /**
     * @deprecated Uses legacy text
     * @param msg the message
     * @return the coloured message
     */
    @Deprecated(forRemoval = true)
    public static String colour(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
