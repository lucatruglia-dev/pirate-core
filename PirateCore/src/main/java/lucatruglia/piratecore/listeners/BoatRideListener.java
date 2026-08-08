package lucatruglia.piratecore.listeners;

import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.managers.boat.BoatManager;

public class BoatRideListener implements Listener {

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        Entity entity = event.getEntered();

        // Check if the entering entity is a player and the vehicle is a boat
        if (entity instanceof Player && vehicle instanceof ChestBoat) {
            Player player = (Player) entity;
            ChestBoat boat = (ChestBoat) vehicle;

            Boolean res = boat.getPersistentDataContainer().get(BoatManager.getInstance().isPlayerBoatKey, PersistentDataType.BOOLEAN);

            if(res==null){
                return;
            }

            Boolean isPlayerOwner = BoatManager.getInstance().playerRideBoat(player, boat.getUniqueId());

            event.setCancelled(!isPlayerOwner);
        }
    }


    @EventHandler
    public void onVehicleEnter(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        Entity entity = event.getExited();

        // Check if the entering entity is a player and the vehicle is a boat
        if (entity instanceof Player && vehicle instanceof ChestBoat) {
            Player player = (Player) entity;
            ChestBoat boat = (ChestBoat) vehicle;

            Boolean res = boat.getPersistentDataContainer().get(BoatManager.getInstance().isPlayerBoatKey, PersistentDataType.BOOLEAN);

            if(res==null){
                return;
            }

            BoatManager.getInstance().playerLeftBoat(player, boat.getUniqueId());

        }
    }
}