package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.item.Milk;

/**
 * Represents a Cow on the Map
 */
public class Cow extends Animal
{
    private static final int HEALTH_GAIN = 5;

    /**
     * A constructor that creates an object of the Cow class
     */
    public Cow()
    {
        super("Cow", 'C', 20);
    }

    /**
     * A method that carries out the feeding of the Cow
     * Heals the Cow and drops the Milk
     * @param actor
     * @param location
     * @return a String describing the feeding of the Cow by the Farmer
     */
    @Override
    public String feed(Actor actor, Location location)
    {
        this.heal(HEALTH_GAIN);
        dropItem(location,new Milk());

        return actor + " feeds " + this;
    }
}
