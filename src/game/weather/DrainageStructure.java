package game.weather;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public class DrainageStructure extends Ground {

    private final WeatherResponse response;

    public DrainageStructure(){
        super('D',"Drainage Structure");
        this.response = new DrainageResponse();
    }

    @Override
    public void tick(Location location){
        response.respond(location);
    }
}
