package lucatruglia.piratecore.placeholders;

import lucatruglia.piratecore.managers.DatabaseManager;
import lucatruglia.piratecore.managers.LevelManager;
import lucatruglia.piratecore.models.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.Optional;

/**
 * Espansione PlaceholderAPI per PirateCore.
 *
 * Placeholder supportati per il giocatore stesso:
 *   %piratecore_level%
 *   %piratecore_xp%
 *   %piratecore_xp_into%
 *   %piratecore_xp_to_next%
 *   %piratecore_progress%
 *   %piratecore_progress_percent%
 *
 * Placeholder per un giocatore specifico (nome senza spazi):
 *   %piratecore_level_Kcalu_%
 *   %piratecore_xp_Kcalu_%
 *   %piratecore_progress_Kcalu_%
 *
 * Sono supportate anche le virgolette intorno al nome:
 *   %piratecore_level_"Kcalu_"%
 */
public class PirateCoreExpansion extends PlaceholderExpansion {


    public static void enable(JavaPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            boolean registered = new PirateCoreExpansion().register();
            if (registered) {
                plugin.getLogger().info("PlaceholderAPI expansion registered successfully!");
            } else {
                plugin.getLogger().warning("Failed to register PlaceholderAPI expansion!");
            }
        } else {
            plugin.getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }
    }

    @Override
    public String getIdentifier() {
        return "piratecore";
    }

    @Override
    public String getAuthor() {
        return "lucatruglia";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer viewer, String params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        // Estrai il parametro richiesto e l'eventuale nome del player target
        String[] parts = params.split("_", 2);
        String key = parts[0].toLowerCase();
        String targetPlayerName = (parts.length > 1) ? parts[1] : null;

        // Se è stato specificato un nome, puliscilo da eventuali virgolette
        if (targetPlayerName != null) {
            targetPlayerName = stripQuotes(targetPlayerName);
        }

        // Ottieni i dati del player di destinazione
        PlayerData data = resolvePlayerData(targetPlayerName, viewer);
        if (data == null) {
            return "N/A";
        }

        // Calcola statistiche extra
        int level = data.level();
        long totalXp = data.totalXp();
        long xpNeededForLevel = LevelManager.getInstance().getTotalXpNeededForLevel(data.level() + 1);

        double progress = (double) ((double) data.totalXp() / (double)xpNeededForLevel);

        

        // Restituisci il valore in base alla chiave
        switch (key) {
            case "level":
                return String.valueOf(level);

            case "xp":
                return String.valueOf(totalXp);

            case "progress":
                return String.format("%.2f", progress);

            case "progress_percent":
                return String.format("%.1f%%", progress * 100.0D);

            default:
                return null;
        }
    }

    // ---------------------------------------------------------------
    // Metodi di supporto
    // ---------------------------------------------------------------

    /**
     * Risolve i dati del player target.
     * Se targetName è specificato, cerca nel database o tra i giocatori online.
     * Altrimenti usa il player viewer (se non null).
     *
     * @param targetName Nome del player target (può essere null)
     * @param viewer     Giocatore che visualizza il placeholder (può essere null)
     * @return PlayerData o null se non trovato
     */
    private PlayerData resolvePlayerData(String targetName, OfflinePlayer viewer) {
        // Caso 1: è stato specificato un nome target
        if (targetName != null && !targetName.isEmpty()) {
            // Cerca prima tra i giocatori online (dati live)
            OfflinePlayer target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                Optional<PlayerData> liveData = DatabaseManager.getInstance().loadPlayer(target.getUniqueId());
                if (liveData.isPresent()) {
                    return liveData.get();
                }
            }

            // Cerca tra i giocatori offline (per nome)
            for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                if (offline.getName() != null && offline.getName().equalsIgnoreCase(targetName)) {
                    Optional<PlayerData> data = DatabaseManager.getInstance().loadPlayer(offline.getUniqueId());
                    if (data.isPresent()) {
                        return data.get();
                    }
                }
            }

            // Cerca direttamente per nome nel database
            Optional<PlayerData> data = DatabaseManager.getInstance().loadPlayerByName(targetName);
            return data.orElse(null);
        }

        // Caso 2: nessun nome target — usa il viewer
        if (viewer == null) {
            return null;
        }

        Optional<PlayerData> data = DatabaseManager.getInstance().loadPlayer(viewer.getUniqueId());
        return data.orElse(null);
    }

    /**
     * Rimuove le virgolette all'inizio e alla fine di una stringa.
     * Esempio: "\"Kcalu_\"" → "Kcalu_"
     */
    private String stripQuotes(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String result = value;
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }
}
