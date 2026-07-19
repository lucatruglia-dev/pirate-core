package lucatruglia.piratecore.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import lucatruglia.piratecore.models.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {
    private static BossBarManager instance;
    private JavaPlugin plugin;
    private boolean initialized = false;
    
    private final Map<UUID, BossBar> activeBars = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();
    
    private BossBarManager() {}
    
    public static BossBarManager getInstance() {
        if (instance == null) {
            instance = new BossBarManager();
        }
        return instance;
    }
    
    /**
     * Inizializza il manager e registra il listener per la pulizia automatica
     */
    public void initialize(JavaPlugin plugin) {
        if (initialized) {
            plugin.getLogger().warning("BossBarManager già inizializzato!");
            return;
        }
        
        this.plugin = plugin;
        this.initialized = true;
        instance = this;
        
        // Registra il listener per la pulizia automatica quando i player escono
        
        plugin.getLogger().info("BossBarManager inizializzato correttamente!");
    }
    
    /**
     * Mostra una BossBar a un giocatore
     */
    public void showBar(Player player, String title, double progress, BarColor color, BarStyle style) {
        checkInitialized();
        UUID uuid = player.getUniqueId();
        
        // Rimuovi eventuale barra esistente
        hideBar(player);
        
        // Crea nuova barra
        BossBar bar = Bukkit.createBossBar(
            colorize(title),
            color,
            style
        );
        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.addPlayer(player);
        bar.setVisible(true);
        
        activeBars.put(uuid, bar);
    }
    
    /**
     * Mostra una BossBar con stile predefinito
     */
    public void showBar(Player player, String title, double progress) {
        showBar(player, title, progress, BarColor.BLUE, BarStyle.SOLID);
    }
    
    /**
     * Mostra una BossBar per un determinato tempo (timer invisibile)
     */
    public void showTimedBar(Player player, String title, double progress, BarColor color, BarStyle style, int durationSeconds) {
        checkInitialized();
        UUID uuid = player.getUniqueId();
        
        // Rimuovi eventuale barra esistente
        hideBar(player);
        
        // Crea nuova barra
        BossBar bar = Bukkit.createBossBar(
            colorize(title),
            color,
            style
        );
        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.addPlayer(player);
        bar.setVisible(true);
        
        activeBars.put(uuid, bar);
        
        // Rimuovi eventuale task esistente
        cancelTask(uuid);
        
        // Crea task interno per rimuovere la barra dopo il tempo specificato
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            hideBar(player);
        }, durationSeconds * 20L);
        
        activeTasks.put(uuid, task);
    }
    
    /**
     * Mostra una BossBar per un determinato tempo (timer invisibile) - Versione con stile predefinito
     */
    public void showTimedBar(Player player, String title, double progress, int durationSeconds) {
        showTimedBar(player, title, progress, BarColor.BLUE, BarStyle.SOLID, durationSeconds);
    }
    
    /**
     * Mostra una BossBar con countdown automatico VISIBILE
     */
    public void showCountdownBar(Player player, String title, int durationSeconds, BarColor color, BarStyle style) {
        checkInitialized();
        UUID uuid = player.getUniqueId();
        
        // Rimuovi eventuale barra esistente
        hideBar(player);
        
        // Crea la barra
        BossBar bar = Bukkit.createBossBar(
            colorize(title + " §7(" + durationSeconds + "s)"),
            color,
            style
        );
        bar.setProgress(1.0);
        bar.addPlayer(player);
        bar.setVisible(true);
        
        activeBars.put(uuid, bar);
        
        // Rimuovi task esistente
        cancelTask(uuid);
        
        // Task per il countdown
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int timeLeft = durationSeconds;
            
            @Override
            public void run() {
                // Se il player non è più online, pulisci tutto
                if (!player.isOnline()) {
                    hideBar(player);
                    return;
                }
                
                if (timeLeft <= 0) {
                    hideBar(player);
                    return;
                }
                
                BossBar currentBar = activeBars.get(uuid);
                if (currentBar != null) {
                    double progress = (double) timeLeft / durationSeconds;
                    currentBar.setProgress(progress);
                    currentBar.setTitle(colorize(title + " §7(" + timeLeft + "s)"));
                    
                    // Cambia colore automaticamente quando il tempo è quasi scaduto
                    if (timeLeft <= 3) {
                        currentBar.setColor(BarColor.RED);
                    } else if (timeLeft <= 10) {
                        currentBar.setColor(BarColor.YELLOW);
                    }
                }
                
                timeLeft--;
            }
        }, 0L, 20L);
        
        activeTasks.put(uuid, task);
    }
    
    /**
     * Aggiorna una BossBar esistente
     */
    public boolean updateBar(Player player, String title, double progress) {
        checkInitialized();
        UUID uuid = player.getUniqueId();
        BossBar bar = activeBars.get(uuid);
        
        if (bar == null) {
            return false;
        }
        
        if (title != null) {
            bar.setTitle(colorize(title));
        }
        if (progress >= 0 && progress <= 1) {
            bar.setProgress(progress);
        }
        
        return true;
    }
    
    /**
     * Aggiorna solo il titolo di una BossBar esistente
     */
    public boolean updateTitle(Player player, String title) {
        return updateBar(player, title, -1);
    }
    
    /**
     * Aggiorna solo il progresso di una BossBar esistente
     */
    public boolean updateProgress(Player player, double progress) {
        return updateBar(player, null, progress);
    }
    
    /**
     * Nasconde la BossBar a un giocatore specifico
     */
    public void hideBar(Player player) {
        if (player == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Annulla eventuale task
        cancelTask(uuid);
        
        // Rimuovi la barra
        BossBar bar = activeBars.remove(uuid);
        if (bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);
        }
    }
    
    /**
     * Nasconde la BossBar e la rimuove definitivamente (non riutilizzabile)
     */
    public void removeBar(Player player) {
        if (player == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Annulla eventuale task
        cancelTask(uuid);
        
        // Rimuovi la barra
        BossBar bar = activeBars.remove(uuid);
        if (bar != null) {
            bar.removeAll();
            bar.setVisible(false);
        }
    }
    
    /**
     * Verifica se un giocatore ha una BossBar attiva
     */
    public boolean hasBar(Player player) {
        return player != null && activeBars.containsKey(player.getUniqueId());
    }
    
    /**
     * Ottiene la BossBar attiva di un giocatore (se presente)
     */
    public BossBar getBar(Player player) {
        return player != null ? activeBars.get(player.getUniqueId()) : null;
    }
    
    /**
     * Rimuove tutte le BossBar attive (da usare in onDisable)
     */
    public void clearAll() {
        // Annulla tutti i task
        for (BukkitTask task : activeTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();
        
        // Rimuovi tutte le barre
        for (Map.Entry<UUID, BossBar> entry : activeBars.entrySet()) {
            BossBar bar = entry.getValue();
            if (bar != null) {
                bar.removeAll();
                bar.setVisible(false);
            }
        }
        activeBars.clear();
        
        if (plugin != null) {
            plugin.getLogger().info("Tutte le BossBar sono state pulite!");
        }
    }
    
    /**
     * Mostra una BossBar con le statistiche del livello del giocatore.
     * (Spostato da PlayerManager per separare UI dalla logica XP)
     */
    public void showPlayerLevelBar(Player player){
        if (player == null) return;
        
        PlayerData data = PlayerManager.getInstance().getInfo(player);
        
        // XP accumulate in questo specifico livello (totalXp - XP per raggiungere il livello corrente)
        long xpInCurrentLevel = data.totalXp() - LevelManager.getInstance().getTotalXpNeededForLevel(data.level());
        // XP necessarie per passare dal livello corrente al prossimo
        long xpNeededForNextLevel = LevelManager.getInstance().getXpNeededForLevel(data.level() + 1);
        

        this.showTimedBar(
            player,
            colorize(formatBossBarTitle(data.level(), data.totalXp(), xpNeededForNextLevel)),
            xpNeededForNextLevel > 0 ? ((double) xpInCurrentLevel / xpNeededForNextLevel) : 0.0,
            BarColor.YELLOW,
            BarStyle.SEGMENTED_20,
            5
        );
    }

    private String formatBossBarTitle(int currentLevel, long currentXp, long nextLevelXp) {
        final int TARGET_LENGTH = 32;
        final String FALLBACK_SPACES = "   "; // 3 spazi di sicurezza se si sfora

        String leftPart = String.valueOf(currentLevel);
        String centerPart = currentXp + "/" + nextLevelXp;
        String rightPart = String.valueOf(currentLevel + 1);

        int textLength = leftPart.length() + centerPart.length() + rightPart.length();

        int spacesLeft = TARGET_LENGTH - textLength;

        String paddingLeft;
        String paddingRight;

        if (spacesLeft > 2) {
            int padSize = spacesLeft / 2;
            
            paddingLeft = " ".repeat(padSize);
            paddingRight = " ".repeat(spacesLeft - padSize);
        } else {
            paddingLeft = FALLBACK_SPACES;
            paddingRight = FALLBACK_SPACES;
        }

        leftPart = "&r&e" + leftPart + "&r";
        rightPart = "&0&e" + rightPart + "&r";
        centerPart = "&r" + centerPart + "&r";

        // 4. Componiamo il titolo finale
        return leftPart + paddingLeft + centerPart + paddingRight + rightPart;
    }
    
    /**
     * Pulisce le BossBar di un giocatore quando esce dal server
     * (Viene chiamato automaticamente dal listener)
     */
    public void onPlayerQuit(Player player) {
        if (player == null) return;
        hideBar(player);
    }
    
    /**
     * Controlla se il manager è stato inizializzato
     */
    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("BossBarManager non è stato inizializzato! Chiama initialize() prima di usarlo.");
        }
    }
    
    /**
     * Annulla un task specifico
     */
    private void cancelTask(UUID uuid) {
        BukkitTask task = activeTasks.remove(uuid);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    /**
     * Sostituisce i colori con & con §
     */
    private String colorize(String message) {
        if (message == null) return "";
        return message.replace('&', '§');
    }
}