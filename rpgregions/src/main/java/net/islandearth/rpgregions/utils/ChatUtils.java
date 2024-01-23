package net.islandearth.rpgregions.utils;

import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public final class ChatUtils {

    public static final Component ARROW_RIGHT = text("➜", Colors.EREBOR_GREEN);
    public static final Component PENCIL_RIGHT = text("✎", Colors.EREBOR_GREEN);

    private ChatUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void eventOk(CommandSender sender, String message) {
        RPGRegionsAPI.getAPI().adventure().sender(sender).sendMessage(text().append(ARROW_RIGHT.append(space()))
                .append(Colors.colourModern(message)).color(Colors.EREBOR_GREEN));
    }

    public static void edit(CommandSender sender, ClickEvent event) {
        RPGRegionsAPI.getAPI().adventure().sender(sender).sendMessage(text().append(text("[")).append(PENCIL_RIGHT.append(space()))
                .append(text("Edit...]")).color(Colors.HONEY_YELLOW).clickEvent(event));
    }
}
