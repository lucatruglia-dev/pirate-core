package lucatruglia.piratecore.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.CustomMapRender;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.ListMessage.Row;
import lucatruglia.piratecore.utils.Logs;


public class TreasureMapManager {
    
    private static TreasureMapManager instance;
    public static NamespacedKey filledMapKey;
    public static NamespacedKey treasureMapKey;
    public static NamespacedKey rarityMapKey;
    public static NamespacedKey uuidMapKey;
    public static NamespacedKey playerUUIDMapKey;

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
        filledMapKey = new NamespacedKey(plugin, "isFilledMap");
        treasureMapKey = new NamespacedKey(plugin, "isTreasureMap");
        rarityMapKey = new NamespacedKey(plugin, "rarity");
        uuidMapKey = new NamespacedKey(plugin, "uuid");
        playerUUIDMapKey = new NamespacedKey(plugin, "playeruuid");
        instance = this;        

    }

    // COMUNE, RARO, EPICO, LEGGENDARIO 

    public void giveMap(Player player, Rarity rarity){
        ItemStack defaultMap = new ItemStack(Material.MAP);
        ItemMeta defaultMapMeta = defaultMap.getItemMeta();

        defaultMapMeta.setDisplayName(""+rarity.name() + " Map");
        defaultMapMeta.getPersistentDataContainer().set(treasureMapKey, PersistentDataType.BOOLEAN, true);
        defaultMapMeta.getPersistentDataContainer().set(rarityMapKey, PersistentDataType.STRING, rarity.name());
        defaultMapMeta.getPersistentDataContainer().set(uuidMapKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        defaultMapMeta.getPersistentDataContainer().set(playerUUIDMapKey, PersistentDataType.STRING, player.getUniqueId().toString());
        
        defaultMap.setItemMeta(defaultMapMeta);

        player.getInventory().addItem(defaultMap);
    }

    private List<Integer> getRandomLocation(){
        return new ArrayList<Integer>(List.of(1,2,3));
    }

    private void passPersistentData(ItemStack fromItem, ItemStack toItem){
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

    private ItemStack generateFilledMap(Player player){
        MapView map = Bukkit.createMap(player.getWorld());
        int x = 858;
        int z = 1301;
        // da controllare che il player sia effettivamente nel mondo spawn

        // 871 1434 spawn coord
        // 858 43 1301 tesoro

        map.setCenterX(x);
        map.setCenterZ(z);

        map.setScale(MapView.Scale.FAR);

        map.setTrackingPosition(false);

        map.getRenderers().clear();

        map.addRenderer(new CustomMapRender("("  +x+   ","    +z+    ")"));

        ItemStack map_item = new ItemStack(Material.FILLED_MAP);
        MapMeta map_meta = (MapMeta) map_item.getItemMeta();

        if (map_meta != null) {
            map_meta.getPersistentDataContainer().set(filledMapKey, PersistentDataType.BOOLEAN, true);

            map_meta.setMapView(map);
            // Opzionale: dai un nome personalizzato alla mappa
            map_meta.setDisplayName("§aMappa di test");
            map_item.setItemMeta(map_meta);
        }

        return map_item;
    }

    public void startTreasure(Player player, ItemStack map_item){
        ItemMeta map_meta = map_item.getItemMeta();
        Rarity rarity = Rarity.valueOf(map_meta.getPersistentDataContainer().get(rarityMapKey, PersistentDataType.STRING));
        
        List<Integer> location = getRandomLocation();
        String stringLocation = location.toString().replace("[", "(").replace("]", ")");

        ItemStack filledMap = generateFilledMap(player);
        passPersistentData(map_item, filledMap);
        
        player.getInventory().setItemInMainHand(filledMap);

        Logs.sendListMessageToPlayer(
            player, 
            new ListMessage("Caccia al tesoro iniziata", 
            new ArrayList<Row>(List.of(
                new ListMessage.Row("Rarità", rarity.name()),
                new ListMessage.Row("Coordinate", stringLocation),
                new ListMessage.Row("Tempo rimanente", "1h 30m")
            )),
            new ArrayList<ListMessage.Button>(List.of(
                new ListMessage.Button("ANNULLA", "")
            ))
        ));
    }
}
