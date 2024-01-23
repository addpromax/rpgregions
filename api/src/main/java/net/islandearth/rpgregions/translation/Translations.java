package net.islandearth.rpgregions.translation;

import com.convallyria.languagy.api.language.Language;
import com.convallyria.languagy.api.language.key.LanguageKey;
import com.convallyria.languagy.api.language.key.TranslationKey;
import com.convallyria.languagy.api.language.translation.Translation;
import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.CompletionException;

public enum Translations {
    NEXT_PAGE(TranslationKey.of("next_page")),
    NEXT_PAGE_LORE(TranslationKey.of("next_page_lore")),
    PREVIOUS_PAGE(TranslationKey.of("previous_page")),
    PREVIOUS_PAGE_LORE(TranslationKey.of("previous_page_lore")),
    REGIONS(TranslationKey.of("regions")),
    DISCOVERED_ON(TranslationKey.of("discovered_on")),
    DISCOVERED_TITLE(TranslationKey.of("discovered_title")),
    DISCOVERED_SUBTITLE(TranslationKey.of("discovered_subtitle")),
    ALREADY_DISCOVERED_TITLE(TranslationKey.of("already_discovered_title")),
    ALREADY_DISCOVERED_SUBTITLE(TranslationKey.of("already_discovered_subtitle")),
    TELEPORT(TranslationKey.of("teleport")),
    TELEPORT_COOLDOWN(TranslationKey.of("teleport_cooldown")),
    TELEPORT_COST(TranslationKey.of("teleport_cost")),
    TELEPORT_NO_MONEY(TranslationKey.of("teleport_no_money")),
    CANNOT_TELEPORT(TranslationKey.of("cannot_teleport")),
    UNKNOWN_REGION(TranslationKey.of("unknown_region")),
    EXIT(TranslationKey.of("exit")),
    EXIT_LORE(TranslationKey.of("exit_lore")),
    CANNOT_ENTER(TranslationKey.of("cannot_enter")),
    COOLDOWN(TranslationKey.of("cooldown")),
    REGION_ENTER_ACTIONBAR(TranslationKey.of("region_enter_actionbar")),
    DISCOVERING_AREA_PLACEHOLDER(TranslationKey.of("discovering_area_placeholder")),
    REQUIREMENT_MET(TranslationKey.of("requirement_met")),
    REQUIREMENT_NOT_MET(TranslationKey.of("requirement_not_met")),
    COORDINATES(TranslationKey.of("coordinates")),
    // Fauna
    FAUNA_DISCOVER_TITLE(TranslationKey.of("fauna_discover_title")),
    FAUNA_DISCOVER_SUBTITLE(TranslationKey.of("fauna_discover_subtitle")),
    BESTIARY_DEFAULT_HOVER(TranslationKey.of("bestiary_default_hover")),
    BESTIARY_PAGE_HEADER(TranslationKey.of("bestiary_page_header"));

    private final TranslationKey key;
    private final boolean isList;

    Translations(TranslationKey key) {
        this.key = key;
        this.isList = false;
    }

    public boolean isList() {
        return isList;
    }

    private String getPath() {
        return this.toString().toLowerCase();
    }

    public void send(Player player, Object... values) {
        get(player, values).forEach((component) -> {
            final IRPGRegionsAPI plugin = RPGRegionsAPI.getAPI();
            plugin.adventure().player(player).sendMessage(component);
        });
    }

    public List<Component> get(Player player, Object... values) {
        final Translation translation = RPGRegionsAPI.getAPI().getTranslator().getTranslationFor(player, key);
        try {
            translation.format(values);
        } catch (CompletionException | UnknownFormatConversionException e) {
            RPGRegionsAPI.getAPI().getLogger().warning("Translation key '" + this.name() +
                    "' is using legacy variable format. " +
                    "Some variables may not show correctly. Please update your language files.");
        }

        translation.getTranslations().replaceAll((translationString) -> this.setPapi(player, translationString));
        return new ArrayList<>(translation.colour());
    }

    public static void generateLang(IRPGRegionsAPI plugin) {
        File lang = new File(plugin.getDataFolder() + "/lang/");
        lang.mkdirs();

        for (Language language : Language.values()) {
            final LanguageKey languageKey = language.getKey();
            try {
                plugin.saveResource("lang/" + languageKey.getCode() + ".yml", false);
                plugin.getLogger().info("Generated " + languageKey.getCode() + ".yml");
            } catch (IllegalArgumentException ignored) { }

            File file = new File(plugin.getDataFolder() + "/lang/" + languageKey.getCode() + ".yml");
            if (file.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (Translations key : values()) {
                    if (config.get(key.toString().toLowerCase()) == null) {
                        plugin.getLogger().warning("No value in translation file for key "
                                + key + " was found. Please regenerate or edit your language files with new values!");
                    }
                }
            }
        }
    }

    @NotNull
    private String setPapi(Player player, String message) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }
}
