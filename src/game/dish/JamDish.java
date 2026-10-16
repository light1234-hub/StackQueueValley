package game.dish;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.item.DishItem;

/**
 * A class that represents the Jam Dish
 */
public class JamDish extends DishItem
{
    private final int fruitCount;

    /**
     * A constructor that creates an object of the Jam dish class
     * if the fruit count is valid
     * @param fruitCount
     */
    public JamDish(int fruitCount)
    {
        super("Jam",'J');

        if (fruitCount < 1)
        {
            throw new RuntimeException("Jam requires at least one fruit");
        }

        this.fruitCount = fruitCount;
    }

    /**
     * A getter that returns the number of fruits used in the making of the jam
     * @return the number of fruits
     */
    public int getFruitCount()
    {
        return fruitCount;
    }

    /**
     * Carries out the effect of consuming a Jam dish
     * @param actor actor consuming the object
     * @return a string describing the result of the consumption
     */
    @Override
    public String consume(Actor actor)
    {
        actor.modifyStatistic(ActorStatistics.STAMINA,StatisticOperations.INCREASE,fruitCount);

        return actor + " eats Jam and restores " + fruitCount + " stamina.";
    }
}
