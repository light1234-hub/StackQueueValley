package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Buyable;

/**
 * Represents a action that buys an item from the merchant
 */
public class BuyAction extends Action
{
    private final Actor merchant;
    private final Buyable product;

    /**
     * A constructor that creates an object of the BuyAction
     * @param merchant
     * @param product
     */
    public BuyAction(Actor merchant, Buyable product)
    {
        this.merchant = merchant;
        this.product = product;
    }

    /**
     * Buys the selected item from the merchant, verifies that the player has enough coins to
     * make the purchase and deducts the price from the players coin balance after purchasing
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a string describing the result of the purchase
     */
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

    /**
     * A string describing how the menu description would look like
     * @param actor The actor performing the action.
     * @return a description of the buy action for the menu
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " buys " + product + " from the " + merchant
                + " for " + product.getBuyCost() + " coins.";
    }
}
