package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Buyable;

public class BuyAction extends Action
{
    private final Actor merchant;
    private final Buyable product;

    public BuyAction(Actor merchant, Buyable product)
    {
        this.merchant = merchant;
        this.product = product;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (actor.getStatistic(FinanceStatistics.COINS) < product.getBuyCost())
        {
            return actor + " cannot afford to purchase " + product + ".";
        }
        else
        {
            actor.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.DECREASE
                    , product.getBuyCost());

            return product.bought(actor,merchant,map.locationOf(merchant));
        }
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " buys " + product + " from the " + merchant
                + " for " + product.getBuyCost() + " coins.";
    }
}
