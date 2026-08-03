package lucatruglia.piratecore.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.models.ListMessage.Button;
import lucatruglia.piratecore.models.ListMessage.Row;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Logs {


    
    public static void sendLog(String object, String message) {
        PirateCore.get().getLogger().info("[" + object + "] " + message);
    }

    public static void sendSuccessActionBarToPlayer(Player player, String object, String text) {
        if(player == null || !player.isOnline())
            return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Utils.colorize("&a&l[&r&a" + object + "&a&l] &a" + text)));
    }

    public static void sendWarningActionBarToPlayer(Player player, String object, String text) {
        if(player == null || !player.isOnline())
            return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Utils.colorize("&c&l[&r&4" + object + "&c&l] &a" + text)));
    }


    public static void sendSuccessMessageToPlayer(Player player, String object, String text) {
        if(player == null || !player.isOnline())
            return;
        player.sendMessage(
                Utils.colorize("&a&l[&r&a" + object + "&a&l] &a" + text));
    }

    public static void sendWarningMessageToPlayer(Player player, String object, String text) {
        if(player == null || !player.isOnline())
            return;
        player.sendMessage(
                Utils.colorize("&c&l[&r&4" + object + "&c&l] &c" + text));
    }

    public static void sendListMessageToPlayer(Player player, ListMessage listMessage) {
        if(player == null || !player.isOnline())
            return;
        List<String> fullMessage = new ArrayList<>();

        fullMessage.add(Utils.colorize(""));
        fullMessage.add(Utils.colorize("&6&l"+listMessage.title));

        if (!listMessage.rows.isEmpty()){
            for (Row row : listMessage.rows) {
                fullMessage.add(Utils.colorize("&6"+row.key+" ► &e"+row.value));
            }
        }

        if (listMessage.buttons!=null){
            fullMessage.add("");
            for (Button button : listMessage.buttons) {
                fullMessage.add(Utils.colorize("&9&l[&r&b"+button.text+"&9&l]"));
            }
        }

        fullMessage.add("");
        player.sendMessage(fullMessage.toArray(new String[0]));
    }
}
