package lucatruglia.piratecore.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import emanondev.itemedit.ItemEdit;
import lucatruglia.piratecore.PirateCore;
import lucatruglia.piratecore.models.ListMessage;
import lucatruglia.piratecore.utils.Logs;
import lucatruglia.piratecore.utils.Utils;

public class BarrelManager {
    private static BarrelManager instance;

    private FileConfiguration config;

    public static NamespacedKey actualLifeKey;
    public static NamespacedKey maxLifeKey;
    public static NamespacedKey xpRewardKey;
    public static NamespacedKey moneyRewardKey;
    public static NamespacedKey idKey;
    public static NamespacedKey textUUIDKey;
    public static NamespacedKey blockUUIDKey;

    public static BarrelManager getInstance() {
        if (instance == null) {
            instance = new BarrelManager();
        }
        return instance;
    }

    public void initialize() {
        actualLifeKey = new NamespacedKey(PirateCore.get(), "life");
        maxLifeKey = new NamespacedKey(PirateCore.get(), "maxlife");
        xpRewardKey = new NamespacedKey(PirateCore.get(), "xpreward");
        moneyRewardKey = new NamespacedKey(PirateCore.get(), "moneyreward");
        idKey = new NamespacedKey(PirateCore.get(), "id");

        textUUIDKey = new NamespacedKey(PirateCore.get(), "textUUID");
        blockUUIDKey = new NamespacedKey(PirateCore.get(), "blockUUID");

        config = ConfigManager.getInstance().getConfig("settings/barrels.yml");

        instance = this;
    }

    public String getBarrelName(int life, int maxlife, double xp, int money) {
        StringBuilder result = new StringBuilder();

        // result.append("&a&lXP: &r&a+").append(xp).append("\n");
        // result.append("&e&lMONEY: &r&e+").append(money).append("\n\n"); // doppio a
        // capo per staccare i cuori

        // result.append("&4&l");
        for (int i = 0; i < life; i++) {
            result.append("&4&l❤");
        }

        for (int i = 0; i < (maxlife - life); i++) {
            result.append("&7&l❤");
        }


        result.append("\n&a+" + Utils.formatDouble(xp) + "XP");
        result.append("\n&a+" + money + " Money");

        return Utils.colorize(result.toString());
    }

    private List<ItemStack> getDrops(String ID) {
        List<String> drops_id = (List<String>) config.getList("barrels." + ID + ".drops");
        List<ItemStack> items = new ArrayList<>();
        for (String string : drops_id) {

            ItemStack tempItem = ItemEdit.get().getServerStorage().getItem(string).clone();

            if (tempItem != null) {
                items.add(tempItem);
            }
        }
        return items;
    }

    public boolean spawnBarrel(Location loc, String ID) {
        int life = config.getInt("barrels." + ID + ".life");
        double xpReward = config.getDouble("barrels." + ID + ".xp_reward");
        int moneyReward = config.getInt("barrels." + ID + ".money_reward");

        return spawnBarrel(loc, life, xpReward, moneyReward, ID);
    }

    public boolean spawnBarrel(Location loc, int maxLife, double xpReward, int moneyReward, String id) {

        Location onAirLocation = findAirAboveWaterOnAxisY(loc);

        if (onAirLocation == null) {
            return false;
        }

        loc = onAirLocation.clone();

        Location armorStandLocation = new Location(
                loc.getWorld(),
                loc.getBlockX() + 0.5,
                loc.getBlockY() - 1,
                loc.getBlockZ() + 0.5);

        Location displayLocation = new Location(
                loc.getWorld(),
                loc.getBlockX(),
                loc.getBlockY() - 0.5,
                loc.getBlockZ());

        TextDisplay td = loc.getWorld().spawn(displayLocation.clone().add(0.5, 1.5, 0.5), TextDisplay.class);
        td.setText(getBarrelName(maxLife, maxLife, xpReward, moneyReward));
        td.setBillboard(Display.Billboard.CENTER);

        ArmorStand as = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation,
                EntityType.ARMOR_STAND);

        as.setSmall(false);
        as.setInvulnerable(false);
        as.setInvisible(true);
        as.setGravity(false);

        as.getPersistentDataContainer().set(actualLifeKey, PersistentDataType.INTEGER, maxLife);
        as.getPersistentDataContainer().set(maxLifeKey, PersistentDataType.INTEGER, maxLife);
        as.getPersistentDataContainer().set(moneyRewardKey, PersistentDataType.INTEGER, moneyReward);
        as.getPersistentDataContainer().set(xpRewardKey, PersistentDataType.DOUBLE, xpReward);
        as.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);

        // as.setCustomName(getBarrelName(maxLife, maxLife, 50, 0));

        as.setCustomNameVisible(false);

        BlockDisplay bd = loc.getWorld().spawn(displayLocation, BlockDisplay.class);
        bd.setBlock(Material.BARREL.createBlockData());

        // TEXTDISPLAY: td; ARMORSTAND: as; BLOCKDISPLAY: bd;

        as.getPersistentDataContainer().set(textUUIDKey, PersistentDataType.STRING, "" + td.getUniqueId());
        as.getPersistentDataContainer().set(blockUUIDKey, PersistentDataType.STRING, "" + bd.getUniqueId());

        AnimationManager.getInstance().aggiungiBarile(bd);

        return true;
    }

    public void removeBlockDisplay(ArmorStand armorStand) {

        String blockUUID = armorStand.getPersistentDataContainer().get(blockUUIDKey, PersistentDataType.STRING);
        BlockDisplay display = (BlockDisplay) Bukkit.getEntity(UUID.fromString(blockUUID));

        if (display == null) {
            Logs.sendLog("BlockDisplay -> " + blockUUID, "Entità inesistente");
            return;
        }

        display.remove();

    }

    public void updateTextDisplay(ArmorStand armorStand, boolean remove) {

        String textUUID = armorStand.getPersistentDataContainer().get(textUUIDKey, PersistentDataType.STRING);
        TextDisplay display = (TextDisplay) Bukkit.getEntity(UUID.fromString(textUUID));
        
        if (display == null) {
            Logs.sendLog("TextDisplay -> " + textUUID, "Entità inesistente");
            return;
        }

        int colpiRimasti = armorStand.getPersistentDataContainer().get(BarrelManager.actualLifeKey,
                PersistentDataType.INTEGER);
        double xpReward = armorStand.getPersistentDataContainer().get(BarrelManager.xpRewardKey,
                PersistentDataType.DOUBLE);
        int moneyReward = armorStand.getPersistentDataContainer().get(BarrelManager.moneyRewardKey,
                PersistentDataType.INTEGER);
        int maxLife = armorStand.getPersistentDataContainer().get(BarrelManager.maxLifeKey, PersistentDataType.INTEGER);

        display.setText(getBarrelName(colpiRimasti, maxLife, xpReward, moneyReward));

        if (remove) {
            display.remove();
        }

    }

    public Location findAirAboveWaterOnAxisY(Location startLoc) {
        if (startLoc == null || startLoc.getWorld() == null) {
            return null;
        }

        var world = startLoc.getWorld();
        int blockX = startLoc.getBlockX();
        int blockZ = startLoc.getBlockZ();

        int maxHeight = world.getMaxHeight(); // Solitamente 320 da 1.18+
        int minHeight = world.getMinHeight(); // Solitamente -64 da 1.18+

        for (int y = maxHeight - 1; y >= minHeight; y--) {
            Block currentBlock = world.getBlockAt(blockX, y, blockZ);

            // Se troviamo l'acqua...
            if (currentBlock.getType() == Material.WATER) {
                Block aboveBlock = currentBlock.getRelative(0, 1, 0);

                if (aboveBlock.getType().isAir()) {
                    return aboveBlock.getLocation();
                }
            }
        }

        return null;
    }

    public void onBarrelHit(Player player, ArmorStand as) {
        // Recuperiamo i colpi rimasti
        int colpiRimasti = as.getPersistentDataContainer().get(BarrelManager.actualLifeKey, PersistentDataType.INTEGER);
        double xpReward = as.getPersistentDataContainer().get(BarrelManager.xpRewardKey, PersistentDataType.DOUBLE);
        int moneyReward = as.getPersistentDataContainer().get(BarrelManager.moneyRewardKey, PersistentDataType.INTEGER);
        //int maxLife = as.getPersistentDataContainer().get(BarrelManager.maxLifeKey, PersistentDataType.INTEGER);
        String id = as.getPersistentDataContainer().get(BarrelManager.idKey, PersistentDataType.STRING);

        // Riduciamo i colpi di 1
        colpiRimasti--;

        if (colpiRimasti <= 0) {

            List<ItemStack> drops = getDrops(id);

            if (!drops.isEmpty()) {
                for (ItemStack item : drops) {
                    PlayerManager.getInstance().dropItem(player, item);
                }
            }

            Logs.sendListMessageToPlayer(player, new ListMessage("Barrel distrutto", List.of(
                    new ListMessage.Row("XP", "+" + xpReward),
                    new ListMessage.Row("Dobloni", "+" + moneyReward + "$"))));

            PlayerManager.getInstance().addXP(player, xpReward, false);
            PlayerManager.getInstance().addMoney(player, (double) moneyReward, false);

            as.getWorld().playSound(as.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1.0f, 1.0f);
            removeBlockDisplay(as);
            updateTextDisplay(as, true);
            as.remove();
        } else {
            // Altrimenti aggiorniamo i dati e il nome visibile
            // as.getWorld().playSound(as.getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 1.0f,
            // 1.0f);
            as.getPersistentDataContainer().set(BarrelManager.actualLifeKey, PersistentDataType.INTEGER, colpiRimasti);
            updateTextDisplay(as, false);
        }

        /*
         * Logs.sendListMessageToPlayer(player,
         * 
         * new ListMessage("Stato barrel", List.of(
         * new ListMessage.Row("Life", ""+colpiRimasti),
         * new ListMessage.Row("MaxLife", ""+maxLife),
         * new ListMessage.Row("XP Reward", ""+xpReward),
         * new ListMessage.Row("Money Reward", ""+moneyReward)
         * ))
         * );
         */
    }
}
