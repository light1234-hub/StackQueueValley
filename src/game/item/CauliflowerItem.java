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

import java.util.Random;

public class CauliflowerItem extends Item implements Consumable, Sellable
{
    private static final int SALE_PRICE = 50;
    private static final double TIP_CHANCE = 0.2;
    private final Random random = new Random();

    public CauliflowerItem(){
        super("Cauliflower",'y');
        this.makePortable();
    }

    @Override
    public String consume(Actor actor)
    {
        actor.heal(25);

        if(Math.random()>=0.5){
            actor.getInventory().add(new CauliflowerItem());
            return actor+ " heals 25 HP, and the Cauliflower miraculously survives!";
        }
        return actor +" heals 25 HP and fully consumes the Cauliflower.";
    }
    /**
     * Adds the action that allows the actor to consume this cauliflower.
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
     * Returns this Caulifloweritem as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }


    /**
     * Processes the sale of a Cauliflower item to the merchant
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
            return seller + " sells a Cauliflower to the " + merchant + " for "
                    + SALE_PRICE + " coins.";
        }
        else
        {
            int tipSale = (int) Math.round(SALE_PRICE + SALE_PRICE * 0.1);
            seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,
                    tipSale);

            return seller + "sells a Cauliflower to the " + merchant +
                    " and the merchant tips 10%, bringing the total to "
                    + tipSale + " coins.";
        }
    }

}
