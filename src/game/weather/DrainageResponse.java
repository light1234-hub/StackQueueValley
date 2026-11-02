package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.NearbyLocationFinderBehaviour;
import game.weatherblueprint.Drainable;

/**
 * Drains all the withing a 5 tile radius
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
        for(Location target : NearbyLocationFinderBehaviour.find(location,RADIUS)){
            Drainable drainable = target.getGroundAs(Drainable.class);
            if(drainable != null){
                drainable.drain(target);
            }
        }
    }
}
