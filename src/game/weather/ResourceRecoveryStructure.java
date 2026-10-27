package game.weather;


import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;


public class ResourceRecoveryStructure extends Ground {
    /**
     * A resource recovery structure that automatically
     * processes nearby weather-damaged terrain.
     */
    private final WeatherResponse response;

    /**
     * Creates a resource recovery structure
     */
    public ResourceRecoveryStructure(){
        super('R',"Resource Recovery Structure");
        this.response = new ResourceRecoveryResponse();
    }

    /**
     * Activates resource recovery every world turn.
     *
     * @param location location occupied by this structure
     */
    @Override
    public void tick(Location location){
        response.respond(location);
    }
}
