package lucatruglia.piratecore.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lucatruglia.piratecore.managers.treasure.Rarity;
import lucatruglia.piratecore.models.Coordinate;

public class OnPlayerStartTreasureEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Rarity rarity;
    private final Coordinate trasureCoord;
    private final UUID treasureUUID;

    public OnPlayerStartTreasureEvent(Player player, Rarity rarity, Coordinate trasureCoord, UUID treasureUUID) {
        this.player = player;
        this.rarity = rarity;
        this.trasureCoord = trasureCoord;
        this.treasureUUID = treasureUUID;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Coordinate getTrasureCoord() {
        return trasureCoord;
    }

    public UUID getTreasureUUID() {
        return treasureUUID;
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
