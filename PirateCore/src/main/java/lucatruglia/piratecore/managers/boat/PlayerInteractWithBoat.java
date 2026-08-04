package lucatruglia.piratecore.managers.boat;

import java.util.UUID;

import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.utils.Logs;

public class PlayerInteractWithBoat implements Listener {
    @EventHandler
    public void onPlayerOpenInventoryBoat(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder() instanceof ChestBoat boat) {
            UUID boatUUID = boat.getUniqueId();

            if (boat.getPersistentDataContainer().get(BoatManager.getInstance().isPlayerBoatKey, PersistentDataType.BOOLEAN) == null){
                return;
            }

            if (!BoatManager.getInstance().isOwner(player, boatUUID)) {
                Logs.sendWarningMessageToPlayer(player, "kBoat", "Non è tuo.");
                event.setCancelled(true);
                return;
            }

            Inventory inv = InventoryManager.getInstance().getPlayerInventory(player, 27, "spazio personale");
            boat.getInventory().setContents(inv.getContents());
            Logs.sendSuccessActionBarToPlayer(player, "kBoat", "Inventario ottenuto");
        }
    }

    @EventHandler
    public void onPlayerCloseInventoryBoat(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder() instanceof ChestBoat boat) {
            UUID boatUUID = boat.getUniqueId();

            if (!BoatManager.getInstance().isOwner(player, boatUUID)) {
                return;
            }

            InventoryManager.getInstance().savePlayerInventory(player, boat.getInventory());

            Logs.sendSuccessActionBarToPlayer(player, "kBoat", "Inventario salvato");

            boat.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerHitBoat(VehicleDamageEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Boat boat = (Boat) event.getVehicle();

            if (boat.getPersistentDataContainer().get(BoatManager.getInstance().isPlayerBoatKey, PersistentDataType.BOOLEAN) == null){
                return;
            }


            if (event.getAttacker() instanceof Player) {
                UUID boatOwnerUUID = UUID.fromString(boat.getPersistentDataContainer().get(BoatManager.getInstance().ownerUuidKey, PersistentDataType.STRING));
                Player player = (Player) event.getAttacker();

                if(!boatOwnerUUID.equals(player.getUniqueId())){
                    Logs.sendWarningMessageToPlayer(player, "kBoat", "Non puoi rimuovere questa barca.");
                    event.setCancelled(true);
                }

                boat.remove();
                Logs.sendSuccessMessageToPlayer(player, "kBoat", "Barca rimossa, fai &e/kboat &aper respawnarla");
                
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }

    }
}
