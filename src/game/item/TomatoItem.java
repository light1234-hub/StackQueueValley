package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.AnimalFood;
import game.blueprint.Processable;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;

/**
 * A harvested tomato that can be consumed, fed to animals, or dried.
 */
public class TomatoItem extends Item implements Consumable, AnimalFood, Processable {

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
     * Applies the tomato's feeding effect to an actor.
     *
     * @param actor actor receiving the food effect
     * @return a description of the effect
     */
    @Override
    public String foodEffect(Actor actor){
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE,2);
        actor.getInventory().remove(this);
        return actor +" Max HP increased by 2";
    }

    /**
     * Returns this tomato as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
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

