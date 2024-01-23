package net.islandearth.rpgregions.utils;

import com.google.common.base.Enums;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.List;

public class TitleAnimator {

    public TitleAnimator(Player player, IRPGRegionsAPI plugin, List<Component> titles, List<Component> subtitles, int speed) {
        new BukkitRunnable() {
            int current = 0;
            @Override
            public void run() {
                Component title = current < titles.size() ? titles.get(current) : Component.empty();
                Component subtitle = current < subtitles.size() ? subtitles.get(current) : Component.empty();
                plugin.debug("Title is: " + title + " Subtitle is: " + subtitle);
                if (current >= titles.size()
                        && current >= subtitles.size()) {
                    plugin.debug("Cancelling! No more titles left to send.");
                    this.cancel();
                    return;
                }

                plugin.debug("Successful title send!");
                int fadein = plugin.getConfig().getInt("settings.server.discoveries.discovered.title.fadein");
                if (current >= 1) {
                    fadein = 0;
                    final String animationSound = plugin.getConfig().getString("settings.server.discoveries.discovered.title.animation_sound", Sound.BLOCK_TRIPWIRE_CLICK_ON.name());
                    final int animationSoundPitch = plugin.getConfig().getInt("settings.server.discoveries.discovered.title.animation_sound_pitch", 1);
                    if (current >= 1) {
                        Enums.getIfPresent(Sound.class, animationSound).toJavaUtil().ifPresentOrElse(sound -> {
                            player.playSound(
                                    player.getLocation(),
                                    sound,
                                    1,
                                    animationSoundPitch
                            );
                        }, () -> player.playSound(
                                player.getLocation(),
                                animationSound,
                                1,
                                animationSoundPitch
                        ));
                    }
                }

                final int stay = plugin.getConfig().getInt("settings.server.discoveries.discovered.title.stay");
                final int fadeout = plugin.getConfig().getInt("settings.server.discoveries.discovered.title.fadeout");
                plugin.adventure().player(player)
                    .showTitle(Title.title(title, subtitle,
                        Title.Times.times(
                            Duration.ofMillis(fadein * 50L),
                            Duration.ofMillis(stay * 50L),
                            Duration.ofMillis(fadeout * 50L))));
                current++;
            }
        }.runTaskTimer((JavaPlugin) plugin, 0L, speed);
    }
}
