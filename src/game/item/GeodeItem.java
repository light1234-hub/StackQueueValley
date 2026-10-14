package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.ToolAbility;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;
import game.farmeraction.BreakGeodeFromInventoryAction;


public class GeodeItem extends Item implements Sellable
{
    private static final int SALE_PRICE = 2500;

    public GeodeItem() {
        super("Geode", 'q');
        this.makePortable();
    }

    /**
     * Offers the option to break this geode while it's being carried.
     *
     * @param owner the actor carrying this geode
     * @param map the map the actor is currently on
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        if (owner.hasAbility(ToolAbility.MINE)) {
            actions.add(new BreakGeodeFromInventoryAction(this));
        }
        return actions;
    }

    /**
     * Processes the sale of a geode item to the merchant
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        int totalPayout = SALE_PRICE;
        String bonusMessage = "";

        if (Math.random() <= 0.25)
        {
            int diamondBonus = 5000;
            totalPayout += diamondBonus;
            bonusMessage = " " + merchant + " cracks it open and discovers a brilliant Diamond inside, paying a "
                    + diamondBonus + " coin bonus!";

            seller.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE, totalPayout);
            return seller + " sells a Geode to " + merchant + " for " + totalPayout + " coins!" + bonusMessage;
        } else
        {
            bonusMessage = " " + merchant + " opens the geode to find no diamond," +
                    "he is disappointed and takes back his money!";

            seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.DECREASE,SALE_PRICE);
            return seller + " gives the Geode to " + merchant + " and gets no money back!";

        }
    }
}