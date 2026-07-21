package lucatruglia.piratecore.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import lucatruglia.piratecore.PirateCore;

public class AnimationManager {
    private static AnimationManager instance;
    private final List<BlockDisplay> barili = new ArrayList<>();
    private final JavaPlugin plugin = PirateCore.get();
    private boolean andandoSu = true;

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    public void initialize() {
        instance = this;
        startGlobalTimer();
    }

    public void aggiungiBarile(BlockDisplay display) {
        // 1. Diciamo al client di metterci 30 tick (1.5 secondi) a completare il
        // movimento
        display.setInterpolationDuration(30);
        display.setInterpolationDelay(0);
        barili.add(display);
    }

    public void startGlobalTimer() {
        // Unico timer per TUTTI i barili del server
        new BukkitRunnable() {
            @Override
            public void run() {
                // Rimuove i barili distrutti per evitare perdite di memoria
                barili.removeIf(b -> !b.isValid() || b.isDead());

                float targetY = andandoSu ? 0.2f : 0.0f; // Escursione massima di 0.2 blocchi
                andandoSu = !andandoSu;

                for (BlockDisplay b : barili) {
                    Transformation trans = b.getTransformation();
                    Vector3f translation = trans.getTranslation();
                    translation.y = targetY;

                    // Applica il movimento: il server invia un solo pacchetto leggero
                    b.setTransformation(new Transformation(
                            translation, trans.getLeftRotation(), trans.getScale(), trans.getRightRotation()));
                    b.setInterpolationDelay(0); // Forza l'inizio immediato sul client
                }
            }
        }.runTaskTimer(plugin, 0L, 30L); // Gira solo una volta ogni 1.5 secondi (30 tick)
    }

}
