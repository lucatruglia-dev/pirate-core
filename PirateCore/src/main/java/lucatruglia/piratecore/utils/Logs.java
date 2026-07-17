package lucatruglia.piratecore.utils;

import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;

public class Logs {
    public static void sendLog(String object, String message) {
        PirateCore.get().getLogger().info("["+object+"] "+message);
    }

    public static void sendSuccessMessageToPlayer(Player player, String object, String text){
        player.sendMessage(
            Utils.colorize("&a&l[&r&a"+object+"&a&l] &a"+text)
        );
    }
}
