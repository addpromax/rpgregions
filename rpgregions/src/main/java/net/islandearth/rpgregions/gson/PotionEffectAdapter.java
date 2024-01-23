package net.islandearth.rpgregions.gson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.islandearth.rpgregions.RPGRegions;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.Map;

public class PotionEffectAdapter implements JsonSerializer<PotionEffect>, JsonDeserializer<PotionEffect> {

    private final RPGRegions plugin;
    private final Gson gson;

    public PotionEffectAdapter(RPGRegions plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public PotionEffect deserialize(JsonElement jsonElement, Type type,
                                 JsonDeserializationContext context) {
        return this.deserialise(gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType()));
    }

    @Override
    public JsonElement serialize(PotionEffect potionEffect, Type type, JsonSerializationContext context) {
        // Our own serialisation - use type name instead of id
        return gson.toJsonTree(ImmutableMap.<String, Object>builder()
            .put("type", potionEffect.getType().getName())
            .put("duration", potionEffect.getDuration())
            .put("amplifier", potionEffect.getAmplifier())
            .put("ambient", potionEffect.isAmbient())
            .put("particles", potionEffect.hasParticles())
            .put("icon", potionEffect.hasIcon())
            .build());
    }

    /*
     Our own deserialisation - use type name instead of id
     */
    private PotionEffect deserialise(Map<String, Object> map) {
        PotionEffectType type = PotionEffectType.getByName((String) map.get("type"));
        double duration = (double) map.get("duration");
        double amplifier = (double) map.get("amplifier");
        boolean ambient = (boolean) map.get("ambient");
        boolean particles = (boolean) map.get("particles");
        boolean icon = (boolean) map.get("icon");
        return new PotionEffect(type, (int) duration, (int) amplifier, ambient, particles, icon);
    }
}