package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.BrewingStation;

/** Collects the finished potion from a cauldron. */
public class CollectAction extends Action {
    private final BrewingStation station;
    private final Location location;
    private final String direction;

    public CollectAction(BrewingStation station, Location location, String direction) {
        this.station = station;
        this.location = location;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return station.collect(actor, location);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " collects " + station.getRecipe() + " from " + station + " " + direction;
    }
}