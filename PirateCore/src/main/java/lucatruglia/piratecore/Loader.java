package lucatruglia.piratecore;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lucatruglia.piratecore.command.*;
import lucatruglia.piratecore.extensions.*;
import lucatruglia.piratecore.gui.GUIListener;
import lucatruglia.piratecore.listeners.*;
import lucatruglia.piratecore.managers.*;
import lucatruglia.piratecore.managers.barrel.AnimationManager;
import lucatruglia.piratecore.managers.barrel.AutoSpawnerManager;
import lucatruglia.piratecore.managers.barrel.BarrelManager;
import lucatruglia.piratecore.managers.boat.BoatManager;
import lucatruglia.piratecore.managers.boat.BoatTrailManager;
import lucatruglia.piratecore.managers.boat.InventoryManager;
import lucatruglia.piratecore.managers.boat.PlayerInteractWithBoat;
import lucatruglia.piratecore.managers.economy.DatabaseManager;
import lucatruglia.piratecore.managers.economy.EconomyManager;
import lucatruglia.piratecore.managers.economy.LevelManager;
import lucatruglia.piratecore.managers.economy.RewardManager;
import lucatruglia.piratecore.managers.player.BossBarManager;
import lucatruglia.piratecore.managers.player.PlayerManager;
import lucatruglia.piratecore.managers.treasure.LootManager;
import lucatruglia.piratecore.managers.treasure.TreasureMapManager;
import lucatruglia.piratecore.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class Loader {

    public static void loadManagers(JavaPlugin plugin) {
        try { BoatTrailManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "BoatTrailManager", e); }
        try { TimerManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "TimerManager", e); }
        try { AutoSpawnerManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "AutoSpawnerManager", e); }
        try { InventoryManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "InventoryManager", e); }
        try { BoatManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "BoatManager", e); }
        try { LootManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "LootManager", e); }
        try { TreasureMapManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "TreasureMapManager", e); }
        try { BossBarManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "BossBarManager", e); }
        try { ConfigManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "ConfigManager", e); }
        try { DatabaseManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "DatabaseManager", e); }
        try { EconomyManager.getInstance().initialize(plugin); } catch (Exception e) { logAndThrow(plugin, "EconomyManager", e); }
        try { PlayerManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "PlayerManager", e); }
        try { LevelManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "LevelManager", e); }
        try { RewardManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "RewardManager", e); }
        try { AnimationManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "AnimationManager", e); }
        try { BarrelManager.getInstance().initialize(); } catch (Exception e) { logAndThrow(plugin, "BarrelManager", e); }
    }

    private static void logAndThrow(JavaPlugin plugin, String managerName, Exception e) {
        plugin.getLogger().severe("FAILED to initialize " + managerName + ": " + e.toString());
        throw new RuntimeException("Failed to initialize " + managerName, e);
    }

    public static void loadListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BossBarListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ArmorStandListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LevelUpListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MapClickListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OpenTreasureChestListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerInteractWithBoat(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnBarrelDestroyListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnBarrelHitListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerStartTreasureListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerEndTreasureListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerFoundTreasureListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BoatRideListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerJoinOnHisChestBoatListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerLeftOnHisChestBoatListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public static void loadCommands(JavaPlugin plugin) {
        registerCommand(plugin, "klevel", new LevelCommand());
        registerCommand(plugin, "kbar", new BossBarCommand());
        registerCommand(plugin, "kbarrel", new BarrelCommand());
        registerCommand(plugin, "kmap", new MapCommand());
        registerCommand(plugin, "kboat", new BoatCommand());
        registerCommand(plugin, "ktrail", new TrailCommand());
    }

    public static void loadTasks(JavaPlugin plugin){
        new BukkitRunnable() {
            @Override
            public void run() {
                TreasureMapManager.getInstance().checkAllActivity();
            }
        }.runTaskTimer(plugin, 20L, Utils.secondsToTicks(60));
    }

    public static void loadExtensions(JavaPlugin plugin) {
        PirateCoreExpansion.enable(plugin);
    }

    
    private static void registerCommand(JavaPlugin plugin, String name, CommandExecutor executor) {
        Command cmd = plugin.getCommand(name);
        if (cmd == null) {
            plugin.getLogger().severe("Command '" + name + "' not found in plugin.yml!");
            return;
        }
        
        if (cmd instanceof PluginCommand pluginCmd) {
            Plugin owner = pluginCmd.getPlugin();
            plugin.getLogger().info("Registering '" + name + "' — owner=" + owner.getName() 
                + " enabled=" + owner.isEnabled() 
                + " ownerHash=" + System.identityHashCode(owner)
                + " pluginHash=" + System.identityHashCode(plugin));
            
            pluginCmd.setExecutor(executor);
        } else {
            plugin.getLogger().warning("Command '" + name + "' is not a PluginCommand, cannot set executor!");
        }
    }



    
}