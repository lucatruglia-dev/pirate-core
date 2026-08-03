package lucatruglia.piratecore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.models.BarrelReward;

public class OnBarrelDestroyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final BarrelData barrel_info;
    private final BarrelReward barrelReward;

    public OnBarrelDestroyEvent(Player player, BarrelData barrelIDs, BarrelReward barrelReward) {
        this.player = player;
        this.barrel_info = barrelIDs;
        this.barrelReward = barrelReward;
    }

    public BarrelReward getBarrelReward() {
        return barrelReward;
    }

    public BarrelData getBarrel_info() {
        return barrel_info;
    }

    public Player getPlayer() {
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
