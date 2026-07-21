package lucatruglia.piratecore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int newlevel;


    public LevelUpEvent(Player player, int newlevel){
        this.player = player;
        this.newlevel = newlevel;
    }


    public Player getPlayer(){
        return player;
    }

    public int getNewlevel(){
        return newlevel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
