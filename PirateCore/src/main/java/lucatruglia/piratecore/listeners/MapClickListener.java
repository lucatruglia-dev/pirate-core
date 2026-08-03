package lucatruglia.piratecore.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
        
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (handleChestInteract(player, event.getClickedBlock())) {
                return; 
            }
        }

        handleTreasureMapInteract(player, event);
    }

    private boolean handleChestInteract(Player player, Block clickedBlock) {
        if (clickedBlock.getType() != Material.CHEST) {
            return false;
        }

        TileState chestState = (TileState) clickedBlock.getState();
        String mapUuidStr = chestState.getPersistentDataContainer().get(TreasureMapManager.uuidMapKey, PersistentDataType.STRING);
        String playerUuidStr = chestState.getPersistentDataContainer().get(TreasureMapManager.playerUUIDMapKey, PersistentDataType.STRING);

        // Se mancano i tag, è una chest normale
        if (mapUuidStr == null || playerUuidStr == null) {
            return false;
        }

        UUID mapUUID = UUID.fromString(mapUuidStr);
        UUID playerUUID = UUID.fromString(playerUuidStr);

        // Chiama l'evento personalizzato del tesoro
        OpenTreasureChestEvent openEvent = new OpenTreasureChestEvent(player, mapUUID, playerUUID);
        Bukkit.getPluginManager().callEvent(openEvent);

        return true;
    }

    private void handleTreasureMapInteract(Player player, PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        boolean isTreasureMap = Boolean.TRUE.equals(
                itemMeta.getPersistentDataContainer()
                        .get(TreasureMapManager.treasureMapKey, PersistentDataType.BOOLEAN));

        if (isTreasureMap) {
            TreasureMapManager.getInstance().startTreasure(player, item);
            event.setCancelled(true);
        }
    }
}