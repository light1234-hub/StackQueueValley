package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;

/** Pays the well's cost and teleports the actor to the linked destination. */
public class WishAction extends Action {
    private final Location destination;
    private final int cost;
    private final String direction;

    public WishAction(Location destination, int cost, String direction) {
        this.destination = destination;
        this.cost = cost;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getStatistic(FinanceStatistics.COINS) < cost) {
            return actor + " cannot afford the " + cost + " coin wish.";
        }
        if (destination.containsAnActor() || !destination.canActorEnter(actor)) {
            return "The arrival spot is blocked. The wish fizzles.";
        }
        actor.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.DECREASE, cost);
        destination.map().moveActor(actor, destination);
        return actor + " makes a wish and is whisked away.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " makes a wish at the well (" + cost + " coins) " + direction;
    }
}