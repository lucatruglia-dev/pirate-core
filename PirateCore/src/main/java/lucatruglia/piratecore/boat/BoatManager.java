package lucatruglia.piratecore.boat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.OakChestBoat;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.managers.ConfigManager;
import lucatruglia.piratecore.utils.Logs;

public class BoatManager {
    private static BoatManager instance;

    FileConfiguration data;
    private final String filePath = "data/boat.yml";
    public NamespacedKey isPlayerBoatKey;
    public NamespacedKey ownerUuidKey;

    public static BoatManager getInstance() {
        if (instance == null) {
            instance = new BoatManager();
        }
        return instance;
    }

    public void initialize() {
        this.data = ConfigManager.getInstance().getConfig(filePath);
        this.isPlayerBoatKey = new NamespacedKey(PirateCore.get(), "isPlayerBoat");
        this.ownerUuidKey = new NamespacedKey(PirateCore.get(), "ownerUUID");
        instance = this;
    }

    private boolean isPlayerOnDb(Player player){
        String player_UUID = player.getUniqueId().toString();

        if (data.get(player_UUID) == null) {
            return false;
        }

        return true;
    }

    private boolean hasPlayerHaveBoat(Player player){
        if(!isPlayerOnDb(player)){
            return false;
        }

        else if(data.get(player.getUniqueId().toString()+".boat_uuid") == null){
            return false;
        } 

        else {

            Logs.sendLog("debug", "il player ha una boat");
            return true;
        }

    }

    public void initializePlayer(Player player) {
        if(isPlayerOnDb(player)){
            return;
        }

        ConfigManager.getInstance().set(filePath, player.getUniqueId().toString() + ".skin", "oak");
        this.data = ConfigManager.getInstance().getConfig(filePath);
    }

    public void despawnOldBoat(Player player) {
        if(hasPlayerHaveBoat(player)){

            UUID boatUUID = UUID.fromString(
                data.getString(player.getUniqueId().toString()+".boat_uuid")
            );
            ConfigManager.getInstance().set(filePath, player.getUniqueId().toString() + ".boat_uuid", null);

            this.data = ConfigManager.getInstance().getConfig(filePath);

            Entity boatEntity =  Bukkit.getEntity(boatUUID);
            if(boatEntity != null){
                boatEntity.remove();
            }

        }
    }

    public void setNewBoat(Player player, UUID newBoatUUID){
        ConfigManager.getInstance().set(filePath, player.getUniqueId().toString() + ".boat_uuid", newBoatUUID.toString());
        this.data = ConfigManager.getInstance().getConfig(filePath);
    }

    public boolean isOwner(Player player, UUID boatUUID){
        UUID boatRealOwnerUUID = UUID.fromString(data.getString(player.getUniqueId().toString()+".boat_uuid"));

        if(boatRealOwnerUUID == null){
            return false;
        }

        else if(boatRealOwnerUUID.equals(boatUUID)){
            return true;
        }

        return false;
    }

    public void spawnBoat(Player player) {
        Location loc = player.getLocation();
        OakChestBoat chestBoat = loc.getWorld().spawn(loc, OakChestBoat.class);
        Logs.sendSuccessMessageToPlayer(player, "Boat", "" + chestBoat.getUniqueId().toString());

        chestBoat.getPersistentDataContainer().set(isPlayerBoatKey, PersistentDataType.BOOLEAN, true);
        chestBoat.getPersistentDataContainer().set(ownerUuidKey, PersistentDataType.STRING, player.getUniqueId().toString());

        initializePlayer(player);
        despawnOldBoat(player);
        setNewBoat(player, chestBoat.getUniqueId());
    }
}
