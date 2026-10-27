package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.NearbyLocationFinderBehaviour;
import game.weatherblueprint.WeatherHazard;
import game.weatherblueprint.WeatherShelterable;

/**
 * Protects nearby livestock from Tornado
 * by relocating them to safe locations.
 */
public class WildlifeResponse implements WeatherResponse{

    private static final int RADIUS = 3;

    /**
     * Searches for endangered livestock and
     * attempts to relocate one animal.
     *
     * @param location centre of the wildlife response
     */
    @Override
    public void respond(Location location){
        for(Location source: NearbyLocationFinderBehaviour.find(location,RADIUS)) {
            WeatherShelterable livestock = source.getActorAs(WeatherShelterable.class);

            if (livestock == null || !isDangerous(source)) {
                continue;
            }

            Actor animal = source.getActor();

            for (Location destination : NearbyLocationFinderBehaviour.find(location, RADIUS)) {
                if (!isDangerous(destination) && destination.canActorEnter(animal)) {
                    location.map().moveActor(animal, destination);
                    livestock.onRelocated(destination);
                    return;
                }
            }
        }
    }
    private boolean isDangerous(Location location){
        WeatherHazard hazard = location.getGroundAs(WeatherHazard.class);
        return hazard != null && hazard.isDangerous();
    }
}
