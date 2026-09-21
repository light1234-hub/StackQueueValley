package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;

/**
 * A harvested tomato that can be consumed, fed to animals, or dried.
 */
public class TomatoItem extends Item implements Consumable {

    /**
     * Creates a portable tomato.
     */
    public TomatoItem(){
        super("Tomato",'o');
        this.makePortable();
    }

    /**
     * Increases the actor's maximum health and removes the tomato.
     *
     * @param actor actor consuming the tomato
     * @return a description of the consumption result
     */
    @Override
    public String consume(Actor actor){
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE,2);
        actor.getInventory().remove(this);

        return "Consumed tomato! Max HP increase by 2";
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

}

