package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;
import game.blueprint.CoatingSource;
import game.effect.MelonCoating;

import java.util.Random;

public class MelonItem extends Item implements Consumable, CoatingSource, Sellable
{
    private static final int SALE_PRICE = 80;
    private static final double TIP_CHANCE = 0.5;
    private final Random random = new Random();

    public MelonItem(){
        super("Melon",'q');
        this.makePortable();
    }


    @Override
    public String consume(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE,10);
        return actor + " consumed the Melon! Maximum HP increased by 10.";
    }
    /**
     * Adds the action that allows the actor to consume this melon.
     *
     * @param actor actor interacting with the tomato
     * @param map current game map
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = super.allowableActions(actor, map);
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Returns this Melonitem as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }

    /** Creates the coating supplied by this melon. */
    @Override
    public MelonCoating createCoating() {
        return new MelonCoating();
    }

    /** A melon is consumed when it is used as a coating source. */
    @Override
    public boolean isConsumedOnUse() {
        return true;
    }

    /**
     * Processes the sale of a melon item to the merchant
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {

        if (random.nextDouble() > TIP_CHANCE)
        {
            seller.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE,
                    SALE_PRICE);
            return seller + " sells a Melon to the " + merchant + " for "
                    + SALE_PRICE + " coins.";
        }
        else
        {
            int tipSale = (int) Math.round(SALE_PRICE + SALE_PRICE * 0.05);
            seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,
                    tipSale);

            return seller + "sells a Melon to the " + merchant +
                    " and the merchant tips 5%, bringing the total to "
                    + tipSale + " coins.";
        }
    }

}
