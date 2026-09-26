package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Processable;

/**
 * Represents an Item that has been Dried using a DryingRack
 */
public class DriedProduct extends ProcessedProduct
{
    private static final String NAME_MODIFIER = "Dried";
    private static final int BONUS_STAM = 2;


    /**
     * A constructor that creates an object of the DriedProduct class
     * @param preProcessedProduct
     */
    public DriedProduct(Processable preProcessedProduct)
    {
        super(preProcessedProduct,NAME_MODIFIER);
    }

    /**
     * An abstract method that applies the effects of the drying
     * @param actor
     */
    @Override
    protected void applyOwnEffect(Actor actor)
    {
        actor.modifyStatistic(ActorStatistics.STAMINA,StatisticOperations.INCREASE,
                BONUS_STAM);
    }

    /**
     * An abstract method that create the effect description of the dried
     * @param actor
     * @return a description of the dried product
     */
    @Override
    protected String ownEffectDescrption(Actor actor)
    {
        return "The Dried effect grants the Farmer an extra " +
                BONUS_STAM + " Stamina";
    }
}
