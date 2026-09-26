package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Food;
import game.blueprint.Processable;

/**
 * Represents a Tomato in the Farmer's Inventory
 */
public class TomatoItem extends ConsumableItem implements Food, Processable
{
    private static final int MAX_HEALTH_GAIN = 2;

    /**
     * A constructor that creates an object of the Tomato class
     */
    public TomatoItem()
    {
        super("Tomato", 'o');
    }

    /**
     * A method that applies the effect of consuming a raw Tomato including bonuses
     * @param actor
     */
    @Override
    public void applyEffect(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,MAX_HEALTH_GAIN);
    }

    /**
     * A method that describes the result and effects of consuming a raw Tomato
     * @param actor
     * @return a String describing the consumption of the Tomato
     */
    @Override
    public String consume(Actor actor)
    {
        return actor + " eats a Tomato, increasing their MAX Health by " + MAX_HEALTH_GAIN;
    }

    /**
     * A method that increases the Max Health of the Farmer by 2
     * after using a Tomato to feed an animal
     * @param actor
     */
    @Override
    public void applyFarmerBonus(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH,StatisticOperations.INCREASE,MAX_HEALTH_GAIN);
    }
}
