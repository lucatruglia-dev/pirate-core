package lucatruglia.piratecore.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import emanondev.itemedit.ItemEdit;
import lucatruglia.piratecore.managers.TreasureMapManager.Rarity;
import lucatruglia.piratecore.utils.Logs;

public class LootManager {
    private static LootManager instance;

    private FileConfiguration config;

    public static LootManager getInstance() {
        if (instance == null) {
            instance = new LootManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin) {
        config = ConfigManager.getInstance().getConfig("settings/treasure.yml");
        instance = this;
    }

    public ArrayList<ItemStack> getTreasureLoot(Rarity rarity) {
    ArrayList<ItemStack> items = new ArrayList<>();
    String path = rarity.name();
    List<Map<?, ?>> rawItems = config.getMapList(path);
    
    if (rawItems == null) return items;

    for (Map<?, ?> entry : rawItems) {
        String itemName = (String) entry.get("item");
        int amount = ((Number) entry.get("amount")).intValue();
        int chance = ((Number) entry.get("chance")).intValue();

        Logs.sendLog("LootDebug", "Controllo item: " + itemName + " con chance: " + chance);

        // TEST TEMPORANEO: Togliamo la chance per vedere se almeno così compaiono gli item nella chest
        ItemStack template = ItemEdit.get().getServerStorage().getItem(itemName);
        if (template != null) {
            ItemStack tempItem = template.clone();
            tempItem.setAmount(amount);
            items.add(tempItem);
        } else {
            Logs.sendLog("LootDebug", "ERRORE: ServerStorage non ha trovato l'item -> " + itemName);
        }
    }


    for (ItemStack iterable_element : items) {
        Logs.sendLog("asd", iterable_element.getItemMeta().getItemName());

        PlayerManager.getInstance().dropItem(Bukkit.getPlayer("Kcalu_"), iterable_element);
    }
    return items;
}

}
