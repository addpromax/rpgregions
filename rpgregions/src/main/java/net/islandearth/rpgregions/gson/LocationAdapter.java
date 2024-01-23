package net.islandearth.rpgregions.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    private final Gson gson;

    public LocationAdapter() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> deserialised = gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType());
        if (!deserialised.containsKey("pitch")) deserialised.put("pitch", 0.0f);
        if (!deserialised.containsKey("yaw")) deserialised.put("yaw", 0.0f);
        return Location.deserialize(deserialised);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        Map<String, Object> serialised = location.serialize();
        if (Float.compare(location.getPitch(), 0.0f) == 0) serialised.remove("pitch");
        if (Float.compare(location.getYaw(), 0.0f) == 0) serialised.remove("yaw");
        return gson.toJsonTree(serialised);
    }
}
