package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Food;
import game.blueprint.Processable;

/**
 * Represents a Parsnip in the Farmer's inventory
 */
public class ParsnipItem extends ConsumableItem implements Food, Processable
{
    private static final int HEALTH_GAIN = 1;
    private static final int STAM_BONUS = 1;
    /**
     * A Constructor that creates an object of the Parsnip class
     */
    public ParsnipItem()
    {
        super("Parsnip",'p');
    }

    /**
     * A method that applies the effect of consuming a raw parsnip
     * @param actor
     */
    @Override
    public void applyEffect(Actor actor)
    {
        actor.heal(HEALTH_GAIN);
    }

    /**
     * A method that describes the result and effects of consuming a raw Parsnip
     * @param actor
     * @return a String describing the consumption of the Parsnip
     */
    @Override
    public String consume(Actor actor)
    {
        return actor + " eats a Parsnip and gained " + HEALTH_GAIN + " HP";
    }

    /**
     * A method that increases the Max Stamina of the Farmer by 1
     * after using a Parsnip to feed an animal
     * @param actor
     */
    @Override
    public void applyFarmerBonus(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,STAM_BONUS);
    }
}
