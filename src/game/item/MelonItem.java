package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;

public class MelonItem extends Item implements Consumable{

    public MelonItem(){
        super("Melon",'q');
        this.makePortable();
    }


    @Override
    public String consume(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE,10);
        actor.getInventory().remove(this);
        return actor + " consumed the Melon! Maximum HP increased by 10.";
    }
    /**
     * Adds the action that allows the actor to consume this melon.
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
