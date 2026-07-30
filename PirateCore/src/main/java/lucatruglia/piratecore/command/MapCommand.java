package lucatruglia.piratecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import lucatruglia.piratecore.managers.TreasureMapManager;
import lucatruglia.piratecore.managers.TreasureMapManager.Rarity;
import lucatruglia.piratecore.utils.Logs;

//kmap give 
public class MapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!((sender) instanceof Player)) {
            sender.sendMessage("Comando utilizzabile solo da player");
            return true;
        }

        Player player = (Player) sender;

        if (args.length <= 0) {
            Logs.sendWarningMessageToPlayer(player, "kMap", "Comando errato: /kmap give");
            return true;
        }

        if (args.length >= 2 || !args[0].equalsIgnoreCase("give")) {
            Logs.sendWarningMessageToPlayer(player, "kMap", "Comando errato: /kmap give");
            return true;
        }
        /* 
        MapView map = Bukkit.createMap(player.getWorld());
        // da controllare che il player sia effettivamente nel mondo spawn

        // 871 1434 spawn coord
        // 858 43 1301 tesoro

        map.setCenterX(858);
        map.setCenterZ(1301);

        map.setScale(MapView.Scale.FAR);

        map.setTrackingPosition(false);

        map.getRenderers().clear();

        map.addRenderer(new CustomMapRender());

        ItemStack map_item = new ItemStack(Material.FILLED_MAP);
        MapMeta map_meta = (MapMeta) map_item.getItemMeta();

        if (map_meta != null) {
            map_meta.setMapView(map);
            // Opzionale: dai un nome personalizzato alla mappa
            map_meta.setDisplayName("§aMappa di test");
            map_item.setItemMeta(map_meta);
        }

        player.getInventory().addItem(map_item);

        Logs.sendSuccessMessageToPlayer(player, "kMap", "Comando corretto");
        */

        TreasureMapManager.getInstance().giveMap(player, Rarity.EPIC);
        Logs.sendSuccessMessageToPlayer(player, "kMap", "Mappa givvata");

        return true;
    }

}
