package game.farmeraction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.AnimalFood;
import game.blueprint.Feedable;

/**
 * Action that feeds an animal using food from the actor's inventory.
 */
public class FeedAction extends Action {
    private final Feedable animal;
    private final AnimalFood food;
    private final String direction;

    /**
     * Creates a feeding action.
     *
     * @param animal animal to feed
     * @param food food used for feeding
     * @param direction direction to the animal
     */
    public FeedAction(Feedable animal,AnimalFood food,String direction){
        this.animal=animal;
        this.food=food;
        this.direction=direction;
    }

    /**
     * Performs the feeding operation and deducts stamina.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the feeding result
     */
    @Override
    public String execute(Actor actor, GameMap map){
        if(actor.getStatistic(ActorStatistics.STAMINA)<2){
            return "You are too tired to feed this animal.";
        }
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE,2);

        String feedBonus = food.foodEffect(actor);
        String animalFeeding = animal.feed(actor,map.locationOf(actor));

        actor.getInventory().remove(food.getItem());

        return actor + " fed the animal. " + feedBonus + " " + animalFeeding;
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " feeds the animal at " + direction + " with " + food.toString();
    }
}
