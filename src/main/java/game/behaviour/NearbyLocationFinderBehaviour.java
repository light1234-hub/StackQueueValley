package game.behaviour;

import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Finding nearby map location
 */
public class NearbyLocationFinderBehaviour {

    private  NearbyLocationFinderBehaviour(){

    }

    public static List<Location> find(Location loc,int radius) {
        List<Location> nearby = new ArrayList<>();
        int minX = Math.max(loc.map().getXRange().min(), loc.x() - radius);
        int maxX = Math.min(loc.map().getXRange().max(), loc.x() + radius);

        int minY = Math.max(loc.map().getYRange().min(), loc.y() - radius);
        int maxY = Math.min(loc.map().getYRange().max(), loc.y() + radius);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int distance = Math.abs(loc.x() - x) + Math.abs(loc.y() - y);
                if (distance > 0 && distance <= radius) {
                    nearby.add(loc.map().at(x, y));
                }
            }
        }
        return nearby;
    }
}
