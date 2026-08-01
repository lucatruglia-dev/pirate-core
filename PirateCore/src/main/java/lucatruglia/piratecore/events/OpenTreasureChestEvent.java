package lucatruglia.piratecore.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenTreasureChestEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final UUID mapUUID;
    private final UUID playerUUID;


    public OpenTreasureChestEvent(Player player, UUID mapUUID, UUID ownerUUID){
        this.player = player;
        this.mapUUID = mapUUID;
        this.playerUUID = ownerUUID;
    }


    public UUID getMapUUID() {
        return mapUUID;
    }


    public UUID getOwnerUUID() {
        return playerUUID;
    }


    public Player getPlayer(){
        return player;
    }

    

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
