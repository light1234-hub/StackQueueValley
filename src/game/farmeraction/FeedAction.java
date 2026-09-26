package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Feedable;
import game.blueprint.Food;

/**
 * Represents the act of Feeding an animal food
 */
public class FeedAction extends Action
{
    private static final int STAM_DRAIN = 2;

    private final Feedable animal;
    private final Item foodItem;

    /**
     * A constructor that creates an object of the FeedAction class
     * @param animal
     * @param foodItem
     */
    public FeedAction(Feedable animal, Item foodItem)
    {
        this.animal = animal;
        this.foodItem = foodItem;
    }

    /**
     * A method describing the result of trying to feed the animal
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a String describing either a successful fed or an unsuccesful feed
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (actor.getStatistic(ActorStatistics.STAMINA) >= STAM_DRAIN)
        {
            actor.modifyStatistic(ActorStatistics.STAMINA,StatisticOperations.DECREASE,STAM_DRAIN);
            actor.getInventory().remove(foodItem);

            foodItem.asCapability(Food.class).ifPresent(food -> food.applyFarmerBonus(actor));

            Location farmerLocation = map.locationOf(actor);
            String feedResult = animal.feed(actor,farmerLocation);

            return actor + " feeds " + foodItem + " to a " + animal;
        }
        else
        {
            return "You are too tired to feed this animal!";
        }
    }

    /**
     * A method that returns the menu option of feeding a food item to an animal
     * @param actor The actor performing the action.
     * @return a String describing the option
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " feeds " + animal + " with a " + foodItem;
    }
}
