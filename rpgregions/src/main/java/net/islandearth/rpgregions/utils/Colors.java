package net.islandearth.rpgregions.utils;

import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.Locale;

public final class Colors {

	public static final TextColor HONEY_YELLOW = TextColor.color(245, 175, 47);
	public static final TextColor BRIGHT_RED = TextColor.color(244, 61, 61); // ERROR
	public static final TextColor BRIGHT_RED_HIGHLIGHT = TextColor.color(255, 186, 186);
	public static final TextColor EREBOR_GREEN = TextColor.color(4, 219, 100); // OK

	private Colors() {
		throw new UnsupportedOperationException("This class cannot be instantiated");
	}

	public static void sendColourful(CommandSender sender, Component text) {
		RPGRegionsAPI.getAPI().adventure().sender(sender).sendMessage(text);
	}

	public static String colour(final String message) {
		return ChatColor.translateAlternateColorCodes('&', BukkitComponentSerializer.legacy().serialize(RPGRegionsAPI.getAPI().miniMessage().deserialize(message)));
	}

	public static Component colourModern(final String message) {
		return RPGRegionsAPI.getAPI().miniMessage().deserialize(message);
	}

	public static int getARGB(int red, int green, int blue, int alpha) {
		int encoded = 0;
		encoded = encoded | blue;
		encoded = encoded | (green << 8);
		encoded = encoded | (red << 16);
		encoded = encoded | (alpha << 24);
		return encoded;
	}

	public static int getARGB(Color color, int alpha) {
		return getARGB(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public static NamedTextColor translateChatColorToNamedTextColor(org.bukkit.ChatColor chatColor) {
		return NamedTextColor.NAMES.value(chatColor.name().toLowerCase(Locale.ROOT));
	}

	public static Color translateChatColorToColor(org.bukkit.ChatColor chatColor) {
		return switch (chatColor) {
			case AQUA -> Color.CYAN;
			case BLACK -> Color.BLACK;
			case BLUE, DARK_BLUE, DARK_AQUA -> Color.BLUE;
			case DARK_GRAY, GRAY -> Color.GRAY;
			case DARK_GREEN, GREEN -> Color.GREEN;
			case DARK_PURPLE, LIGHT_PURPLE -> Color.MAGENTA;
			case DARK_RED, RED -> Color.RED;
			case GOLD, YELLOW -> Color.YELLOW;
			default -> Color.WHITE;
		};
	}
}
