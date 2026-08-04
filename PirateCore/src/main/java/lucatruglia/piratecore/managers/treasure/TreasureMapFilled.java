package lucatruglia.piratecore.managers.treasure;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;

import lucatruglia.piratecore.utils.Utils;

public class TreasureMapFilled {
    private ItemStack item;
    private World world;
    private MapView mapView;
    private int coordX;
    private int coordZ;

    public ItemStack getItem() {
        return item;
    }

    public World getWorld() {
        return world;
    }

    public MapView getMapView() {
        return mapView;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordZ() {
        return coordZ;
    }

    public TreasureMapFilled(World world, Player playerOwner, int[] treasureLocation) {
        this.world = world;
        this.item = new ItemStack(Material.FILLED_MAP);
        this.mapView = Bukkit.createMap(world);
        this.coordX = treasureLocation[0];
        this.coordZ = treasureLocation[1];

        initView();
        initMeta();
    }

    private void initView() {
        mapView.setCenterX(coordX);
        mapView.setCenterZ(coordZ);
        mapView.setScale(MapView.Scale.FAR);
        mapView.setTrackingPosition(false);
        mapView.getRenderers().clear();
        mapView.addRenderer(new CustomMapRender(Utils.coordToString(coordX, coordZ)));
    }

    private void initMeta() {
        MapMeta map_meta = (MapMeta) this.item.getItemMeta();
        
        map_meta.getPersistentDataContainer().set(TreasureMapManager.filledMapKey, PersistentDataType.BOOLEAN, true);
        map_meta.setMapView(mapView);
        map_meta.setDisplayName("§aMappa del tesoro §e " + Utils.coordToString(coordX, coordZ));
        
        this.item.setItemMeta(map_meta);
    }
}
