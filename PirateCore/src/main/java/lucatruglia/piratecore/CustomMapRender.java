package lucatruglia.piratecore;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.Color;

public class CustomMapRender extends MapRenderer {

    private boolean sfondoFatto = false;
    String text;

    public CustomMapRender(String text) {
        super(true);
        this.text = text;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player p) {
        // Sfondo blu — una volta sola
        if (!sfondoFatto) {
            for (int x = 0; x < 128; x++) {
                for (int z = 0; z < 128; z++) {
                    canvas.setPixelColor(x, z, Color.BLUE);
                }
            }
            sfondoFatto = true;
        }

        canvas.drawText(2, 2, MinecraftFont.Font, "§0;"+text);

        MapCursorCollection cursori = canvas.getCursors();
        while (cursori.size() > 0) {
            cursori.removeCursor(cursori.getCursor(0));
        }

        Location loc = p.getLocation();

        int deltaX = loc.getBlockX() - map.getCenterX();
        int deltaZ = loc.getBlockZ() - map.getCenterZ();

        int scala = 8;
        int mappaX = Math.max(-128, Math.min(127, deltaX / scala));
        int mappaZ = Math.max(-128, Math.min(127, deltaZ / scala));

        float yaw = ((loc.getYaw() % 360) + 360) % 360;
        byte direzione = (byte) ((int) ((yaw + 11.25) / 22.5) % 16);

        cursori.addCursor(new MapCursor((byte) 0, (byte) 0, (byte) 0,
                MapCursor.Type.RED_X, true));
        cursori.addCursor(new MapCursor((byte) mappaX, (byte) mappaZ,
                direzione, MapCursor.Type.PLAYER, true));
    }
}
