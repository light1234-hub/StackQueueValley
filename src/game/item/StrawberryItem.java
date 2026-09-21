package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;
import game.effect.HealingEffect;
import game.effect.PoisonEffect;

public class StrawberryItem extends Item implements Consumable {
    private boolean isRotten;
    /**
     * Creates a Strawberry item and determines if it is rotten.
     */
    public StrawberryItem(){
        super("Strawberry",'s');
        this.makePortable();

        this.isRotten = Math.random() <= 0.5;
    }

    /**
     * Checks if this strawberry is rotten.
     *
     * @return true if rotten, false otherwise
     */
    public boolean isRotten() {
        return this.isRotten;
    }

    @Override
    public String consume(Actor actor){
        if(isRotten()){
            actor.getInventory().add(new PoisonEffect());
            actor.getInventory().remove(this);
            return "A rotten strawberry! You have been poisoned.";
        }else{
            actor.getInventory().add(new HealingEffect());
            actor.getInventory().remove(this);
            return "A fresh strawberry! You feel a regenerative healing effect.";
        }

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
