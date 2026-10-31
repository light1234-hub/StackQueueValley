package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.ArrayList;
import java.util.List;

public abstract class NPC extends Actor {

    private final List<Behaviour<Actor, Action>> behaviours = new ArrayList<>();

    /**
     *  creates an NPC
     * @param name name of NPC
     * @param displayChar character of NPC that would be displayed
     * @param health NPC starting health
     */
    public NPC(String name, char displayChar, int health){
        super(name,displayChar,health);
    }

    /**
     *  Adding a behaviour to the NPC's list of behaviours
     *  The order of the behaviours determines their priority
     * @param behaviour to add
     */
    public void addBehaviour(Behaviour<Actor,Action> behaviour){
        behaviours.add(behaviour);
    }

    /**
     * Selects an action by evaluating the added behaviours.
     * The first behaviour returning a non-null action is selected. If
     * no behaviour then the NPC does nothing
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the selected action
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display){
        if(!isConscious()){
            unconscious(map);
            return new DoNothingAction();
        }

        for(Behaviour<Actor,Action> behaviour : behaviours){
            Action action = behaviour.operate(this,map.locationOf(this));
            if(action != null){
                return action;
            }
        }
        return new DoNothingAction();
    }
}
