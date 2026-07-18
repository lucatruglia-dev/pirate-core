package lucatruglia.piratecore.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.ListMessage.Row;

public class Logs {
    public static void sendLog(String object, String message) {
        PirateCore.get().getLogger().info("[" + object + "] " + message);
    }

    public static void sendSuccessMessageToPlayer(Player player, String object, String text) {
        player.sendMessage(
                Utils.colorize("&a&l[&r&a" + object + "&a&l] &a" + text));
    }

    public static void sendListMessageToPlayer(Player player, ListMessage listMessage) {
        List<String> fullMessage = new ArrayList<>();

        fullMessage.add(Utils.colorize("&6&l[PC] "+listMessage.title));

        for (Row row : listMessage.rows) {
            fullMessage.add(Utils.colorize("&6"+row.key+" ► &e"+row.value));
        }


        player.sendMessage(fullMessage.toArray(new String[0]));
    }
}
