package game.dish;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.item.DishItem;

/**
 * A class that represents the Harvest Feast dish
 */
public class HarvestFeast extends DishItem
{
    /**
     * A constructor that creates an object of the Harvest Feast
     */
    public HarvestFeast()
    {
        super("Harvest Feast", 'H');
    }

    /**
     * Carries out the effect of consuming a Harvest Feast
     * @param actor actor consuming the object
     * @return a string describing the result of the consumption
     */
    @Override
    public String consume(Actor actor)
    {
        actor.modifyStatistic(ActorStatistics.HEALTH,StatisticOperations.INCREASE,1);
        actor.modifyStatistic(ActorStatistics.STAMINA,StatisticOperations.INCREASE,1);

        return actor + " eats a Harvest Feast. Maximum HP and Stamina has been increased by 1";
    }
}
