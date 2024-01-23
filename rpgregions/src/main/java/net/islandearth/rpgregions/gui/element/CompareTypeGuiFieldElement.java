package net.islandearth.rpgregions.gui.element;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import net.islandearth.rpgregions.chat.preset.ReturnValueConversationPreset;
import net.islandearth.rpgregions.gui.IGuiEditable;
import net.islandearth.rpgregions.utils.PlaceholderCompareType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompareTypeGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        new ReturnValueConversationPreset(player, "Enter the compare type, valid compares are: "
                + Arrays.toString(PlaceholderCompareType.values()) + ".", input -> {
            Optional<PlaceholderCompareType> compareType = Enums.getIfPresent(PlaceholderCompareType.class,input.toUpperCase());
            if (compareType.isPresent()) {
                try {
                    FieldUtils.writeField(field, guiEditable, PlaceholderCompareType.valueOf(input.toUpperCase()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(ChatColor.RED + "Could not find a compare type with that name. "
                        + Arrays.toString(PlaceholderCompareType.values()));
            }
            completableFuture.complete(null);
        });
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(PlaceholderCompareType.class);
    }

    @Override
    public boolean needsValue() {
        return false;
    }
}
