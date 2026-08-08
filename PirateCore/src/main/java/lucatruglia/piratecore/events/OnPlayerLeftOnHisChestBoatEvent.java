package lucatruglia.piratecore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lucatruglia.piratecore.managers.boat.Boat;

public class OnPlayerLeftOnHisChestBoatEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Boat boat;

    public OnPlayerLeftOnHisChestBoatEvent(Player player, Boat boat) {
        this.player = player;
        this.boat = boat;
    }

    public Player getPlayer() {
        return player;
    }

    public Boat getBoat() {
        return boat;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
