package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.BrewingStation;

/** Loads a cauldron with the ingredients of its recipe and starts the brew. */
public class LoadAction extends Action {
    private final BrewingStation station;
    private final Location location;
    private final String direction;

    public LoadAction(BrewingStation station, Location location, String direction) {
        this.station = station;
        this.location = location;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return station.load(actor, location);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " loads " + station + " with " + station.getRecipe() + " ingredients ("
                + station.getRecipe().describeIngredients() + ", XP " + station.getRecipe().getXpRequired()
                + ") " + direction;
    }
}