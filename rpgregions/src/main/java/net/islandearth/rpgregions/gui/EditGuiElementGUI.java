package net.islandearth.rpgregions.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.chat.RPGRegionsConversationPrefix;
import net.islandearth.rpgregions.chat.RPGRegionsStringPrompt;
import net.islandearth.rpgregions.gui.element.ICustomGuiFeedback;
import net.islandearth.rpgregions.gui.element.IGuiFieldElement;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import net.islandearth.rpgregions.utils.ReflectionUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditGuiElementGUI extends RPGRegionsGUI {

    private final RPGRegions plugin;
    private final Player player;
    private final ConfiguredRegion region;
    private final IGuiEditable guiEditable;
    private ChestGui gui;

    public EditGuiElementGUI(RPGRegions plugin, Player player, ConfiguredRegion region, IGuiEditable guiEditable) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.region = region;
        this.guiEditable = guiEditable;
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
        gui.setTitle("Loading...");
        CompletableFuture<List<Field>> future = ReflectionUtils.getSuperFieldsFromAnnotationAsync(guiEditable.getClass(), GuiEditable.class);
        future.thenAccept(fields -> {
            for (Field field : fields) {
                GuiEditable annotation = field.getAnnotation(GuiEditable.class);
                String name = annotation.value();
                ItemStack item = new ItemStackBuilder(annotation.icon())
                        .withName("&6" + name + "&7 - &6" + getField(field))
                        .withLore("&e&lClick &7to set value.").build();
                GuiItem guiItem = new GuiItem(item, click -> {
                    IGuiFieldElement element = plugin.getManagers().getGuiFieldElementRegistry().fromClass(field.getType());
                    if (element != null) {
                        if (!element.needsValue()) { // Just toggle it.
                            element.set(player, guiEditable, field, null).thenAccept(done -> open());
                        } else {
                            if (annotation.type() == GuiEditable.GuiEditableType.CHAT) {
                                String info = getInfoIfApplicable(field);
                                if (info != null) player.sendMessage(ChatColor.GREEN + "Info provided: " + ChatColor.GRAY + info);
                                ConversationFactory factory = new ConversationFactory(plugin)
                                        .withModality(true)
                                        .withPrefix(new RPGRegionsConversationPrefix())
                                        .withFirstPrompt(new RPGRegionsStringPrompt("Enter value:"))
                                        .withEscapeSequence("quit")
                                        .withLocalEcho(true)
                                        .withTimeout(60);
                                Conversation conversation = factory.buildConversation(player);
                                conversation.begin();
                                conversation.addConversationAbandonedListener(abandonedEvent -> {
                                    String input = (String) abandonedEvent.getContext().getSessionData("input");
                                    boolean flag = false;
                                    if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
                                        ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
                                        flag = customGuiFeedback.feedback(player, input);
                                    }

                                    if (!flag) {
                                        element.set(player, guiEditable, field, input);
                                    }
                                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                    open();
                                });
                                player.closeInventory();
                                return;
                            }

                            new AnvilGUI.Builder()
                                .onClose(player -> open())
                                .onClickAsync((slot, stateSnapshot) -> {
                                    final String text = stateSnapshot.getText();
                                    if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
                                        ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
                                        boolean flag = customGuiFeedback.feedback(player, text);
                                        if (flag) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                            return CompletableFuture.completedFuture(List.of(AnvilGUI.ResponseAction.close()));
                                        }
                                    }

                                    CompletableFuture<List<AnvilGUI.ResponseAction>> responses = new CompletableFuture<>();
                                    element.set(player, guiEditable, field, text).thenAccept(done -> {
                                        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                        responses.complete(List.of(AnvilGUI.ResponseAction.close()));
                                        open();
                                    });
                                    return responses;
                                })
                                .text("Enter value")
                                .itemLeft(new ItemStack(Material.WRITABLE_BOOK))
                                .title("Set value of this element")
                                .plugin(plugin)
                                .open(player);
                        }
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                });
                items.add(guiItem);
            }

            plugin.getScheduler().executeOnMain(() -> { // GUI updates must be sync.
                pane.populateWithGuiItems(items);
                gui.setTitle(region.getId());
                gui.update();
            });
        });
        pane.populateWithGuiItems(items);
        gui.update();
    }

    private String getInfoIfApplicable(Field field) {
        if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
            ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
            return customGuiFeedback.info(field.getName());
        }
        return null;
    }

    private Object getField(Field field) { // Don't throw exceptions for cleaner code elsewhere
        try {
            field.setAccessible(true);
            return field.get(guiEditable);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
