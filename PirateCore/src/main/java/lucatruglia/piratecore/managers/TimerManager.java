package lucatruglia.piratecore.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lucatruglia.piratecore.utils.Logs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerManager {

    private JavaPlugin plugin;
    // Mappa per tracciare il task attivo associato al UUID del giocatore
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();
    // Mappa opzionale per tranciare i secondi rimanenti (utile per la pausa)
    private final Map<UUID, Integer> timeRemaining = new HashMap<>();

    public static TimerManager instance;

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void initialize(JavaPlugin plugin){
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Avvia un timer a conto alla rovescia (Countdown) per un giocatore.
     *
     * @_player Il giocatore a cui assegnare il timer.
     * @param seconds I secondi totali di durata.
     * @param onFinish Azione (Runnable) da eseguire allo scadere del tempo.
     */
    public void startCountdown(Player player, int seconds, String text, Runnable onFinish) {
        UUID uuid = player.getUniqueId();
        
        // Se ha già un timer attivo, lo annulliamo prima di crearne uno nuovo
        cancelTimer(player);

        timeRemaining.put(uuid, seconds);

        BukkitTask task = new BukkitRunnable() {
            int timeLeft = timeRemaining.get(uuid);

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTimer(player);
                    this.cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    // Timer scaduto
                    Logs.sendWarningActionBarToPlayer(player, formatTime(0), "Tempo scaduto");
                    cancelTimer(player);
                    if (onFinish != null) {
                        onFinish.run();
                    }
                    this.cancel();
                    return;
                }

                // Aggiorna il tempo rimanente e mostra l'Action Bar
                timeRemaining.put(uuid, timeLeft);
                // player.sendActionBar(ChatColor.YELLOW + "Tempo rimasto: " + formatTime(timeLeft));
                Logs.sendSuccessActionBarToPlayer(player, "⌚ "+ formatTime(timeLeft), text);
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Esegue ogni secondo (20 ticks)

        activeTasks.put(uuid, task);
    }

    /**
     * Ferma (Mette in pausa) il timer del giocatore preservando il tempo rimanente.
     */
    public void pauseTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
            player.sendMessage(ChatColor.GOLD + "Timer messo in pausa.");
        }
    }

    /**
     * Riprende il timer dal punto in cui è stato fermato.
     */
    public void resumeTimer(Player player, Runnable onFinish) {
        UUID uuid = player.getUniqueId();
        if (timeRemaining.containsKey(uuid) && !activeTasks.containsKey(uuid)) {
            int remaining = timeRemaining.get(uuid);
            startCountdown(player, remaining, "",onFinish);
            player.sendMessage(ChatColor.GREEN + "Timer ripreso.");
        } else {
            player.sendMessage(ChatColor.RED + "Nessun timer in pausa trovato.");
        }
    }

    /**
     * Annulla completamente e rimuove il timer del giocatore azzerando tutto.
     */
    public void cancelTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
        }
        timeRemaining.remove(uuid);
        player.sendMessage(ChatColor.GRAY + "Timer annullato.");
    }

    /**
     * Verifica se il giocatore ha un timer attivo.
     */
    public boolean hasActiveTimer(Player player) {
        return activeTasks.containsKey(player.getUniqueId());
    }

    /**
     * Converte i secondi in un formato leggibile (mm:ss).
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}