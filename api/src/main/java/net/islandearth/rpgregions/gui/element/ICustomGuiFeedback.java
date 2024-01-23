package net.islandearth.rpgregions.gui.element;

import org.bukkit.entity.Player;

public interface ICustomGuiFeedback {

    boolean feedback(Player player, String input);

    default String info(String field) {
        return null;
    }
}
