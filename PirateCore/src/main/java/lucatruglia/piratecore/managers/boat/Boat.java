package lucatruglia.piratecore.managers.boat;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.OakChestBoat;
import org.bukkit.persistence.PersistentDataType;

public class Boat {
    BoatType boatType;
    OakChestBoat chestBoat;
    Player playerOwner;
    List<String> activatedTrails;

    public List<String> getActivatedTrails() {
        return activatedTrails;
    }

    public BoatType getBoatType() {
        return boatType;
    }

    public OakChestBoat getChestBoat() {
        return chestBoat;
    }

    public Player getPlayerOwner() {
        return playerOwner;
    }

    public Boat(Player playerOwner, BoatType boatType, List<String> activatedTrails) {
        this.boatType = boatType;
        this.playerOwner = playerOwner;
        this.activatedTrails = activatedTrails;
    }

    public void spawnBoat(){
        Location loc = playerOwner.getLocation();
        this.chestBoat = loc.getWorld().spawn(loc, OakChestBoat.class);
        chestBoat.getPersistentDataContainer().set(BoatManager.getInstance().isPlayerBoatKey, PersistentDataType.BOOLEAN, true);
        chestBoat.getPersistentDataContainer().set(BoatManager.getInstance().ownerUuidKey, PersistentDataType.STRING, playerOwner.getUniqueId().toString());
    }


}
