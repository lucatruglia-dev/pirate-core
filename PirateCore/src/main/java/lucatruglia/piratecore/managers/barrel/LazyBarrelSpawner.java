package lucatruglia.piratecore.managers.barrel;

import lucatruglia.piratecore.managers.ConfigManager;
import lucatruglia.piratecore.models.BarrelData;
import lucatruglia.piratecore.utils.Logs;
import lucatruglia.piratecore.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LazyBarrelSpawner implements Listener {

    private final Map<Long, List<BarrelEntry>> chunkMap = new HashMap<>();
    private final Set<Long> processedChunks = new HashSet<>();
    private final World world;
    private final String filePath;

    public record BarrelEntry(int index, int x, int z, String configId) {
    }

    public LazyBarrelSpawner(World world, String filePath) {
        this.world = world;
        this.filePath = filePath;
    }

    /**
     * Carica tutte le coordinate dal config e le raggruppa per chunk.
     * Salta i barili già spawnati (quelli con armorStandUUID già salvato).
     */
    public void loadFromConfig(int amount, String defaultBarrelId) {
        chunkMap.clear();
        processedChunks.clear();

        for (int i = 0; i < amount; i++) {
            // Se l'UUID c'è già, il barile è già stato spawnato
            String uuid = ConfigManager.getInstance().getString(filePath, i + ".armorStandUUID");
            if (uuid != null && !uuid.isEmpty())
                continue;

            int x = 0, z = 0;
            Object raw = ConfigManager.getInstance().getConfig(filePath).get("" + i);

            if (raw instanceof List<?> list && list.size() >= 2) {
                // Vecchio formato: [X, Z] (generato da generateCoord precedente)
                x = (Integer) list.get(0);
                z = (Integer) list.get(1);
            } else if (raw != null) {
                // Nuovo formato nidificato: {X, Z, armorStandUUID}
                x = ConfigManager.getInstance().getInt(filePath, i + ".X");
                z = ConfigManager.getInstance().getInt(filePath, i + ".Z");
            } else {
                continue;
            }

            long chunkKey = chunkKey(x >> 4, z >> 4);
            chunkMap.computeIfAbsent(chunkKey, k -> new ArrayList<>())
                    .add(new BarrelEntry(i, x, z, defaultBarrelId));
        }
    }

    /**
     * Calcola la chunk key da coordinate chunk (non blocchi).
     * Equivalente a chunkKey(cx, cz) su Paper 1.16.5+.
     */
    private static long chunkKey(int cx, int cz) {
        return (long) cx & 0xFFFFFFFFL | ((long) cz & 0xFFFFFFFFL) << 32;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.getWorld().equals(world))
            return;

        Chunk chunk = event.getChunk();
        long key = chunkKey(chunk.getX(), chunk.getZ());

        if (processedChunks.contains(key))
            return;

        List<BarrelEntry> entries = chunkMap.get(key);
        if (entries == null || entries.isEmpty())
            return;

        processedChunks.add(key);

        // Controlla barili già esistenti nel chunk (da sessioni precedenti)
        Set<String> existingCoords = new HashSet<>();
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ArmorStand as) {
                if (BarrelManager.getPDC(as, BarrelManager.BARREL_ID, PersistentDataType.STRING) != null) {
                    existingCoords.add(as.getLocation().getBlockX() + "," + as.getLocation().getBlockZ());
                }
            }
        }

        // Spawna SOLO i barili di questo chunk (tipicamente 1-5)
        for (BarrelEntry entry : entries) {
            if (existingCoords.contains(entry.x + "," + entry.z))
                continue;

            Location spawnLoc = new Location(world, entry.x, 64, entry.z);
            BarrelData result = BarrelManager.getInstance().spawnBarrel(spawnLoc, entry.configId);
            Bukkit.getServer().broadcastMessage(filePath);
            Logs.sendLog("BARREL LOG", "Barile spawnato a " + Utils.coordToString(entry.x, entry.z));

            if (result != null) {
                // Salva UUID nel config per barrelList/cleanUp
                ConfigManager.getInstance().set(filePath, entry.index + ".X", entry.x);
                ConfigManager.getInstance().set(filePath, entry.index + ".Z", entry.z);
                ConfigManager.getInstance().set(filePath, entry.index + ".armorStandUUID",
                        result.armorStandUUID().toString());
            }
        }
    }

    public void despawnAllActiveBarrels() {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof ArmorStand as) {
                    String barrelId = BarrelManager.getPDC(as, BarrelManager.BARREL_ID, PersistentDataType.STRING);
                    if (barrelId != null) {
                        BarrelManager.getInstance().removeBlockDisplay(as);
                        BarrelManager.getInstance().updateTextDisplay(as, true);
                        as.remove();
                        Bukkit.getServer().broadcastMessage("Barile eliminato");
                    }
                }
            }
            Logs.sendLog("Despawn All Barrels","Analizzo chunk (X: "+chunk.getX() + ", Y: " + chunk.getZ() + ")");

        }

        processedChunks.clear(); // Permette il reload al passaggio successivo
        Bukkit.getServer().broadcastMessage(" - - - Fatto (?) - - - ");
    }

    /** @return numero di barili ancora in attesa di spawn */
    public int getPendingCount() {
        return chunkMap.values().stream().mapToInt(List::size).sum();
    }
}
