package lucatruglia.piratecore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.models.Reward;

public class OnBarrelHitEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final BarrelData barrelIDs;
    private final Reward barrelReward;
    private final int barrelLife;
    
    
    public OnBarrelHitEvent(Player player, BarrelData barrelIDs, Reward barrelReward, int barrelLife){
        this.player = player;
        this.barrelIDs = barrelIDs;
        this.barrelLife = barrelLife;
        this.barrelReward = barrelReward;
    }
    
    public Reward getBarrelReward() {
        return barrelReward;
    }
    
    public int getBarrelLife() {
        return barrelLife;
    }

    public BarrelData getBarrelIDs() {
        return barrelIDs;
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
