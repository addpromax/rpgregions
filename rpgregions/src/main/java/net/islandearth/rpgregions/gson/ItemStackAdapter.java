package net.islandearth.rpgregions.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import me.lucko.helper.serialize.Serializers;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private final Gson gson;

    public ItemStackAdapter() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type,
                                 JsonDeserializationContext context) {
        try {
            return Serializers.deserializeItemstack(jsonElement);
        } catch (Exception e) { // Legacy data, load it as normal, when it's next saved it will be normal.
            RPGRegionsAPI.getAPI().getLogger().warning("Trying to migrate legacy ItemStack...");
            return ItemStack.deserialize(gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType()));
        }
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        return Serializers.serializeItemstack(itemStack);
    }
}