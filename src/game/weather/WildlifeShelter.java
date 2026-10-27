package game.weather;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Locale;

public class WildlifeShelter extends Ground {
   private final WeatherResponse response;

    /**
     * Creates a wildlife shelter with its default response
     */
    public WildlifeShelter(){
        super('H',"Wildlife Shelter");
        this.response = new WildlifeResponse();
    }

    @Override
    public void tick(Location location){
        response.respond(location);
    }
}
