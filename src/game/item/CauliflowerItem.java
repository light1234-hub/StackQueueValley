package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;

public class CauliflowerItem extends Item implements Consumable
{

    public CauliflowerItem(){
        super("Cauliflower",'y');
        this.makePortable();
    }

    @Override
    public String consume(Actor actor)
    {
        actor.heal(25);

        if(Math.random()>=0.5){
            return actor+ " heals 25 HP, and the Cauliflower miraculously survives!";
        }
        actor.getInventory().remove(this);
        return actor +" heals 25 HP and fully consumes the Cauliflower.";
    }
    /**
     * Adds the action that allows the actor to consume this cauliflower.
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
