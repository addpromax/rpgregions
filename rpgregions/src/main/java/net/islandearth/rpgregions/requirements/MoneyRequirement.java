package net.islandearth.rpgregions.requirements;

import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyRequirement extends RegionRequirement {

    @GuiEditable(value = "Money", icon = Material.GOLD_NUGGET)
    private final double money;

    public MoneyRequirement(IRPGRegionsAPI api) {
        this(api, 20);
    }

    public MoneyRequirement(IRPGRegionsAPI api, int money) {
        super(api);
        this.money = money;
    }
    
    @Override
    public boolean meetsRequirements(Player player) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
    
        RegisteredServiceProvider<Economy> economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economy == null) {
            return false;
        }
        
        return economy.getProvider().getBalance(player) >= money;
    }
    
    @Override
    public String getName() {
        return "Money";
    }
    
    @Override
    public String getText(Player player) {
        return "Money " + money;
    }
}
