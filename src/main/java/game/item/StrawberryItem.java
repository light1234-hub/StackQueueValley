package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;
import game.effect.HealingEffect;
import game.effect.PoisonEffect;

public class StrawberryItem extends Item implements Consumable, Sellable
{
    private static final int SALE_PRICE = 150;
    private boolean isRotten;
    /**
     * Creates a Strawberry item and determines if it is rotten.
     */
    public StrawberryItem()
    {
        this(Math.random() <= 0.5);
    }

    /** Creates a strawberry with the requested freshness. */
    public StrawberryItem(boolean rotten)
    {
        super("Strawberry",'s');
        this.makePortable();

        this.isRotten = rotten;
    }

    /**
     * Checks if this strawberry is rotten.
     *
     * @return true if rotten, false otherwise
     */
    public boolean isRotten() {
        return this.isRotten;
    }

    @Override
    public String consume(Actor actor){
        if(isRotten()){
            actor.addStatus(new PoisonEffect(actor));
            return "A rotten strawberry! You have been poisoned.";
        }else{
            actor.addStatus(new HealingEffect(actor));
            return "A fresh strawberry! You feel a regenerative healing effect.";
        }
    }

    /**
     * Adds the action that allows the actor to consume this tomato.
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
     * Returns this Straberryitem as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }

    /**
     * Processes the sale of a strawberry item to the merchant
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation) {
        if (isRotten())
        {
            int penaltyPrice = SALE_PRICE / 2;
            seller.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE, penaltyPrice);
            return seller + " sold a rotten Strawberry to " + merchant + " for a reduced price of "
                    + penaltyPrice + " coins! " + merchant + " looks disgusted.";
        }
        else
        {
            seller.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE, SALE_PRICE);
            return seller + " sells a fresh Strawberry to " + merchant + " for " + SALE_PRICE + " coins.";
        }
    }

}
