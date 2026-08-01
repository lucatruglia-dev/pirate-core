package lucatruglia.piratecore.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class Utils {
    
    /**
     * Formatta un double per display: mostra come intero se non ha decimali significativi,
     * altrimenti con 2 decimali.
     */
    public static String formatDouble(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }
    
    public static String colorize(String msg) {
        Matcher match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, String.valueOf(ChatColor.of(color)));
            match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String coordToString(int x, int y, int z){
        return "("+x +  ", " +y +  ", " + z + ")";  
    }

    public static String coordToString(int x, int z){
        return "("+x +  ", " + z + ")";  
    }

    public static long secondsToTicks(int seconds){
        return ((long)seconds)*20;
    } 
    
    // Add more utility methods here
}