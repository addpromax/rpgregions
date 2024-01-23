package net.islandearth.rpgregions.fauna;

import com.google.common.base.Enums;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.fauna.trigger.FaunaTrigger;
import net.islandearth.rpgregions.fauna.trigger.KillFaunaTrigger;
import net.islandearth.rpgregions.fauna.trigger.NearbyFaunaTrigger;
import net.islandearth.rpgregions.managers.data.fauna.IFaunaCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FaunaCache implements IFaunaCache {

    private final RPGRegions plugin;
    private final List<FaunaInstance<?>> fauna = new ArrayList<>();

    public FaunaCache(RPGRegions plugin) {
        this.plugin = plugin;
        this.reload();
    }

    @Override
    public void reload() {
        fauna.clear();
        plugin.saveResource("fauna/goat.yml", false);
        File faunaFolder = new File(plugin.getDataFolder() + File.separator + "fauna");
        for (File file : faunaFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (!config.getBoolean("enabled", true)) continue;
            final String typeName = config.getString("type", "");
            final String identifier = config.getString("identifier", "");
            final String name = config.getString("display_name", "");
            final List<String> description = config.getStringList("description");

            List<FaunaTrigger> triggers = new ArrayList<>();
            final ConfigurationSection triggersSection = config.getConfigurationSection("triggers");
            for (String triggerId : triggersSection.getKeys(false)) {
                final ConfigurationSection triggerSection = triggersSection.getConfigurationSection(triggerId);
                if (triggerId.equals("nearby")) {
                    final double radius = triggerSection.getDouble("radius");
                    triggers.add(new NearbyFaunaTrigger(radius));
                } else if (triggerId.equals("kill")) {
                    triggers.add(new KillFaunaTrigger());
                }
            }

            final EntityType entityType = Enums.getIfPresent(EntityType.class, typeName).orNull();
            if (entityType != null) {
                this.addFauna(new VanillaMobFaunaInstance(identifier, name, description, entityType, triggers));
            } else {
                final Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(typeName);
                mythicMob.ifPresent(mob -> this.addFauna(new MythicMobFaunaInstance(identifier, name, description, mob, triggers)));
            }
        }
    }

    @Override
    public List<FaunaInstance<?>> getFauna() {
        return fauna;
    }

    @Override
    public Optional<FaunaInstance<?>> getFauna(String id) {
        return fauna.stream().filter(instance -> instance.getIdentifier().equals(id)).findFirst();
    }

    @Override
    public void addFauna(FaunaInstance<?> instance) {
        fauna.add(instance);
    }
}
