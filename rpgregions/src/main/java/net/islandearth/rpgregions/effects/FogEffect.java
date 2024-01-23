package net.islandearth.rpgregions.effects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FogEffect extends RegionEffect {

    @GuiEditable("Sky Colour") private final String skyColour;
    @GuiEditable("Water Colour") private final String waterColour;
    @GuiEditable("Water Fog Colour") private final String waterFogColour;
    @GuiEditable("Fog Colour") private final String fogColour;
    @GuiEditable("Grass Colour") private final String grassColour;
    @GuiEditable("Foilage Colour") private final String foilageColour;

    //private transient BiomeBase biomeBase;
    private transient int biomeId;
    private transient List<UUID> effect;

    public FogEffect(IRPGRegionsAPI api) {
        super(api);
        this.skyColour = "FF0000";
        this.waterColour = "FF0000";
        this.waterFogColour = "FF0000";
        this.fogColour = "FF0000";
        this.grassColour = "FF0000";
        this.foilageColour = "FF0000";
        generateBiomes();
    }

    public void generateBiomes() {
        /*ResourceKey<BiomeBase> key = ResourceKey.a(IRegistry.aO, new MinecraftKey(UUID.randomUUID().toString()));
        BiomeBase biomeBase = new BiomeBaseWrapper_1_17R1()
                .build(fogColour,
                        waterColour,
                        waterFogColour,
                        skyColour,
                        grassColour.equals("-1") ? null : grassColour,
                        foilageColour.equals("-1") ? null : foilageColour);

        DedicatedServer ds = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
        IRegistryWritable<BiomeBase> rw = ds.getCustomRegistry().b(IRegistry.aO);
        rw.a(key, biomeBase, Lifecycle.stable());
        this.biomeId = ds.getCustomRegistry().d(IRegistry.aO).getId(biomeBase);
        this.biomeBase = biomeBase;*/
    }

    @Override
    public void effect(Player player) {
        if (this.effect == null) this.effect = new ArrayList<>();

        if (!effect.contains(player.getUniqueId())) {
            effect.add(player.getUniqueId());

            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            for (Chunk chunk : getChunkAround(player.getLocation().getChunk(), Bukkit.getServer().getViewDistance())) {
                final PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MAP_CHUNK);

                //try {
                //    manager.sendServerPacket(player, packetContainer);
                //} catch (InvocationTargetException e) {
                //    e.printStackTrace();
                //}
            }
        }
    }

    @Override
    public void uneffect(Player player) {
        if (this.effect == null) this.effect = new ArrayList<>();

        if (effect.contains(player.getUniqueId())) {
            effect.remove(player.getUniqueId());
            for (Chunk chunk : getChunkAround(player.getLocation().getChunk(), Bukkit.getServer().getViewDistance())) {
                //net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
                //((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutMapChunk(c));
            }
        }
    }

    public PacketContainer onSendChunk(PacketContainer packet, Player target) {
        if (effect != null && effect.contains(target.getUniqueId())) {
            int[] biomeIDs = packet.getIntegerArrays().read(0);
            Arrays.fill(biomeIDs, biomeId);
            packet.getIntegerArrays().write(0, biomeIDs);
        }
        return packet;
    }

    @Override
    public String getName() {
        return "Fog";
    }

    private Collection<Chunk> getChunkAround(Chunk origin, int radius) {
        World world = origin.getWorld();

        int length = (radius * 2) + 1;
        Set<Chunk> chunks = new HashSet<>(length * length);

        int cX = origin.getX();
        int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (world.isChunkLoaded(cX + x, cZ + z)) chunks.add(world.getChunkAt(cX + x, cZ + z));
            }
        }
        return chunks;
    }
}
