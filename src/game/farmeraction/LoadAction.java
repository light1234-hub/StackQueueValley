package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.BrewingStation;

/**
 * The farmer loads a cauldron with ingredients. All rule checking (XP,
 * ingredients, cauldron state) lives in {@link BrewingStation#load}.
 */
public class LoadAction extends Action {
    private final BrewingStation station;
    private final Location stationLocation;
    private final String direction;

    /**
     * @param station the cauldron to load
     * @param stationLocation where the cauldron is
     * @param direction direction of the cauldron from the actor (for the menu)
     */
    public LoadAction(BrewingStation station, Location stationLocation, String direction) {
        this.station = station;
        this.stationLocation = stationLocation;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return station.load(actor, stationLocation);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " loads the " + station + " at " + direction
                + " to brew " + station.getRecipe();
    }
}