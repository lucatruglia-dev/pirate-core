package lucatruglia.piratecore.managers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.CustomMapRender;
import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.ListMessage.Row;
import lucatruglia.piratecore.utils.Logs;
import lucatruglia.piratecore.utils.Utils;

public class TreasureMapManager {

    private static TreasureMapManager instance;
    public static NamespacedKey filledMapKey;

    private FileConfiguration config;

    public static NamespacedKey treasureMapKey;
    public static NamespacedKey rarityMapKey;
    public static NamespacedKey uuidMapKey;
    public static NamespacedKey playerUUIDMapKey;

    public final Integer[] middleMap_x_z = { 1301, 907 };
    public final int despawnChestTimeInMinutes = 60;
    public final int endMissionTimeInSeconds = 5;
    public final UUID worldUUID = UUID.fromString("3f5f0ed0-5454-467a-800e-505e6d88aabf");

    private String filePath = "data/treasurestarted.yml";

    private static RegionManager regions;

    public static enum Rarity {
        COMMON, RARE, EPIC, LEGENDARY
    }

    public static TreasureMapManager getInstance() {
        if (instance == null) {
            instance = new TreasureMapManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        initializeRegions();
        config = ConfigManager.getInstance().getConfig(this.filePath);
        filledMapKey = new NamespacedKey(plugin, "isFilledMap");
        treasureMapKey = new NamespacedKey(plugin, "isTreasureMap");
        rarityMapKey = new NamespacedKey(plugin, "rarity");
        uuidMapKey = new NamespacedKey(plugin, "uuid");
        playerUUIDMapKey = new NamespacedKey(plugin, "playeruuid");

        instance = this;
    }

    private void initTreasureOnDb(
            UUID player_uuid,
            int[] chest_position,
            Rarity mission_rarity,
            UUID map_uuid) {
        String index = player_uuid.toString() + ".";

        ConfigManager.getInstance().set(filePath, index + "chest_position", chest_position);
        ConfigManager.getInstance().set(filePath, index + "mission_rarity", mission_rarity.name());
        ConfigManager.getInstance().set(filePath, index + "mission_UUID", map_uuid.toString());
        ConfigManager.getInstance().set(filePath, index + "start_mission_date", Instant.now().toString());
        ConfigManager.getInstance().set(filePath, index + "despawn_chest_date",
                Instant.now().plus(Duration.ofMinutes(despawnChestTimeInMinutes)).toString());

        ConfigManager.getInstance().set(filePath, index + "is_player_searching", true);

        config = ConfigManager.getInstance().getConfig("data/treasurestarted.yml");
    }

    private void generateChestWithLoot(Location locationToPlaceBlock, Rarity rarity, UUID map_uuid, Player player) {
        locationToPlaceBlock.getBlock().setType(Material.CHEST);

        Block chestBlock = locationToPlaceBlock.getBlock();
        TileState chestState = (TileState) chestBlock.getState();

        chestState.getPersistentDataContainer().set(uuidMapKey, PersistentDataType.STRING, map_uuid.toString());
        chestState.getPersistentDataContainer().set(playerUUIDMapKey, PersistentDataType.STRING,
                player.getUniqueId().toString());
        chestState.update();
    }

    private void initializeRegions() {
        World world = PirateCore.get().getServer().getWorld(worldUUID);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        regions = container.get(BukkitAdapter.adapt(world));
    }

    private void removeMapOnInventory(Player player, UUID mapUUID) {
        ItemStack[] items = player.getInventory().getContents();
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            ItemMeta itemMeta = item.getItemMeta();
            String res = itemMeta.getPersistentDataContainer().get(uuidMapKey, PersistentDataType.STRING);
            if (res == null) {
                continue;
            }

            if (mapUUID.toString().equals(res)) {
                player.getInventory().remove(item);
                Logs.sendWarningMessageToPlayer(player, "kMap", "Mappa rimossa dall'inventario.");
            }
        }
    }

    private void removePlayerOnDB(UUID playerUUID) {
        ConfigManager.getInstance().set(filePath, playerUUID.toString(), null);
    }

    public boolean hasPlayerFoundTreasure(Player player) {
        Boolean res = ConfigManager.getInstance().getBoolean(filePath,
                player.getUniqueId().toString() + ".is_player_searching");

        return !res;
    }

    public void playerFoundTreasure(Player player) {
        ConfigManager.getInstance().set(filePath, player.getUniqueId().toString() + ".is_player_searching", false);

        Rarity rarity = Rarity.valueOf((String) ConfigManager.getInstance().get(filePath,
                player.getUniqueId().toString() + ".mission_rarity"));
        UUID missionUUID = UUID.fromString(
                ConfigManager.getInstance().getString(filePath, player.getUniqueId().toString() + ".mission_UUID"));

        // PREMIARE PLAYER
        PlayerManager.getInstance().addXP(player, 100, false);

        Logs.sendListMessageToPlayer(
                player,
                new ListMessage(
                        "Complimenti! hai trovato il tesoro (" + rarity.name() + ")",
                        List.of(
                                new ListMessage.Row("Attenzione",
                                        "Hai " + Math.round(endMissionTimeInSeconds / 60)
                                                + " minuti per prendere il bottino!"),
                                new ListMessage.Row("XP", "+100 XP"),
                                new ListMessage.Row("Dobloni", "+100 $")),
                        List.of(
                                new ListMessage.Button("Contrassegna come completata", ""))));

        Logs.sendWarningActionBarToPlayer(player, "Attenzione",
                "Hai " + Math.round(endMissionTimeInSeconds / 60) + " minuti per prendere il bottino!");
        new BukkitRunnable() {
            @Override
            public void run() {
                TreasureMapManager.getInstance().removeMapOnInventory(player, missionUUID);
                TreasureMapManager.getInstance().removeChest(player.getUniqueId());
                TreasureMapManager.getInstance().removePlayerOnDB(player.getUniqueId());
            }
        }.runTaskLater(PirateCore.get(), Utils.secondsToTicks(endMissionTimeInSeconds));
    }

    private void removeChest(UUID playerUUID) {
        Object rawPosition = ConfigManager.getInstance().get(filePath, playerUUID.toString() + ".chest_position");
        List<Integer> chestPosition = new ArrayList<>();

        if (rawPosition instanceof List) {
            chestPosition = (List<Integer>) rawPosition;
        } else if (rawPosition instanceof int[]) {
            for (int val : (int[]) rawPosition) {
                chestPosition.add(val);
            }
        }

        if (chestPosition.isEmpty() || chestPosition.size() < 3)
            return;

        World world = PirateCore.get().getServer().getWorld(worldUUID);
        Location loc = new Location(world, chestPosition.get(0), chestPosition.get(1), chestPosition.get(2));
        loc.getBlock().setType(Material.AIR);

        Player p = PirateCore.get().getServer().getPlayer(playerUUID);

        if (p != null && p.isOnline()) {
            Logs.sendWarningMessageToPlayer(p, "kMap", "Chest rimossa.");
        }
    }
    // COMUNE, RARO, EPICO, LEGGENDARIO

    public void giveMap(Player player, Rarity rarity) {
        ItemStack defaultMap = new ItemStack(Material.MAP);
        ItemMeta defaultMapMeta = defaultMap.getItemMeta();

        defaultMapMeta.setDisplayName("" + rarity.name() + " Map");
        defaultMapMeta.getPersistentDataContainer().set(treasureMapKey, PersistentDataType.BOOLEAN, true);
        defaultMapMeta.getPersistentDataContainer().set(rarityMapKey, PersistentDataType.STRING, rarity.name());
        defaultMapMeta.getPersistentDataContainer().set(uuidMapKey, PersistentDataType.STRING,
                UUID.randomUUID().toString());
        defaultMapMeta.getPersistentDataContainer().set(playerUUIDMapKey, PersistentDataType.STRING,
                player.getUniqueId().toString());

        defaultMap.setItemMeta(defaultMapMeta);

        player.getInventory().addItem(defaultMap);
    }

    public int[] getRandomLocation() {

        Map<String, ProtectedRegion> regions_list = regions.getRegions();
        int radius = 30;

        int random_z = ThreadLocalRandom.current().nextInt(middleMap_x_z[1] - radius, middleMap_x_z[1] + radius);

        Integer random_x = null;

        while (random_x == null) {
            int temp_x = ThreadLocalRandom.current()
                    .nextInt(middleMap_x_z[0] - radius, middleMap_x_z[0] + radius);

            boolean valid = regions_list.values().stream()
                    .noneMatch(region -> region.contains(BlockVector2.at(temp_x, random_z)));

            if (valid) {
                random_x = temp_x;
            }
        }

        return new int[] { random_x, random_z };
    }

    public Location getSeabedBlock(int x, int z) {
        World world = PirateCore.get().getServer().getWorld(worldUUID);
        int y = world.getHighestBlockYAt(x, z);

        while (y > world.getMinHeight()) {
            Block block = world.getBlockAt(x, y, z);

            if (block.getType() == Material.WATER) {
                Block below = block.getRelative(BlockFace.DOWN);

                if (below.getType() != Material.WATER) {
                    return new Location(world, below.getX(), below.getY() + 1, below.getZ());
                }
            }

            y--;
        }

        return null;
    }

    private boolean playerHasTreasureActive(Player player) {
        ConfigManager.getInstance().reloadConfig(filePath);
        String mission_uuid = (String) ConfigManager.getInstance().get(filePath,
                player.getUniqueId().toString() + ".mission_UUID");
        return mission_uuid != null;
    }

    public void checkAllActivity() {
        ConfigManager.getInstance().reloadConfig(filePath);
        this.config = ConfigManager.getInstance().getConfig("data/treasurestarted.yml");

        if (config.getKeys(false) == null)
            return;

        Instant now = Instant.now(); // Orario attuale in UTC

        for (String playerUuidStr : config.getKeys(false)) {
            String despawnDateStr = config.getString(playerUuidStr + ".despawn_chest_date");
            UUID mapUUID = UUID.fromString(config.getString(playerUuidStr + ".mission_UUID"));
            Boolean isPlayerSearching = config.getBoolean(playerUuidStr + ".is_player_searching");
            Player player = PirateCore.get().getServer().getPlayer(UUID.fromString(playerUuidStr));

            if (despawnDateStr == null || isPlayerSearching == false)
                continue;

            // Converte la stringa ISO (es. '2026-07-31T17:49:19.533Z') in un oggetto
            // Instant
            Instant despawnTime = Instant.parse(despawnDateStr);

            // Calcola la differenza temporale
            Duration duration = Duration.between(now, despawnTime);
            long secondsLeft = duration.getSeconds();

            // 1. Controllo se è scaduta
            if (secondsLeft <= 0) {
                Logs.sendSuccessActionBarToPlayer(player, "kMap", "Missione scaduta, la chest è despawnata.");
                removeMapOnInventory(player, mapUUID);
                removeChest(UUID.fromString(playerUuidStr));
                removePlayerOnDB(UUID.fromString(playerUuidStr));

            } else if (secondsLeft <= 100) {
                Logs.sendWarningActionBarToPlayer(player, "kMap", "Attenzione, la missione scade tra pochi secondi.");
                Logs.sendWarningMessageToPlayer(player, "kMap", "Attenzione, la missione scade tra pochi secondi.");
            }

            else {
                int minutesLeft = (int) Math.round(secondsLeft / 60);
                Logs.sendWarningActionBarToPlayer(player, "kMap",
                        "Attenzione, la missione scade tra " + minutesLeft + " minuti.");
            }
        }
        return;

    }

    private void passPersistentData(ItemStack fromItem, ItemStack toItem) {
        ItemMeta item1meta = fromItem.getItemMeta();
        ItemMeta item2meta = toItem.getItemMeta();

        String rarity = item1meta.getPersistentDataContainer().get(rarityMapKey, PersistentDataType.STRING);
        String uuid = item1meta.getPersistentDataContainer().get(uuidMapKey, PersistentDataType.STRING);
        String player_uuid = item1meta.getPersistentDataContainer().get(playerUUIDMapKey, PersistentDataType.STRING);

        item2meta.getPersistentDataContainer().set(rarityMapKey, PersistentDataType.STRING, rarity);
        item2meta.getPersistentDataContainer().set(uuidMapKey, PersistentDataType.STRING, uuid);
        item2meta.getPersistentDataContainer().set(playerUUIDMapKey, PersistentDataType.STRING, player_uuid);

        toItem.setItemMeta(item2meta);
    }

    private ItemStack generateFilledMap(Player player, int[] coord) {
        MapView map = Bukkit.createMap(PirateCore.get().getServer().getWorld(worldUUID));
        int x = coord[0];
        int z = coord[1];

        map.setCenterX(x);
        map.setCenterZ(z);

        map.setScale(MapView.Scale.FAR);

        map.setTrackingPosition(false);

        map.getRenderers().clear();

        map.addRenderer(new CustomMapRender(Utils.coordToString(x, z)));

        ItemStack map_item = new ItemStack(Material.FILLED_MAP);
        MapMeta map_meta = (MapMeta) map_item.getItemMeta();

        if (map_meta != null) {
            map_meta.getPersistentDataContainer().set(filledMapKey, PersistentDataType.BOOLEAN, true);

            map_meta.setMapView(map);

            map_meta.setDisplayName("§aMappa del tesoro §e " + Utils.coordToString(x, z));
            map_item.setItemMeta(map_meta);
        }

        return map_item;
    }

    public boolean startTreasure(Player player, ItemStack map_item) {

        if (playerHasTreasureActive(player)) {
            Logs.sendWarningMessageToPlayer(player, "kMap", "Hai già una mappa del tesoro attiva.");
            return false;
        }

        ItemMeta map_meta = map_item.getItemMeta();
        Rarity rarity = Rarity
                .valueOf(map_meta.getPersistentDataContainer().get(rarityMapKey, PersistentDataType.STRING));
        UUID map_uuid = UUID
                .fromString(map_meta.getPersistentDataContainer().get(uuidMapKey, PersistentDataType.STRING));

        int[] location = getRandomLocation();
        String stringLocation = Utils.coordToString(location[0], location[1]);

        ItemStack filledMap = generateFilledMap(player, location);
        passPersistentData(map_item, filledMap);
        player.getInventory().setItemInMainHand(filledMap);

        Location locationToPlaceBlock = getSeabedBlock(location[0], location[1]);
        generateChestWithLoot(locationToPlaceBlock, rarity, map_uuid, player);

        initTreasureOnDb(
                player.getUniqueId(),
                new int[] { locationToPlaceBlock.getBlockX(), locationToPlaceBlock.getBlockY(),
                        locationToPlaceBlock.getBlockZ() },
                rarity, map_uuid);

        player.teleport(locationToPlaceBlock.clone().add(0, 10, 0));
        player.playSound(player, Sound.BLOCK_BELL_USE, 1.0f, 0.2f);

        Logs.sendListMessageToPlayer(
                player,
                new ListMessage("Caccia al tesoro iniziata",
                        new ArrayList<Row>(List.of(
                                new ListMessage.Row("Rarità", rarity.name()),
                                new ListMessage.Row("Coordinate", stringLocation),
                                new ListMessage.Row("Tempo rimanente", "" + despawnChestTimeInMinutes + " minuti"))),
                        new ArrayList<ListMessage.Button>(List.of(
                                new ListMessage.Button("ANNULLA", "")))));

        return true;
    }

}
