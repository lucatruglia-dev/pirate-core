package lucatruglia.piratecore.managers;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
    private static EconomyManager instance;
    private static Economy econ = null;
    
    private JavaPlugin plugin;
    
    public static EconomyManager getInstance() {
        if (instance == null) {
            instance = new EconomyManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        this.plugin = plugin;
        if(!setupEconomy()){
            plugin.getLogger().severe("Vault o un plugin di economia compatibile non sono stati trovati!");
        }
        instance = this;
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
