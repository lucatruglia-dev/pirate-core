package lucatruglia.piratecore.managers;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import emanondev.itemedit.ItemEdit;
import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.events.OnBarrelDestroyEvent;
import lucatruglia.piratecore.events.OnBarrelHitEvent;
import lucatruglia.piratecore.models.*;
import lucatruglia.piratecore.utils.*;

public class BarrelManager {

    private static BarrelManager instance;
    private FileConfiguration config;

    // --- NamespacedKeys ---
    public static NamespacedKey ACTUAL_LIFE;
    public static NamespacedKey MAX_LIFE;
    public static NamespacedKey XP_REWARD;
    public static NamespacedKey MONEY_REWARD;
    public static NamespacedKey BARREL_ID;
    public static NamespacedKey TEXT_UUID;
    public static NamespacedKey BLOCK_UUID;

    public static BarrelManager getInstance() {
        if (instance == null) {
            instance = new BarrelManager();
        }
        return instance;
    }

    public void initialize() {
        this.config = ConfigManager.getInstance().getConfig("settings/barrels.yml");
        initKeys();
    }

    private void initKeys() {
        ACTUAL_LIFE = key("life");
        MAX_LIFE = key("maxlife");
        XP_REWARD = key("xpreward");
        MONEY_REWARD = key("moneyreward");
        BARREL_ID = key("id");
        TEXT_UUID = key("textUUID");
        BLOCK_UUID = key("blockUUID");
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(PirateCore.get(), name);
    }

    // ---------- Spawn (ritorna UUID / null) ----------

    public BarrelData spawnBarrel(Location loc, String configId) {
        int life = config.getInt("barrels." + configId + ".life");
        double xp = config.getDouble("barrels." + configId + ".xp_reward");
        int money = config.getInt("barrels." + configId + ".money_reward");
        return spawnBarrel(loc, life, xp, money, configId);
    }

    public BarrelData spawnBarrel(Location loc, int maxLife, double xpReward, int moneyReward, String id) {

        Location airLoc = findAirAboveWaterOnAxisY(loc);
        if (airLoc == null)
            return null;

        World world = airLoc.getWorld();
        int bx = airLoc.getBlockX();
        int bz = airLoc.getBlockZ();

        // ArmorStand: centro del blocco, 1 sotto
        Location asLoc = new Location(world, bx + 0.5, airLoc.getBlockY() - 1, bz + 0.5);
        // Display: offset dal blocco
        Location dispLoc = new Location(world, bx, airLoc.getBlockY() - 0.5, bz);

        // --- Spawn entità ---
        TextDisplay td = world.spawn(dispLoc.clone().add(0.5, 1.5, 0.5), TextDisplay.class);
        td.setText(getBarrelName(maxLife, maxLife, xpReward, moneyReward));
        td.setBillboard(Display.Billboard.CENTER);

        ArmorStand as = (ArmorStand) world.spawnEntity(asLoc, EntityType.ARMOR_STAND);
        as.setSmall(false);
        as.setInvulnerable(false);
        as.setInvisible(true);
        as.setGravity(false);
        as.setCustomNameVisible(false);

        BlockDisplay bd = world.spawn(dispLoc, BlockDisplay.class);
        bd.setBlock(Material.BARREL.createBlockData());

        // --- Persistenza dati su ArmorStand ---
        setPDC(as, ACTUAL_LIFE, PersistentDataType.INTEGER, maxLife);
        setPDC(as, MAX_LIFE, PersistentDataType.INTEGER, maxLife);
        setPDC(as, MONEY_REWARD, PersistentDataType.INTEGER, moneyReward);
        setPDC(as, XP_REWARD, PersistentDataType.DOUBLE, xpReward);
        setPDC(as, BARREL_ID, PersistentDataType.STRING, id);
        setPDC(as, TEXT_UUID, PersistentDataType.STRING, td.getUniqueId().toString());
        setPDC(as, BLOCK_UUID, PersistentDataType.STRING, bd.getUniqueId().toString());

        // --- Animazione ---
        AnimationManager.getInstance().aggiungiBarile(bd);

        // ✅ Ritorna tutti gli UUID
        return new BarrelData(as.getUniqueId(), bd.getUniqueId(), td.getUniqueId(), id);
    }

    // ---------- Helper PDC ----------

    public static <T, Z> Z getPDC(ArmorStand as, NamespacedKey key, PersistentDataType<T, Z> type) {
        return as.getPersistentDataContainer().get(key, type);
    }

    private static <T, Z> void setPDC(ArmorStand as, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        as.getPersistentDataContainer().set(key, type, value);
    }

    // ---------- Rimozione / Update ----------

    public void removeBlockDisplay(ArmorStand as) {
        String raw = getPDC(as, BLOCK_UUID, PersistentDataType.STRING);
        if (raw == null) {
            Logs.sendLog("BlockDisplay", "UUID mancante su ArmorStand " + as.getUniqueId());
            return;
        }
        Entity entity = Bukkit.getEntity(UUID.fromString(raw));
        if (entity instanceof BlockDisplay bd) {
            bd.remove();
        } else {
            Logs.sendLog("BlockDisplay -> " + raw, "Entità inesistente");
        }
    }

    public void updateTextDisplay(ArmorStand as, boolean remove) {
        String raw = getPDC(as, TEXT_UUID, PersistentDataType.STRING);
        if (raw == null)
            return;

        Entity entity = Bukkit.getEntity(UUID.fromString(raw));
        if (!(entity instanceof TextDisplay td)) {
            Logs.sendLog("TextDisplay -> " + raw, "Entità inesistente");
            return;
        }

        int life = getPDC(as, ACTUAL_LIFE, PersistentDataType.INTEGER);
        int max = getPDC(as, MAX_LIFE, PersistentDataType.INTEGER);
        double xp = getPDC(as, XP_REWARD, PersistentDataType.DOUBLE);
        int money = getPDC(as, MONEY_REWARD, PersistentDataType.INTEGER);

        td.setText(getBarrelName(life, max, xp, money));

        if (remove)
            td.remove();
    }

    // ---------- Hit ----------

    public void onBarrelHit(Player player, ArmorStand as) {
        int life = getPDC(as, ACTUAL_LIFE, PersistentDataType.INTEGER);
        life--;
        BarrelReward reward = new BarrelReward(getPDC(as, XP_REWARD, PersistentDataType.DOUBLE), getPDC(as, MONEY_REWARD, PersistentDataType.INTEGER));

        BarrelData barrelInfo = getBarrelResult(as);
        OnBarrelHitEvent hitEvent = new OnBarrelHitEvent(player, barrelInfo, reward, life);
        Bukkit.getServer().getPluginManager().callEvent(hitEvent);

        if (life <= 0) {
            OnBarrelDestroyEvent destroyEvent = new OnBarrelDestroyEvent(player, barrelInfo, reward);
            destroyBarrel(as, player);
            Bukkit.getServer().getPluginManager().callEvent(destroyEvent);
        } else {
            setPDC(as, ACTUAL_LIFE, PersistentDataType.INTEGER, life);
            updateTextDisplay(as, false);
        }
    }

    // ---------- Distruzione ----------

    /**
     * Distrugge un barrel dato l'UUID dell'ArmorStand.
     * 
     * @return true se il barrel è stato trovato e distrutto, false altrimenti
     */
    public boolean destroyBarrel(UUID armorStandUUID, Player player) {
        Entity entity = Bukkit.getEntity(armorStandUUID);
        if (!(entity instanceof ArmorStand as))
            return false;

        // Verifica che sia effettivamente un barrel (controllo PDC)
        String id = getPDC(as, BARREL_ID, PersistentDataType.STRING);
        if (id == null)
            return false;

        destroyBarrel(as, player);
        return true;
    }

    /**
     * Distrugge un barrel dato un BarrelData.
     */
    public boolean destroyBarrel(BarrelData result, Player player) {
        return destroyBarrel(result.armorStandUUID(), player);
    }

    /**
     * Logica effettiva di distruzione (già con l'ArmorStand in mano).
     */
    private void destroyBarrel(ArmorStand as, Player player) {
        // Effetti
        as.getWorld().playSound(as.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1f, 1f);

        // Pulizia entità
        removeBlockDisplay(as);
        updateTextDisplay(as, true);
        as.remove();
    }

    // ---------- Utility immutate (o quasi) ----------

    public BarrelData getBarrelResult(ArmorStand as) {
        UUID textUUID = UUID.fromString(getPDC(as, TEXT_UUID, PersistentDataType.STRING));
        UUID blockUUID = UUID.fromString(getPDC(as, BLOCK_UUID, PersistentDataType.STRING));
        String config_id = getPDC(as, BARREL_ID, PersistentDataType.STRING);

        return new BarrelData(as.getUniqueId(), blockUUID, textUUID, config_id);
    }

    private String getBarrelName(int life, int maxLife, double xp, int money) {
        StringBuilder sb = new StringBuilder();
        sb.append("&4&l").append("❤".repeat(life));
        sb.append("&7&l").append("❤".repeat(maxLife - life));
        sb.append("\n&a+").append(Utils.formatDouble(xp)).append("XP");
        sb.append("\n&a+").append(money).append(" Money");
        return Utils.colorize(sb.toString());
    }

    public List<ItemStack> getDrops(String id) {
        List<String> dropIds = config.getStringList("barrels." + id + ".drops");
        return dropIds.stream()
                .map(dropId -> ItemEdit.get().getServerStorage().getItem(dropId))
                .filter(Objects::nonNull)
                .map(ItemStack::clone)
                .toList();
    }

    private Location findAirAboveWaterOnAxisY(Location start) {
        if (start == null || start.getWorld() == null)
            return null;

        World world = start.getWorld();
        int bx = start.getBlockX();
        int bz = start.getBlockZ();

        for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(bx, y, bz);
            if (block.getType() == Material.WATER && block.getRelative(0, 1, 0).getType().isAir()) {
                return block.getRelative(0, 1, 0).getLocation();
            }
        }
        return null;
    }
}
