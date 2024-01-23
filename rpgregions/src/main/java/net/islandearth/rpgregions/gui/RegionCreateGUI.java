package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.chat.preset.ConfirmConversationPreset;
import net.islandearth.rpgregions.chat.preset.ReturnValueConversationPreset;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class RegionCreateGUI extends RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    private final ConfiguredRegion region;
    private ChestGui gui;

    public RegionCreateGUI(RPGRegions plugin, Player player, ConfiguredRegion region) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.region = region;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, region.getId());
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        OutlinePane oPane = new OutlinePane(0, 1, 9, 1, Pane.Priority.HIGHEST);
        oPane.setRepeat(true);
        oPane.addItem(new GuiItem(new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .withName(" ")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build()));
        gui.addPane(oPane);

        StaticPane regionInfo = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGH);
        ItemStack regionInfoItem = new ItemStackBuilder(region.getIcon()).withModel(region.getIconModel())
                .withName("&6" + region.getCustomName())
                .withLore("&6Information:", "&fIt is recommended you edit the region file directly!", "&7&o" + region.getFile(plugin), "&7" + region.getDiscoveredLore(), "&e&lClick &7to save the region.")
                .withModel(region.getIconModel())
                .build();
        regionInfo.addItem(new GuiItem(regionInfoItem, event -> {
            region.save(plugin);
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
        }),0, 0);
        gui.addPane(regionInfo);

        StaticPane undiscoveredModel = new StaticPane(7, 0, 1, 1, Pane.Priority.HIGH);
        ItemStack undiscoveredModelItem = new ItemStackBuilder(region.getUndiscoveredIcon().getType())
                .withName("&6Set undiscovered icon material & model &7- &6" + region.getUndiscoveredIcon().getType() + " (" + region.getUndiscoveredIconModel() + ")")
                .withLore("&7Set the material & custom model data of the icon.", "", "&e&lClick &7to set material and icon model.")
                .withModel(region.getUndiscoveredIconModel())
                .build();
        undiscoveredModel.addItem(new GuiItem(undiscoveredModelItem, event -> {
            new ReturnValueConversationPreset(player, "Enter the material and/or model, split using ':'. Example: TOTEM_OF_UNDYING:1", input -> {
                final String[] split = input.split(":");
                try {
                    final Optional<Material> materialOptional = Enums.getIfPresent(Material.class, split[0]);
                    if (!materialOptional.isPresent()) {
                        player.sendMessage(ChatColor.RED + "'" + split[0] + "' is not a valid material.");
                        open();
                        return;
                    }

                    try {
                        final int potentialModel = Integer.parseInt(split[1]);
                        region.setUndiscoveredIconModel(potentialModel);
                    } catch (IndexOutOfBoundsException ignored) {}
                    region.setUndiscoveredIcon(materialOptional.get());
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "'" + split[1] + "' is not a valid number.");
                }
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(undiscoveredModel);

        StaticPane model = new StaticPane(8, 0, 1, 1, Pane.Priority.HIGH);
        ItemStack modelItem = new ItemStackBuilder(region.getIcon().getType())
                .withName("&6Set icon material & model &7- &6" + region.getIcon().getType() + " (" + region.getIconModel() + ")")
                .withLore("&7Set the material & custom model data of the icon.", "", "&e&lClick &7to set material and icon model.")
                .withModel(region.getIconModel())
                .build();
        model.addItem(new GuiItem(modelItem, event -> {
            new ReturnValueConversationPreset(player, "Enter the material and/or model, split using ':'. Example: TOTEM_OF_UNDYING:1", input -> {
                final String[] split = input.split(":");
                try {
                    final Optional<Material> materialOptional = Enums.getIfPresent(Material.class, split[0]);
                    if (!materialOptional.isPresent()) {
                        player.sendMessage(ChatColor.RED + "'" + split[0] + "' is not a valid material.");
                        open();
                        return;
                    }

                    try {
                        final int potentialModel = Integer.parseInt(split[1]);
                        region.setIconModel(potentialModel);
                    } catch (IndexOutOfBoundsException ignored) {}
                    region.setIcon(materialOptional.get());
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "'" + split[1] + "' is not a valid number.");
                }
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(model);

        StaticPane displayName = new StaticPane(0, 2, 1, 1, Pane.Priority.HIGH);
        ItemStack displayNameItem = new ItemStackBuilder(Material.PAPER)
                .withName("&6Set region display name &7- &6" + region.getCustomName())
                .withLore("&7Your region can be renamed at any time.", "&e&lClick &7to rename the region.")
                .build();
        displayName.addItem(new GuiItem(displayNameItem, event -> {
            new ReturnValueConversationPreset(player, "What display name should this region have?", input -> {
                region.setCustomName(input);
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(displayName);

        /*StaticPane description = new StaticPane(0, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack descriptionItem = new ItemStackBuilder(Material.PAPER)
                .withName("&6Set region description &7- &6" + region.getDiscoveredLore())
                .withLore("&7What description the region should have.", "&e&lClick &7to set the region description.")
                .build();
        description.addItem(new GuiItem(descriptionItem, event -> {
            ConversationFactory factory = new ConversationFactory(plugin)
                    .withModality(true)
                    .withPrefix(new RPGRegionsConversationPrefix())
                    .withFirstPrompt(new RPGRegionsStringPrompt("What description should this region have?"))
                    .withEscapeSequence("quit")
                    .withLocalEcho(true)
                    .withTimeout(60);
            Conversation conversation = factory.buildConversation(player);
            conversation.begin();
            conversation.addConversationAbandonedListener(abandonedEvent -> {
                region.setDescription((String) abandonedEvent.getContext().getSessionData("input"));
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(description);*/

        StaticPane delete = new StaticPane(0, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack deleteItem = new ItemStackBuilder(Material.REDSTONE)
                .withName("&c&lDelete Region")
                .withLore(" ", "&cThis action cannot be undone.", " ", "&c&lShift-Click &7to delete the region.")
                .build();
        delete.addItem(new GuiItem(deleteItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                new ConfirmConversationPreset(player, accepted -> {
                    if (accepted) {
                        plugin.getManagers().getRegionsCache().removeConfiguredRegion(region.getId());
                        region.delete(plugin);
                        player.sendMessage(ChatColor.RED + "Region has been deleted.");
                        return;
                    }
                    open();
                });
            }
        }),0, 0);
        gui.addPane(delete);

        StaticPane time = new StaticPane(0, 4, 1, 1, Pane.Priority.HIGH);
        ItemStack timeItem = new ItemStackBuilder(Material.SAND)
                .withName("&6Set region timer &7- &6" + region.getSecondsInsideToDiscover())
                .withLore("&7How long should the player have to", "&7stay in the region to discover it?", "", "&e&lClick &7to set region timer.")
                .build();
        time.addItem(new GuiItem(timeItem, event -> {
            new ReturnValueConversationPreset(player, "How long should the player have to stay in the region to discover it? (in seconds)", input -> {
                try {
                    final int potentialTime = Integer.parseInt(input);
                    region.setSecondsInsideToDiscover(potentialTime);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "'" + input + "' is not a valid number.");
                }
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(time);

        StaticPane teleport = new StaticPane(0, 5, 1, 1, Pane.Priority.HIGH);
        ItemStack teleportItem = new ItemStackBuilder(Material.ENDER_PEARL)
                .withName("&6Set teleport cooldown &7- &6" + region.getTeleportCooldown())
                .withLore("&7How long should the player have to", "&7wait until teleporting to the region again?", "", "&e&lClick &7to set teleport cooldown.")
                .build();
        teleport.addItem(new GuiItem(teleportItem, event -> {
            new ReturnValueConversationPreset(player, "How long should the player have to wait to teleport to the region again? (in ticks)", input -> {
                try {
                    final int potentialTime = Integer.parseInt(input);
                    region.setTeleportCooldown(potentialTime);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "'" + input + "' is not a valid number.");
                }
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(teleport);

        int x = 4;
        for (RPGRegionsRegistry<?> registry : plugin.getManagers().getRegistries()) {
            StaticPane registryPane = new StaticPane(x, 2, 1, 1, Pane.Priority.HIGH);
            ItemStackBuilder registryItem = new ItemStackBuilder(registry.getIcon())
                    .withName("&6" + registry.getRegistryName());
            for (String description : registry.getDescription()) {
                registryItem.withLore(description); // Use this to translate colours
            }

            registryPane.addItem(new GuiItem(registryItem.build(), event -> {
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
                new QueryGUI(plugin, player, region, registry).open();
            }),0, 0);
            gui.addPane(registryPane);
            x = x + 2;
        }

        StaticPane hidden = new StaticPane(6, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack hiddenItem = new ItemStackBuilder(Material.ORANGE_BANNER)
                .withName("&6Toggle hidden region &7- &6" + region.isHidden())
                .withLore("&7A hidden region does", "&7not appear in GUIs.", "&c&lShift-Click &7to toggle hidden region.")
                .build();
        hidden.addItem(new GuiItem(hiddenItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setHidden(!region.isHidden());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(hidden);

        StaticPane discoverable = new StaticPane(6, 4, 1, 1, Pane.Priority.HIGH);
        ItemStack discoverableItem = new ItemStackBuilder(Material.GREEN_BANNER)
                .withName("&6Toggle discoverable &7- &6" + region.isDiscoverable())
                .withLore("&7Whether this region can be discovered.", "&c&lShift-Click &7to toggle discoverable.")
                .build();
        discoverable.addItem(new GuiItem(discoverableItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setDiscoverable(!region.isDiscoverable());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(discoverable);

        StaticPane teleportable = new StaticPane(6, 5, 1, 1, Pane.Priority.HIGH);
        ItemStack teleportableItem = new ItemStackBuilder(Material.RED_BANNER)
                .withName("&6Toggle teleportable &7- &6" + region.isTeleportable())
                .withLore("&7Whether this region can be teleported to.", "&c&lShift-Click &7to toggle teleportable.")
                .build();
        teleportable.addItem(new GuiItem(teleportableItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setTeleportable(!region.isTeleportable());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(teleportable);

        StaticPane showHint = new StaticPane(7, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack showHintItem = new ItemStackBuilder(Material.BLUE_BANNER)
                .withName("&6Toggle show hint &7- &6" + region.isTeleportable())
                .withLore("&7Whether the hint for this region is shown.", "&c&lShift-Click &7to toggle show hint.")
                .build();
        showHint.addItem(new GuiItem(showHintItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setShowHint(!region.showHint());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(showHint);

        StaticPane dynmap = new StaticPane(7, 4, 1, 1, Pane.Priority.HIGH);
        ItemStack dynmapItem = new ItemStackBuilder(Material.FILLED_MAP)
                .withName("&6Toggle dynmap &7- &6" + region.dynmap())
                .withLore("&7Whether this region is shown on dynmap.", "&c&lShift-Click &7to toggle dynmap.")
                .build();
        dynmap.addItem(new GuiItem(dynmapItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setDynmap(!region.dynmap());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(dynmap);

        StaticPane actionbar = new StaticPane(7, 5, 1, 1, Pane.Priority.HIGH);
        ItemStack actionbarItem = new ItemStackBuilder(Material.BOOK)
                .withName("&6Toggle actionbar &7- &6" + region.showActionbar())
                .withLore("&7Whether this region is shown in the actionbar on entry.", "&c&lShift-Click &7to toggle actionbar.")
                .build();
        actionbar.addItem(new GuiItem(actionbarItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setShowActionbar(!region.showActionbar());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(actionbar);

        StaticPane coords = new StaticPane(8, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack coordsItem = new ItemStackBuilder(Material.MAP)
                .withName("&6Toggle coords &7- &6" + region.showCoords())
                .withLore("&7Whether the coordinates of this region are shown.", "&c&lShift-Click &7to toggle coords.")
                .build();
        coords.addItem(new GuiItem(coordsItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setShowCoords(!region.showCoords());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(coords);

        StaticPane passthrough = new StaticPane(8, 4, 1, 1, Pane.Priority.HIGH);
        ItemStack passthroughItem = new ItemStackBuilder(Material.BARRIER)
                .withName("&6Toggle disable passthrough &7- &6" + region.disablesPassthrough())
                .withLore("&7If this is the prioritised region and disable passthrough is set to true,", "&7 only the prioritised region shall run.", "&c&lShift-Click &7to toggle passthrough.")
                .build();
        passthrough.addItem(new GuiItem(passthroughItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                region.setDisablePassthrough(!region.disablesPassthrough());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(passthrough);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
