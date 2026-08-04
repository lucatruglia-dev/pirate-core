package lucatruglia.piratecore.managers.treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.models.Reward;
import lucatruglia.piratecore.utils.Utils;

public class TreasureMapItem {

    Rarity rarity;
    ItemStack item;
    Reward reward;
    int minimum_level;

    public TreasureMapItem(Rarity rarity) {
        this.rarity = rarity;
        this.reward = TreasureMapManager.getInstance().getReward(rarity);
        this.minimum_level = TreasureMapManager.getInstance().getMinimumLevel(rarity);
        this.item = new ItemStack(Material.MAP);

        initPDC();
        setInfo();
    }

    private void initPDC() {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(TreasureMapManager.treasureMapKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(TreasureMapManager.rarityMapKey, PersistentDataType.STRING,
                rarity.name());
        meta.getPersistentDataContainer().set(TreasureMapManager.uuidMapKey, PersistentDataType.STRING,
                UUID.randomUUID().toString());
        item.setItemMeta(meta);
    }

    private void setInfo() {
        ItemMeta meta = item.getItemMeta();
        List<String> lores = new ArrayList<String>();
        
        meta.setDisplayName(Utils.colorize("&r&aMappa Del Tesoro [&6&l" + rarity.name() + "&r&a]"));
        lores.add(Utils.colorize("&bLivello richiesto: &l" + minimum_level));
        lores.add(" ");
        lores.add(Utils.colorize("&eRicompense"));
        lores.add(Utils.colorize("  &eXP: +" + reward.xp()));
        lores.add(Utils.colorize("  &eDobloni: +" + reward.money()));
        lores.add(" ");

        meta.setLore(lores);
        item.setItemMeta(meta);
    }

    public void give(Player player) {
        if(!player.isOnline()){
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(TreasureMapManager.playerUUIDMapKey, PersistentDataType.STRING,
                player.getUniqueId().toString());
        item.setItemMeta(meta);

        player.getInventory().addItem(item);
    }

}
