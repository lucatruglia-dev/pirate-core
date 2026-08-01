package lucatruglia.piratecore.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.events.OpenTreasureChestEvent;
import lucatruglia.piratecore.managers.TreasureMapManager;

public class MapClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if(action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType()==Material.CHEST){
                TileState chestState = (TileState) event.getClickedBlock().getState();

                UUID mapUUID = UUID.fromString(chestState.getPersistentDataContainer().get(TreasureMapManager.uuidMapKey, PersistentDataType.STRING));
                UUID playerUUID = UUID.fromString(chestState.getPersistentDataContainer().get(TreasureMapManager.playerUUIDMapKey, PersistentDataType.STRING));

                if(mapUUID != null & playerUUID != null){
                    OpenTreasureChestEvent openTreasureChestEvent = new OpenTreasureChestEvent(player, mapUUID, playerUUID);
                    Bukkit.getPluginManager().callEvent(openTreasureChestEvent);
                }


                return;
            }

            if(item==null){
                return;
            }

            ItemMeta item_meta = item.getItemMeta();
            boolean isTreasureMap = Boolean.TRUE.equals(
                    item_meta.getPersistentDataContainer()
                            .get(TreasureMapManager.treasureMapKey, PersistentDataType.BOOLEAN));

            if (isTreasureMap) {
                TreasureMapManager.getInstance().startTreasure(player, item);
                event.setCancelled(true);
            }
            return;
        }
    }

}
