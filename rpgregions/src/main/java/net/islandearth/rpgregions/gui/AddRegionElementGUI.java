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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AddRegionElementGUI extends RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    private final ConfiguredRegion region;
    private final IRPGRegionsRegistry<?> registry;
    private ChestGui gui;

    protected AddRegionElementGUI(RPGRegions plugin, Player player, ConfiguredRegion region, IRPGRegionsRegistry<?> registry) {
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
        registry.get().forEach((name, clazz) -> {
            ItemStack item = new ItemStackBuilder(Material.WRITTEN_BOOK).withName("&6" + name).generation(null).build();
            GuiItem guiItem = new GuiItem(item, click -> {
                Object newInstance = registry.getNew(name, plugin, region);
                if (newInstance == null) {
                    player.sendMessage(ChatColor.RED + "This requires a newer Minecraft version or a plugin which is not installed.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    return;
                }

                if (registry instanceof RegionEffectRegistry) {
                    region.getEffects().add((RegionEffect) newInstance);
                } else if (registry instanceof RegionRewardRegistry) {
                    region.getRewards().add((DiscoveryReward) newInstance);
                } else if (registry instanceof RegionRequirementRegistry) {
                    region.getRequirements().add((RegionRequirement) newInstance);
                }

                new EditRegionElementGUI(plugin, player, region, registry).open();
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
            });
            items.add(guiItem);
        });
        pane.populateWithGuiItems(items);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
