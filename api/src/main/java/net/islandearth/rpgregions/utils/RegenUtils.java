package net.islandearth.rpgregions.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.integrations.IntegrationType;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.regenerate.Regenerate;
import net.islandearth.rpgregions.regenerate.entity.RegeneratingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @deprecated this entire thing needs replacing
 */
@Deprecated
public class RegenUtils {

    public static boolean regenerate(ConfiguredRegion region) {
        if (region == null || region.getWorld() == null) return false;
        Regenerate regenerate = region.getRegenerate();
        if (regenerate != null) {
            IRPGRegionsAPI plugin = RPGRegionsAPI.getAPI();
            IntegrationType integrationType = IntegrationType.valueOf(plugin.getConfig().getString("settings.integration.name").toUpperCase());
            if (integrationType == IntegrationType.WORLDGUARD) {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(region.getWorld()));
                if (regionManager == null) return false;
                ProtectedRegion protectedRegion = regionManager.getRegion(region.getId());
                Location min = new Location(region.getWorld(), protectedRegion.getMinimumPoint().getBlockX(), protectedRegion.getMinimumPoint().getBlockY(), protectedRegion.getMinimumPoint().getBlockZ());
                Location max = new Location(region.getWorld(), protectedRegion.getMaximumPoint().getBlockX(), protectedRegion.getMaximumPoint().getBlockY(), protectedRegion.getMaximumPoint().getBlockZ());
                if (!regenerate.isLoadChunks() && !region.getWorld().isChunkLoaded(min.getBlockX() >> 4, min.getBlockZ() >> 4)
                        && !region.getWorld().isChunkLoaded(max.getBlockX() >> 4, max.getBlockZ() >> 4)) {
                    return false;
                }

                if (regenerate.isOnlyEntities()) {
                    generateRandomEntities(protectedRegion, region);
                    return true;
                }

                World world = BukkitAdapter.adapt(region.getWorld());
                EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

                try {
                    if (regenerate.getSchematicName() != null && regenerate.getOrigin() != null) {
                        BlockVector3 position = BlockVector3.at(regenerate.getOrigin().getX(), regenerate.getOrigin().getY(), regenerate.getOrigin().getZ());
                        File schematic = new File("plugins/WorldEdit/schematics/" + regenerate.getSchematicName());
                        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
                        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().maxBlocks(-1).world(world).build();
                        Clipboard clipboard = format.getReader(new FileInputStream(schematic)).read();
                        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
                        Operation operation = clipboardHolder.createPaste(editSession)
                                .to(position)
                                .ignoreAirBlocks(false)
                                .build();
                        Operations.complete(operation);
                        editSession.flushSession();
                        generateRandomEntities(protectedRegion, region);
                        return true;
                    }
                } catch (IOException | WorldEditException e) {
                    e.printStackTrace();
                    return false;
                }

                boolean result = world.regenerate(new CuboidRegion(world, protectedRegion.getMinimumPoint(), protectedRegion.getMaximumPoint()), session);
                if (!result) {
                    plugin.getLogger().severe("Could not regenerate region " + region.getId() + "!");
                    return false;
                }
                session.flushSession();

                generateRandomEntities(protectedRegion, region);
                return true;
            } else {
                plugin.getLogger().warning("Regeneration only supports WorldGuard. Set regenerate to null in " + region.getId() + ".json to disable this message.");
                return false;
            }
        }
        return false;
    }

    // This is awful and needs changing
    private static void generateRandomEntities(ProtectedRegion region, ConfiguredRegion configuredRegion) {
        Regenerate regenerate = configuredRegion.getRegenerate();
        if (regenerate == null) return;
        if (regenerate.getRegeneratingEntities().isEmpty() || configuredRegion.getWorld() == null) return;
        Random random = ThreadLocalRandom.current();
        Location min = new Location(configuredRegion.getWorld(), region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ());
        Location max = new Location(configuredRegion.getWorld(), region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    for (RegeneratingEntity regeneratingEntity : regenerate.getRegeneratingEntities()) {
                        Block block = configuredRegion.getWorld().getBlockAt(x, y, z);
                        Block blockUnder = block.getLocation().subtract(0, 1, 0).getBlock();
                        Block blockAbove = block.getLocation().add(0, 1, 0).getBlock();
                        if (regeneratingEntity.getValidSpawnSurfaces().contains(blockUnder.getType())
                                && blockAbove.getType() == Material.AIR) {
                            int entityCount = 0;
                            for (Entity entity : block.getChunk().getEntities()) {
                                if (regeneratingEntity.isMythicEntity()
                                        && MythicBukkit.inst().getMobManager().isActiveMob(io.lumine.mythic.bukkit.BukkitAdapter.adapt(entity))) entityCount++;
                                else if (!regeneratingEntity.isMythicEntity()
                                        && entity.getType() == EntityType.valueOf(regeneratingEntity.getEntity())) entityCount++;
                            }

                            if (entityCount < regeneratingEntity.getMaxPerChunk()) {
                                if (random.nextInt(regeneratingEntity.getRarity() - 1) == 1) {
                                    if (regeneratingEntity.isMythicEntity()) {
                                        MythicBukkit.inst().getMobManager().getMythicMob(regeneratingEntity.getEntity()).ifPresent(mm -> {
                                            mm.spawn(io.lumine.mythic.bukkit.BukkitAdapter.adapt(block.getLocation()), 1);
                                        });
                                    } else {
                                        configuredRegion.getWorld().spawnEntity(block.getLocation(), EntityType.valueOf(regeneratingEntity.getEntity()));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
