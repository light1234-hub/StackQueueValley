package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

public interface Buyable
{
    int getBuyCost();

    String bought(Actor buyer, Actor merchant, Location merchantLocation);
}
