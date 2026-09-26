package game.item;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.ConsumeAction;
import game.blueprint.Processable;

/**
 * An item produced by drying another processable item.
 */
public class DriedItem extends Item implements Processable {
    private Processable item;

    /**
     * Creates a dried version of an item.
     *
     * @param item item being dried
     */
    public DriedItem(Processable item) {
        super("Dried " + item.getItem().toString(), '&');
        this.makePortable();
        this.item = item;
    }

    /**
     * Consumes the dried item and restores stamina.
     *
     * @param actor actor consuming the item
     * @return a description of the consumption result
     */
    @Override
    public String consume(Actor actor) {

        String message = item.consume(actor);
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 5);
        actor.getInventory().remove(this);

        return message + " restored 5 SP!";
    }

    /**
     * Adds the action that allows the actor to consume this item.
     *
     * @param actor actor interacting with the item
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
     * Returns this dried item as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }
}
