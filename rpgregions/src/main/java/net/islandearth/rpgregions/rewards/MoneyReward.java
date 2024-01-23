package net.islandearth.rpgregions.rewards;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.managers.data.account.RPGRegionsAccount;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyReward extends DiscoveryReward {

    @GuiEditable(value = "Money", icon = Material.GOLD_NUGGET)
    private final int amount;

    public MoneyReward(IRPGRegionsAPI api) {
        super(api);
        this.amount = 20;
    }
    public MoneyReward(IRPGRegionsAPI api, int amount) {
        super(api);
        this.amount = amount;
    }

    @Override
    public void award(Player player, RPGRegionsAccount account) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economy == null) {
            return;
        }

        economy.getProvider().depositPlayer(player, amount);
        this.updateAwardTime(account);
    }

    @Override
    public String getName() {
        return "Money";
    }
}
