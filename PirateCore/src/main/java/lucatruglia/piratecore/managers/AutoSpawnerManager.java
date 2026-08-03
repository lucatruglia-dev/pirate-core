package lucatruglia.piratecore.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.utils.Logs;
public class AutoSpawnerManager {
    private static AutoSpawnerManager instance;
    private static Map<String, ProtectedRegion> regions_list;
    private final String filePath = "data/barrel_position.yml";
    private final int barrelAmountToSpawn = 5;

    public final UUID worldUUID = UUID.fromString("b23dea45-474e-467d-bca1-e25f4c973dd3");

    public static AutoSpawnerManager getInstance() {
        if (instance == null) {
            instance = new AutoSpawnerManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        ConfigManager.getInstance().getConfig(filePath);
        initializeRegions();

        instance = this;
    }

    private void initializeRegions() {
        if (this.worldUUID == null) {
            Logs.sendLog("deubg", "worldUUID non è configurato correttamente");
            return;
        }

        World world = PirateCore.get().getServer().getWorld(this.worldUUID);

        if (world == null) {
            Logs.sendLog("initalizeRegions - AutoSpawner", "error with world with uuid: " + this.worldUUID);
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        regions_list = container.get(BukkitAdapter.adapt(world)).getRegions();
    }

    public int[] getRandomLocation() {
        int random_z = ThreadLocalRandom.current().nextInt(-1900, 1900);

        Integer random_x = null;

        while (random_x == null) {
            int temp_x = ThreadLocalRandom.current().nextInt(-1000, 2760);

            boolean valid = regions_list.values().stream()
                    .noneMatch(region -> region.contains(BlockVector2.at(temp_x, random_z)));

            if (valid) {
                random_x = temp_x;
            }
        }

        return new int[] { random_x, random_z };
    }

    public void barrel() {
        /*
         * String filePath = "data/test.yml";
         * for (int i = 0; i < 10; i++) {
         * int[] loc = getRandomLocation();
         * Location location = new
         * Location(PirateCore.get().getServer().getWorld(AutoSpawnerManager.getInstance
         * ().worldUUID), loc[0], 64, loc[1]);
         * boolean res = BarrelManager.getInstance().spawnBarrel(location, "barile1");
         * 
         * Logs.sendSuccessMessageToPlayer(player, "COORD", "X: "+loc[0] +
         * ", Z: "+loc[1]);
         * }
         */

        for (int i = 0; i < barrelAmountToSpawn; i++) {
            int[] loc = getRandomLocation();
            Location location = new Location(Bukkit.getWorld(worldUUID), loc[0], 63, loc[1]);
            BarrelData res = BarrelManager.getInstance().spawnBarrel(location, "barile1");

            if (res == null) {
                Logs.sendLog("ERROR BARREL N." + i, "non spawnato");
                continue;
            }

            ConfigManager.getInstance().set(filePath, "" + i + ".armorStandUUID", res.armorStandUUID().toString());
            ConfigManager.getInstance().set(filePath, "" + i + ".blockDisplayUUID", res.blockDisplayUUID().toString());
            ConfigManager.getInstance().set(filePath, "" + i + ".textDisplayUUID", res.textDisplayUUID().toString());
            ConfigManager.getInstance().set(filePath, "" + i + ".X", loc[0]);
            ConfigManager.getInstance().set(filePath, "" + i + ".Z", loc[1]);

            Logs.sendLog("NWE BARREL", "");

            Logs.sendLog("BARREL N." + i,
                    "ArmorStand (" + res.armorStandUUID().toString() + ")");

            Logs.sendLog("BARREL N." + i,
                    "BlockDisplay (" + res.blockDisplayUUID().toString() + ")");

            Logs.sendLog("BARREL N." + i,
                    "TextDisplay (" + res.textDisplayUUID().toString() + ")");

            Logs.sendLog("BARREL N." + i,
                    "COORD (" + loc[0] + ", " + loc[1] + ")");
        }

    }
}
