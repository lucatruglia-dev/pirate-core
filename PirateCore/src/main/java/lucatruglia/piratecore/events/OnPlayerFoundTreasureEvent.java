package lucatruglia.piratecore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lucatruglia.piratecore.managers.treasure.Rarity;

public class OnPlayerFoundTreasureEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Rarity rarity;

    public OnPlayerFoundTreasureEvent(Player player, Rarity rarity) {
        this.player = player;
        this.rarity = rarity;
    }

    public Rarity getRarity() {
        return rarity;
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
