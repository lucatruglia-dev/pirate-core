package lucatruglia.piratecore.managers.barrel;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.ConfigManager;
import lucatruglia.piratecore.utils.Logs;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AutoSpawnerManager {
    private static AutoSpawnerManager instance;
    private static Map<String, ProtectedRegion> regions_list;
    private final String filePath = "data/barrel_position.yml";
    private final int barrelAmountToSpawn = 2000;
    public final UUID worldUUID = UUID.fromString("b23dea45-474e-467d-bca1-e25f4c973dd3");

    public final FileConfiguration config = ConfigManager.getInstance().getConfig(filePath);

    private LazyBarrelSpawner lazySpawner;

    public static AutoSpawnerManager getInstance() {
        if (instance == null)
            instance = new AutoSpawnerManager();
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        ConfigManager.getInstance().getConfig(filePath);
        initializeRegions();

        World world = Bukkit.getWorld(worldUUID);
        if (world != null) {
            lazySpawner = new LazyBarrelSpawner(world, filePath);
            Bukkit.getPluginManager().registerEvents(lazySpawner, plugin);
        } else {
            Logs.sendLog("AutoSpawner", "Mondo non trovato, LazySpawner non avviato!");
        }

        instance = this;
    }

    private void initializeRegions() {
        // ... invariato ...
        if (this.worldUUID == null) {
            Logs.sendLog("debug", "worldUUID non è configurato correttamente");
            return;
        }
        World world = PirateCore.get().getServer().getWorld(this.worldUUID);
        if (world == null) {
            Logs.sendLog("initializeRegions - AutoSpawner", "error with world with uuid: " + this.worldUUID);
            return;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        regions_list = container.get(BukkitAdapter.adapt(world)).getRegions();
    }

    public int[] getRandomLocation() {
        // ... invariato ...
        int random_z = ThreadLocalRandom.current().nextInt(-1900, 1900);
        Integer random_x = null;
        while (random_x == null) {
            int temp_x = ThreadLocalRandom.current().nextInt(-1000, 2760);
            boolean valid = regions_list.values().stream()
                    .noneMatch(region -> region.contains(BlockVector2.at(temp_x, random_z)));
            if (valid)
                random_x = temp_x;
        }
        return new int[] { random_x, random_z };
    }

    public void barrelList(Player player, int amount) {
        // ... invariato ...
        for (int i = 0; i < amount; i++) {
            String asUUID = ConfigManager.getInstance().getString(filePath, i + ".armorStandUUID");
            if (asUUID == null)
                continue;

            Integer x = ConfigManager.getInstance().getInt(filePath, i + ".X");
            Integer z = ConfigManager.getInstance().getInt(filePath, i + ".Z");

            TextComponent button = new TextComponent("[" + x + ", " + z + "] " + asUUID);
            button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/tppos " + player.getName() + " " + x + " 65 " + z));
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§7Click to run!").create()));
            player.spigot().sendMessage(button);
        }
        player.sendMessage("done");
    }

    // ═══════════ NUOVO ═══════════
    public void barrel() {
        if (lazySpawner != null) {
            lazySpawner.loadFromConfig(barrelAmountToSpawn, "barile1");
            Bukkit.broadcastMessage("§aLazy autospawn: " + barrelAmountToSpawn
                    + " barrels");
        }
    }


    public void cleanUp(){
        lazySpawner.despawnAllActiveBarrels();
    }
    

    public void generateCoord() {
        for (int i = 0; i < barrelAmountToSpawn; i++) {
            int[] loc = getRandomLocation();
            // NUOVO: salva già nel formato nidificato
            ConfigManager.getInstance().set(filePath, i + ".X", loc[0]);
            ConfigManager.getInstance().set(filePath, i + ".Z", loc[1]);
            ConfigManager.getInstance().set(filePath, i + ".armorStandUUID", null);
        }
    }
}
