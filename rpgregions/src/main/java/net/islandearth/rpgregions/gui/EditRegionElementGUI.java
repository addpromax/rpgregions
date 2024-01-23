package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.effects.RegionEffectRegistry;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.registry.IRPGRegionsRegistry;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.requirements.RegionRequirementRegistry;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.rewards.RegionRewardRegistry;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EditRegionElementGUI extends RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    private final ConfiguredRegion region;
    private final IRPGRegionsRegistry<?> registry;
    private ChestGui gui;

    public EditRegionElementGUI(RPGRegions plugin, Player player, ConfiguredRegion region, IRPGRegionsRegistry<?> registry) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.region = region;
        this.registry = registry;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, region.getId());
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        PaginatedPane pane = super.generateDefaultConfig();
        StaticPane exit = new StaticPane(exitX, exitY, exitL, exitH, Pane.Priority.HIGHEST);
        // Exit item
        Material em = Material.valueOf(plugin.getConfig().getString("settings.server.gui.exit.exit"));
        ItemStack exitItem = new ItemStackBuilder(em)
                .withName(Translations.EXIT.get(player).get(0))
                .lore(Translations.EXIT_LORE.get(player))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        exit.addItem(new GuiItem(exitItem, event -> {
            new RegionCreateGUI(plugin, player, region).open();
            player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
        }), 0, 0);
        gui.addPane(exit);

        List<GuiItem> items = new ArrayList<>();
        if (registry instanceof RegionEffectRegistry) {
            for (RegionEffect effect : region.getEffects()) {
                items.add(getDefaultGuiItem(effect, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        region.getEffects().remove(effect);
                        return true;
                    }
                    return false;
                }));
            }
        } else if (registry instanceof RegionRewardRegistry) {
            for (DiscoveryReward reward : region.getRewards()) {
                items.add(getDefaultGuiItem(reward, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        region.getRewards().remove(reward);
                        return true;
                    }
                    return false;
                }));
            }
        } else if (registry instanceof RegionRequirementRegistry) {
            for (RegionRequirement requirement : region.getRequirements()) {
                items.add(getDefaultGuiItem(requirement, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        region.getRequirements().remove(requirement);
                        return true;
                    }
                    return false;
                }));
            }
        }
        pane.populateWithGuiItems(items);
        gui.update();
    }

    public GuiItem getDefaultGuiItem(IGuiEditable guiEditable, IRPGRegionsRegistry<?> registry, Predicate<ClickType> function) {
        ItemStack item = new ItemStackBuilder(registry.getIcon())
                .withName("&6" + guiEditable.getName())
                .withLore("&e&lClick &7to edit.", "&c&lShift-Click &cto remove.")
                .build();
        GuiItem guiItem = new GuiItem(item);
        guiItem.setAction(click -> {
            if (function.test(click.getClick())) {
                guiItem.setVisible(false);
                gui.update();
                return;
            }
            new EditGuiElementGUI(plugin, player, region, guiEditable).open();
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        });
        return guiItem;
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
