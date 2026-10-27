package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.NearbyLocationFinderBehaviour;
import game.weatherblueprint.WeatherRecoverable;

/**
 * Recovers resources from nearby weather-damaged terrain.
 * Processes at most one eligible tile per world turn.
 */
public class ResourceRecoveryResponse implements WeatherResponse{

    private static final int RADIUS = 4;

    /**
     * Searches for weather dmaged terrain and triggers its resource recover behaviour
     * @param location the centre of the response effects
     */
    @Override
    public void respond(Location location){
        for(Location target: NearbyLocationFinderBehaviour.find(location,RADIUS)){
            if(target.containsAnActor()){
                continue;
            }
            WeatherRecoverable recoverable = target.getActorAs(WeatherRecoverable.class);

            if(recoverable != null && recoverable.recover(target)){
                return;
            }
        }
    }
}
