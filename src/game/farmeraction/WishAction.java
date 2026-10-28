package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;

/**
 * Pays the well's fee (if any) and teleports the actor to the linked well's
 * map. Nothing is charged if the trip cannot happen.
 */
public class WishAction extends Action {
    private final Location destination;
    private final int cost;
    private final String direction;

    /**
     * @param destination where the actor ends up
     * @param cost coins charged for the wish (0 for a free return trip)
     * @param direction direction of the well from the actor (for the menu)
     */
    public WishAction(Location destination, int cost, String direction) {
        this.destination = destination;
        this.cost = cost;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (cost > 0 && (!actor.hasStatistic(FinanceStatistics.COINS)
                || actor.getStatistic(FinanceStatistics.COINS) < cost)) {
            return actor + " cannot afford the " + cost + " coin wish.";
        }
        if (destination.containsAnActor() || !destination.canActorEnter(actor)) {
            return "The wish fizzles out: the other side is occupied.";
        }
        if (cost > 0) {
            actor.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.DECREASE, cost);
        }
        map.moveActor(actor, destination);
        return actor + " makes a wish and is whisked away to " + destination.map() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        String price = cost > 0 ? " (" + cost + " coins)" : "";
        return actor + " makes a wish at the well at " + direction + price
                + " to travel to " + destination.map();
    }
}