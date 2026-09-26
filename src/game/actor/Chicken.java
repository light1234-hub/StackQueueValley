package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.item.Egg;

/**
 * Represents a Chicken on the Map
 */
public class Chicken extends Animal
{
    private static final int HEALTH_GAIN = 3;
    private static final double EXTRA_EGG = 0.5;

    /**
     * A constructor that creates an object of the Chicken class
     */
    public Chicken()
    {
        super("Chicken",'c',10);
    }

    /**
     * A method that carries out the feeding of the Chicken
     * Heals the chicken, drops the egg and handles the chance of another egg dropping
     * @param actor
     * @param location
     * @return a String describing the feeding of the Chicken by the Farmer
     */
    @Override
    public String feed(Actor actor, Location location)
    {
        this.heal(HEALTH_GAIN);

        dropItem(location, new Egg());

        if (RandomChance(EXTRA_EGG))
        {
            dropItem(location, new Egg());
        }

        return actor + " feeds " + this;
    }
}
