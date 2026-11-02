package game.dish;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import game.item.DishItem;

import java.util.ArrayList;


/**
 * A class that represents the Omelette Dish
 */
public class OmeletteDish extends DishItem
{
    /**
     * A constructor that creates an object of the Omelette Dish
     */
    public OmeletteDish()
    {
        super("Omelette",'e');
    }

    /**
     * Carries out the effect of consuming an Omelette
     * @param actor actor consuming the object
     * @return a string describing the result of the consumption
     */
    @Override
    public String consume(Actor actor)
    {
        for (Status status : new ArrayList<>(actor.statuses()))
        {
            actor.removeStatus(status);
        }

        return actor + " eats an Omelette and all status effects are removed.";
    }
}
