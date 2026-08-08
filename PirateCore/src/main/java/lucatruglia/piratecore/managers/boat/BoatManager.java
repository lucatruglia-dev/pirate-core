package lucatruglia.piratecore.managers.boat;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.events.OnPlayerJoinOnHisChestBoatEvent;
import lucatruglia.piratecore.events.OnPlayerLeftOnHisChestBoatEvent;
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

    public List<String> getAvailableTrails(Player player){
        String playerUUID = player.getUniqueId().toString();
        return data.getStringList(playerUUID+".available_trails");
    }

    public void addAvailableTrail(Player player, String trailID){
        List<String> available_trails = getAvailableTrails(player);
        String playerUUID = player.getUniqueId().toString();
        available_trails.add(trailID);
        ConfigManager.getInstance().set(filePath, playerUUID+".available_trails", available_trails);
        reloadData();
    }

    public void removeAvailableTrails(Player player, String trailID){
        String playerUUId = player.getUniqueId().toString();
        List<String> available_trails = getAvailableTrails(player);
        available_trails.remove(trailID);
        ConfigManager.getInstance().set(filePath, playerUUId+".available_trails", available_trails);
        reloadData();
    }

    public Boolean isAvailableTrails(Player player, String trailID){
        List<String> avilableList = getAvailableTrails(player);
        if(avilableList == null | avilableList.isEmpty()){
            return false;
        }
        return avilableList.contains(trailID);
    }

    public List<String> getActivedTrails(Player player){
        String playerUUId = player.getUniqueId().toString();
        return data.getStringList(playerUUId + ".active_trails");
    }

    public boolean addActiveTrails(Player player, String traildID){
        if(!isAvailableTrails(player, traildID)){
            return false;
        }

        String playerUUId = player.getUniqueId().toString();
        List<String> active_trails = getActivedTrails(player);
        active_trails.add(traildID);
        ConfigManager.getInstance().set(filePath, playerUUId+".active_trails", active_trails);
        reloadData();
        return true;
    }

    public void removeActiveTrails(Player player, String trailID){
        String playerUUId = player.getUniqueId().toString();
        List<String> active_trails = getActivedTrails(player);
        active_trails.remove(trailID);
        ConfigManager.getInstance().set(filePath, playerUUId+".active_trails", active_trails);
        reloadData();
    }

    public void reloadData(){
        this.data = ConfigManager.getInstance().getConfig(filePath);
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

    public Boolean playerRideBoat(Player player, UUID boatUUID){
        Entity boatEntity = Bukkit.getEntity(boatUUID);
        if(boatEntity == null){
            return false;
        }
        ChestBoat boat = (ChestBoat) boatEntity;
        UUID playerUUID = player.getUniqueId();
        UUID onwerboatUUID = UUID.fromString(boat.getPersistentDataContainer().get(ownerUuidKey, PersistentDataType.STRING));

        if(playerUUID == null || onwerboatUUID == null){
            return false;
        }

        if(playerUUID.equals(onwerboatUUID)){
            Boat boat_temp = new Boat(player, BoatType.DEFAULT, getActivedTrails(player));
            OnPlayerJoinOnHisChestBoatEvent event = new OnPlayerJoinOnHisChestBoatEvent(player, boat_temp);
            Bukkit.getServer().getPluginManager().callEvent(event);
            return true;
        }

        return false;
    }

    public void playerLeftBoat(Player player, UUID boatUUID){
        Entity boatEntity = Bukkit.getEntity(boatUUID);
        if(boatEntity == null){
            return;
        }
        ChestBoat boat = (ChestBoat) boatEntity;
        UUID playerUUID = player.getUniqueId();
        UUID onwerboatUUID = UUID.fromString(boat.getPersistentDataContainer().get(ownerUuidKey, PersistentDataType.STRING));

        if(playerUUID == null || onwerboatUUID == null){
            return;
        }

        if(playerUUID.equals(onwerboatUUID)){
            Boat boat_temp = new Boat(player, BoatType.DEFAULT, getActivedTrails(player));
            OnPlayerLeftOnHisChestBoatEvent event = new OnPlayerLeftOnHisChestBoatEvent(player, boat_temp);
            Bukkit.getServer().getPluginManager().callEvent(event);
            return;
        }

    }

    public void spawnBoat(Player player) {
        Boat boat = new Boat(player, BoatType.DEFAULT, getActivedTrails(player));
        boat.spawnBoat();
        ChestBoat chestBoat = boat.getChestBoat();

        initializePlayer(player);
        despawnOldBoat(player);
        setNewBoat(player, chestBoat.getUniqueId());
    }
}
