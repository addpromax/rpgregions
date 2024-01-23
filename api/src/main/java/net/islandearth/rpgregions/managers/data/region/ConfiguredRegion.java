package net.islandearth.rpgregions.managers.data.region;

import com.google.gson.Gson;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.command.IconCommand;
import net.islandearth.rpgregions.editor.annotate.EditableField;
import net.islandearth.rpgregions.editor.annotate.NeedsGUI;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.regenerate.Regenerate;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.translation.Translations;
import net.islandearth.rpgregions.utils.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ConfiguredRegion {

    private UUID world;
    private String id;
    private String customName;
    private final List<DiscoveryReward> rewards;
    private String sound;
    private String icon;
    private int iconModel;
    private int undiscoveredIconModel;
    private String undiscoveredIcon;
    private final List<IconCommand> iconCommand;
    private boolean showCoords;
    private Location location;
    @NeedsGUI private final List<String> hints;
    private boolean showHint;
    private boolean teleportable;
    private boolean hidden;
    private boolean discoverable;
    private final List<RegionEffect> effects;
    private final List<RegionRequirement> requirements;
    private final List<String> discoveredLore;
    @EditableField(description = "Toggle whether the title is always shown on entry after discovery", name = "Toggle always showing titles")
    @NeedsGUI private final boolean alwaysShowTitles;
    private List<String> title;
    private List<String> subtitle;
    private List<String> discoveredTitle;
    private List<String> discoveredSubtitle;
    private Regenerate regenerate;
    private int teleportCooldown;
    @EditableField(material = Material.NETHER_STAR, name = "Set teleport cost", description = "Set the cost for teleportation")
    private double teleportCost;
    private boolean showActionbar;
    private double opacity, lineOpacity;
    @EditableField(material = Material.RED_DYE, name = "Hex display colour", description = "Set the colour of the region. It is a hex colour (e.g 0x42f4f1 for red) and is used in dynmap.")
    private final String colour;
    private final String lineColour;
    private boolean dynmap;
    private int secondsInsideToDiscover;
    // If this is the prioritised region and disablePassthrough is set to true, only the prioritised region shall run
    private boolean disablePassthrough;

    public ConfiguredRegion(@Nullable World world, String id, String customName,
                            List<DiscoveryReward> rewards, List<RegionEffect> effects) {
        this.world = world != null ? world.getUID() : null;
        this.id = id;
        this.customName = customName;
        this.rewards = rewards;
        this.sound = Sound.UI_TOAST_CHALLENGE_COMPLETE.name();
        Optional<Material> defaultIcon = Optional.of(Material.valueOf(RPGRegionsAPI.getAPI().getConfig().getString("settings.server.gui.default_region_icon")));
        this.icon = defaultIcon.map(Enum::name).orElseGet(Material.TOTEM_OF_UNDYING::name);
        this.undiscoveredIcon = defaultIcon.map(Enum::name).orElseGet(Material.TOTEM_OF_UNDYING::name);
        this.iconCommand = new ArrayList<>();
        this.showCoords = false;
        this.hints = new ArrayList<>();
        this.showHint = false;
        this.teleportable = false;
        this.hidden = false;
        this.discoverable = true;
        this.effects = effects;
        this.requirements = new ArrayList<>();
        this.discoveredLore = new ArrayList<>();
        this.alwaysShowTitles = false;
        this.teleportCooldown = 0;
        this.teleportCost = 0.00;
        this.showActionbar = true;
        this.opacity = this.lineOpacity = 0.5;
        this.colour = String.valueOf(13369344);
        this.lineColour = String.valueOf(13369344);
        this.dynmap = true;
    }

    public ConfiguredRegion(@Nullable World world, String id, String customName,
                            List<DiscoveryReward> rewards, List<RegionEffect> effects, Sound sound, Material icon) {
        this(world, id, customName, rewards, effects);
        this.sound = sound.name();
        this.icon = icon.name();
    }

    public String getId() {
        return id;
    }

    @Deprecated //todo find better way to do this (templates)
    public void setId(String id) {
        this.id = id;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public List<DiscoveryReward> getRewards() {
        return rewards;
    }

    public Optional<String> getSoundName() {
        return Optional.ofNullable(sound);
    }

    public void setSoundName(String sound) {
        this.sound = sound;
    }

    @Nullable
    public ItemStack getIcon() {
        if (icon == null) return new ItemStack(Material.TOTEM_OF_UNDYING);
        if (icon.startsWith("hdb-") && RPGRegionsAPI.getAPI().hasHeadDatabase()) return new ItemStackBuilder(new HeadDatabaseAPI().getItemHead(icon.replace("hdb-", ""))).withModel(iconModel).build();
        return new ItemStackBuilder(Material.valueOf(icon)).withModel(iconModel).build();
    }

    public void setIcon(@NotNull Material material) {
        this.icon = material.name();
    }

    public int getIconModel() {
        return iconModel;
    }

    public void setIconModel(int iconModel) {
        this.iconModel = iconModel;
    }

    @Nullable
    public ItemStack getUndiscoveredIcon() {
        if (undiscoveredIcon == null) return new ItemStack(Material.TOTEM_OF_UNDYING);
        if (undiscoveredIcon.startsWith("hdb-") && RPGRegionsAPI.getAPI().hasHeadDatabase()) return new ItemStackBuilder(new HeadDatabaseAPI().getItemHead(undiscoveredIcon.replace("hdb-", ""))).withModel(undiscoveredIconModel).build();
        return new ItemStackBuilder(Material.valueOf(undiscoveredIcon)).withModel(undiscoveredIconModel).build();
    }

    public void setUndiscoveredIcon(@NotNull Material material) {
        this.undiscoveredIcon = material.name();
    }

    public int getUndiscoveredIconModel() {
        return undiscoveredIconModel;
    }

    public void setUndiscoveredIconModel(int undiscoveredIconModel) {
        this.undiscoveredIconModel = undiscoveredIconModel;
    }

    @NotNull
    public List<IconCommand> getIconCommand() {
        return iconCommand == null ? new ArrayList<>() : iconCommand;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean showCoords() {
        return showCoords;
    }

    public void setShowCoords(boolean showCoords) {
        this.showCoords = showCoords;
    }

    @Nullable
    public List<String> getHints() {
        return hints;
    }

    public boolean showHint() {
        return showHint;
    }

    public void setShowHint(boolean showHint) {
        this.showHint = showHint;
    }

    public boolean isTeleportable() {
        return teleportable;
    }

    public void setTeleportable(boolean teleportable) {
        this.teleportable = teleportable;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    @Deprecated //todo find better way to do this (templates)
    public void setWorld(UUID world) {
        this.world = world;
    }

    public boolean isDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(boolean discoverable) {
        this.discoverable = discoverable;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Nullable
    public List<RegionEffect> getEffects() {
        return effects;
    }

    @Nullable
    public List<RegionRequirement> getRequirements() {
        return requirements;
    }

    @Nullable
    public List<String> getDiscoveredLore() {
        return discoveredLore;
    }

    public boolean alwaysShowTitles() {
        return alwaysShowTitles;
    }

    /**
     * Gets the region title for a player. If region title is null, the translation files will be used.
     * @param player the player
     * @return A string list of title
     */
    @NotNull
    public List<Component> getTitle(Player player) {
        if (title == null) {
            return Translations.DISCOVERED_TITLE.get(player, customName);
        }

        List<Component> translatedTitle = new ArrayList<>();
        title.forEach(titles -> translatedTitle.add(RPGRegionsAPI.getAPI().miniMessage().deserialize(titles)));
        return translatedTitle;
    }

    /**
     * Gets the region subtitle for a player. If region subtitle is null, the translation files will be used.
     * @param player the player
     * @return A string list of subtitles
     */
    @NotNull
    public List<Component> getSubtitle(Player player) {
        if (subtitle == null) {
            return Translations.DISCOVERED_SUBTITLE.get(player, customName);
        }

        List<Component> translatedSubtitle = new ArrayList<>();
        subtitle.forEach(sub -> translatedSubtitle.add(RPGRegionsAPI.getAPI().miniMessage().deserialize(sub)));
        return translatedSubtitle;
    }

    /**
     * Gets the region discovered title for a player. If region title is null, the translation files will be used.
     * @param player the player
     * @return A string list of title
     */
    @NotNull
    public List<Component> getDiscoveredTitle(Player player) {
        if (discoveredTitle == null) {
            return Translations.ALREADY_DISCOVERED_TITLE.get(player, customName);
        }

        List<Component> translatedTitle = new ArrayList<>();
        discoveredTitle.forEach(titles -> translatedTitle.add(RPGRegionsAPI.getAPI().miniMessage().deserialize(titles)));
        return translatedTitle;
    }

    /**
     * Gets the region discovered subtitle for a player. If region subtitle is null, the translation files will be used.
     * @param player the player
     * @return A string list of subtitles
     */
    @NotNull
    public List<Component> getDiscoveredSubtitle(Player player) {
        if (discoveredSubtitle == null) {
            return Translations.ALREADY_DISCOVERED_SUBTITLE.get(player, customName);
        }

        List<Component> translatedSubtitle = new ArrayList<>();
        discoveredSubtitle.forEach(sub -> translatedSubtitle.add(RPGRegionsAPI.getAPI().miniMessage().deserialize(sub)));
        return translatedSubtitle;
    }

    @Nullable
    public Regenerate getRegenerate() {
        return regenerate;
    }

    public void setRegenerate(@NotNull Regenerate regenerate) {
        this.regenerate = regenerate;
    }

    public int getTeleportCooldown() {
        return teleportCooldown;
    }

    public double getTeleportCost(Player player) {
        double cost = teleportCost;
        if (RPGRegionsAPI.getAPI().getConfig().getBoolean("settings.teleport.permission-based-cost") && cost == 0.00) {
            for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
                if (perm.getPermission().startsWith("rpgregions.teleport" + ".")) {
                    String priceExpression = perm.getPermission()
                            .substring(perm.getPermission().lastIndexOf(".") + 1);
                    priceExpression = priceExpression.replace("n",
                            Math.round(player.getLocation().distance(this.location)) + "");
                    CompiledExpression exp = Crunch.compileExpression(priceExpression);
                    cost = exp.evaluate();
                }
            }
        }
        return cost;
    }

    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
    }

    public void setTeleportCost(int teleportCost) { this.teleportCost = teleportCost; }

    public boolean showActionbar() {
        return showActionbar;
    }

    public void setShowActionbar(boolean showActionbar) {
        this.showActionbar = showActionbar;
    }

    @Nullable
    public List<Location> getBoundingBox() {
        return RPGRegionsAPI.getAPI().getManagers().getIntegrationManager().getBoundingBoxPoints(this);
    }

    public double getOpacity() {
        return opacity;
    }

    public String getColour() {
        if (colour == null) return "0";
        return colour;
    }

    public double getLineOpacity() {
        return lineOpacity;
    }

    public String getLineColour() {
        if (lineColour == null) return "0";
        return lineColour;
    }

    public boolean dynmap() {
        return dynmap;
    }

    public void setDynmap(boolean dynmap) {
        this.dynmap = dynmap;
    }

    public int getSecondsInsideToDiscover() {
        return secondsInsideToDiscover;
    }

    public void setSecondsInsideToDiscover(int secondsInsideToDiscover) {
        this.secondsInsideToDiscover = secondsInsideToDiscover;
    }

    public boolean isTimedRegion() {
        return secondsInsideToDiscover != 0;
    }

    public boolean disablesPassthrough() {
        return disablePassthrough;
    }

    public void setDisablePassthrough(boolean disablePassthrough) {
        this.disablePassthrough = disablePassthrough;
    }

    public File getFile(IRPGRegionsAPI plugin) {
        File file = null;
        try {
            file = this.findFile(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file == null) file = new File(plugin.getDataFolder() + "/regions/" + this.id + ".json");
        return file;
    }

    public void save(IRPGRegionsAPI plugin, File file) {
        try {
            if (file == null) file = new File(plugin.getDataFolder() + "/regions/" + this.id + ".json");
            try (Writer writer = new FileWriter(file)) {
                Gson gson = plugin.getGson();
                gson.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(IRPGRegionsAPI plugin) {
        try {
            this.save(plugin, this.findFile(plugin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(IRPGRegionsAPI plugin) {
        try {
            File file = this.findFile(plugin);
            if (file == null) file = new File(plugin.getDataFolder() + "/regions/" + this.id + ".json");
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    private File findFile(IRPGRegionsAPI plugin) throws IOException {
        File folder = new File(plugin.getDataFolder() + "/regions/");
        Stream<Path> files = Files.walk(Paths.get(folder.getPath()));
        List<Path> valid = files.filter(path -> Files.isRegularFile(path) && path.toFile().getName().equals(this.id + ".json")).toList();
        files.close();
        if (valid.isEmpty()) return null;
        if (valid.size() > 1) {
            plugin.getLogger().severe("Duplicate region files have been found for " + this.id + ". " +
                    "In order to protect your data, the plugin will NOT save the region config.");
            throw new IOException("Duplicate region file");
        }

        File file = null;
        for (Path path : valid) {
            file = path.toFile();
        }
        return file;
    }
}
