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
 * A harvested parsnip that can be consumed, fed to animals, or dried.
 */
public class ParsnipItem extends Item implements Consumable, AnimalFood, Processable {
    /**
     * Creates a portable parsnip.
     */
    public ParsnipItem(){
        super("Parsnip",'p');
        this.makePortable();

    }

    /**
     * Restores the actor's health and removes the parsnip from the inventory.
     *
     * @param actor actor consuming the parsnip
     * @return a description of the consumption result
     */
    @Override
    public String consume(Actor actor){
        actor.heal(1);
        actor.getInventory().remove(this);

        return "Consumed Parsnip! Restore 1 HP";
    }

    /**
     * Applies the parsnip's feeding effect to an actor.
     *
     * @param actor actor receiving the food effect
     * @return a description of the effect
     */
    @Override
    public String foodEffect(Actor actor){
        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);
        actor.getInventory().remove(this);
        return actor +" Max Stamina increased by 1";
    }

    /**
     * Returns this parsnip as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }

    /**
     * Adds the action that allows the actor to consume this parsnip.
     *
     * @param actor actor interacting with the parsnip
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