package net.islandearth.rpgregions.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(Material mat) {
        this.ITEM_STACK = new ItemStack(mat);
    }

    public ItemStackBuilder(@Nullable ItemStack item) {
        this.ITEM_STACK = item == null
                ? new ItemStackBuilder(new ItemStack(Material.BARRIER)).withName(ChatColor.RED + "Item is null").build()
                : item;
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withName(Component name) {
        return withName(LegacyComponentSerializer.legacyAmpersand().serialize(name));
    }

    public ItemStackBuilder withLore(String name) {
        if (name == null) return this;
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(Component name) {
        return withLore(LegacyComponentSerializer.legacyAmpersand().serialize(name));
    }

    public ItemStackBuilder withLore(@Nullable List<String> name) {
        if (name == null) return this;
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = name;
        } else {
            lore.addAll(name);
        }
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder lore(@Nullable List<Component> name) {
        if (name == null) return this;
        List<String> legacy = new ArrayList<>();
        for (Component component : name) {
            legacy.add(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(component)));
        }
        return withLore(legacy);
    }

    public ItemStackBuilder withLore(String... name) {
        List<String> lines = new ArrayList<>();
        for (String lore : name) {
            lines.add(ChatColor.translateAlternateColorCodes('&', lore));
        }
        return withLore(lines);
    }

    public ItemStackBuilder withDurability(int durability) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final Damageable damageable = (Damageable) meta;
        damageable.setDamage(durability);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addFlags(final ItemFlag... flags) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(flags);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder withSkullOwner(final OfflinePlayer player) {
        Material type = ITEM_STACK.getType();
        if (type == Material.PLAYER_HEAD) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(player.getName());
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withSkullOwner is only applicable for skulls!");
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder setSkullOwner(final UUID uuid) {
        Material type = ITEM_STACK.getType();
        if (type == Material.PLAYER_HEAD) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(Bukkit.getPlayer(uuid).getName());
            return this;
        } else {
            throw new IllegalArgumentException("withSkullOwner is only applicable for skulls!");
        }
    }

    public ItemStackBuilder generation(BookMeta.Generation generation) {
        Material type = ITEM_STACK.getType();
        if (type == Material.WRITTEN_BOOK) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final BookMeta bookMeta = (BookMeta) meta;
            bookMeta.setGeneration(generation);
            return this;
        } else {
            throw new IllegalArgumentException("generation is only applicable for written books!");
        }
    }

    public ItemStackBuilder withModel(int model) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(model);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setLore(new ArrayList<>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS
                || type == Material.LEATHER_CHESTPLATE
                || type == Material.LEATHER_HELMET
                || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return ITEM_STACK;
    }
}
