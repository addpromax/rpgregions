package net.islandearth.rpgregions.fauna;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.fauna.trigger.FaunaTrigger;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class FaunaInstance<M> {

    private final String identifier;
    private final String name;
    private final List<String> description;
    private final M type;
    private final List<FaunaTrigger> triggers;

    public FaunaInstance(String identifier, String name, List<String> description, M type, List<FaunaTrigger> triggers) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.type = type;
        this.triggers = triggers;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void openDescription(Player player) {
        player.playSound(player.getEyeLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
        final IRPGRegionsAPI plugin = RPGRegionsAPI.getAPI();
        final Book.Builder builder = Book.builder();
        builder.title(Component.text("Bestiary", NamedTextColor.DARK_PURPLE));
        // Lol, I wonder if some redistribution sites will auto-remove this line?
        // Would cause errors.
        builder.author(Component.text("RPGRegions %%__USER__%%"));
        List<Component> pages = new ArrayList<>();
        TextComponent.Builder pageBuilder = Component.text();
        for (String line : description) {
            if (line.equals("<newpage>")) {
                pages.add(pageBuilder.build());
                pageBuilder = Component.text();
                continue;
            }
            pageBuilder.append(plugin.miniMessage().deserialize(line));
            pageBuilder.appendNewline();
        }
        pages.add(pageBuilder.build());
        builder.pages(pages);
        plugin.adventure().player(player).openBook(builder.build());
    }

    public M getType() {
        return type;
    }

    public List<FaunaTrigger> getTriggers() {
        return triggers;
    }

    public boolean hasTrigger(Class<? extends FaunaTrigger> type) {
        return triggers.stream().anyMatch(trigger -> trigger.getClass().isAssignableFrom(type));
    }
}
