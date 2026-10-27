package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.weatherblueprint.Drainable;

/**
 * Drains at most one flooded tile per turn
 */
public class DrainageResponse implements WeatherResponse{
    private static final int RADIUS = 5;

    /**
     * Searches for the first drainable tile;
     * starting from the top row
     * @param location
     */
    @Override
    public void respond(Location location){
        int minX = Math.max(location.map().getXRange().min(), location.x() - RADIUS);
        int maxX = Math.max(location.map().getXRange().max(), location.x() + RADIUS);

        int minY = Math.max(location.map().getYRange().min(), location.y() - RADIUS);
        int maxY = Math.max(location.map().getYRange().max(), location.y() + RADIUS);

        for( int y = minY; y<= maxY; y++){
            for(int x = minX; x<= maxX; x++){
                int distance = Math.abs(location.x() - x)+Math.abs(location.y() - y);

                if(distance == 0 || distance > RADIUS){
                    continue;
                }
                Location target = location.map().at(x,y);

                if(target.containsAnActor()){
                    continue;
                }

                Drainable drainable = target.getGroundAs(Drainable.class);

                if(drainable != null && drainable.drain(target)){
                    return;
                }
            }
        }
    }
}
