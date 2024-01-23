package net.islandearth.rpgregions.rewards;

import io.papermc.lib.PaperLib;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeleportReward extends DiscoveryReward {

    @GuiEditable(value = "Teleport location", icon = Material.ENDER_PEARL)
    private Location location;

    public TeleportReward(IRPGRegionsAPI api) {
        super(api);
        this.location = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String getName() {
        return "Teleport";
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        PaperLib.teleportAsync(player, location);
        this.updateAwardTime(account);
    }
}
