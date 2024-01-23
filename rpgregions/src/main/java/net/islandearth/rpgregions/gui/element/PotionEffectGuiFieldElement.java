package net.islandearth.rpgregions.gui.element;

import net.islandearth.rpgregions.chat.preset.ReturnValueConversationPreset;
import net.islandearth.rpgregions.gui.IGuiEditable;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PotionEffectGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        new ReturnValueConversationPreset(player, "Enter the effect type, you can set duration and " +
                "amplifier using `;`, e.g REGENERATION;1000;1", input -> {
            String[] split = input.split(";");
            String effectName = split[0];
            PotionEffectType validType = null;
            for (PotionEffectType type : PotionEffectType.values()) {
                if (type.getName().equalsIgnoreCase(effectName)) {
                    validType = type;
                    break;
                }
            }
            if (validType == null) {
                player.sendMessage(ChatColor.RED + "Could not find an effect type with that name. "
                        + Arrays.toString(PotionEffectType.values()));
            } else {
                try {
                    if (field.getType().isAssignableFrom(PotionEffectType.class)) {
                        FieldUtils.writeField(field, guiEditable, validType);
                    } else {
                        PotionEffect potionEffect = (PotionEffect) field.get(guiEditable);
                        int duration = potionEffect.getDuration();
                        int amplifier = potionEffect.getAmplifier();
                        if (split.length > 1) {
                            duration = Integer.parseInt(split[1]);
                        }
                        if (split.length > 2) {
                            amplifier = Integer.parseInt(split[2]);
                        }
                        PotionEffect newPotionEffect = new PotionEffect(validType, duration, amplifier,
                                potionEffect.isAmbient(), potionEffect.hasParticles(), potionEffect.hasIcon());
                        FieldUtils.writeField(field, guiEditable, newPotionEffect);
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
            completableFuture.complete(null);
        });
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(PotionEffect.class, PotionEffectType.class);
    }

    @Override
    public boolean needsValue() {
        return false;
    }
}
