package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

public interface Sellable
{
    int getSellPrice();

    String sold(Actor seller, Actor merchant, Location merchantLocation);
}
