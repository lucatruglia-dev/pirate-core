package lucatruglia.piratecore.utils;

import lucatruglia.piratecore.PirateCore;

public class Logs {
    public static void sendLog(String object, String message) {
        PirateCore.get().getLogger().info("["+object+"] "+message);


        PirateCore.get().getServer().getPlayer("Kcalu_")
            .sendMessage("["+object+"] "+message);
    }
}
