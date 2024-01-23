package net.islandearth.rpgregions.effects.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.effects.FogEffect;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.managers.RPGRegionsManagers;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.entity.Player;

public class ProtocolCreator {

    public ProtocolCreator(final RPGRegions plugin, final RPGRegionsManagers managers) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                for (ConfiguredRegion region : managers.getRegionsCache().getConfiguredRegions().values()) {
                    if (region.getEffects() == null) continue;
                    for (RegionEffect effect : region.getEffects()) {
                        if (effect instanceof FogEffect fogEffect) {
                            event.setPacket(fogEffect.onSendChunk(packet, player));
                        }
                    }
                }
            }
        });
    }
}
