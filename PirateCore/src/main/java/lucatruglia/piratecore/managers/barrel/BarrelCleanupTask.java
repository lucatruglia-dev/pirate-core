package lucatruglia.piratecore.managers.barrel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class BarrelCleanupTask extends BukkitRunnable {

    private final Queue<ArmorStand> queue;
    private final int batchSize;
    private int removed;

    public BarrelCleanupTask(World world, int batchSize) {
        this.batchSize = batchSize;
        this.removed = 0;

        List<ArmorStand> stands = new ArrayList<>();
        for (ArmorStand as : world.getEntitiesByClass(ArmorStand.class)) {
            String id = BarrelManager.getPDC(as, BarrelManager.BARREL_ID,
                    org.bukkit.persistence.PersistentDataType.STRING);
            if (id != null) stands.add(as);
        }
        this.queue = new LinkedList<>(stands);
    }

    @Override
    public void run() {
        if (queue.isEmpty()) {
            Bukkit.broadcastMessage("§a✔ Rimossi " + removed + " barili.");
            this.cancel();
            return;
        }

        for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
            ArmorStand as = queue.poll();
            BarrelManager.getInstance().removeBlockDisplay(as);
            BarrelManager.getInstance().updateTextDisplay(as, true);
            as.remove();
            removed++;
        }
    }
}
