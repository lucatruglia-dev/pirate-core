package lucatruglia.piratecore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.managers.TreasureMapManager;

public class MapClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            ItemMeta item_meta = item.getItemMeta();
            boolean isTreasureMap = item_meta
                .getPersistentDataContainer()
                .get(TreasureMapManager.treasureMapKey, PersistentDataType.BOOLEAN);
            if(isTreasureMap){
                TreasureMapManager.getInstance().startTreasure(player, item);
                event.setCancelled(true);
            }
            return;
        }
    }

}
