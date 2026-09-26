package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.WanderBehaviour;
import game.farmeraction.FeedAction;
import game.blueprint.AnimalFood;
import game.blueprint.Feedable;
import game.item.Egg;


import java.util.List;
import java.util.Random;

/**
 * A chicken that wanders around the farm and produces an egg when fed.
 */
public class Chicken extends Actor implements Feedable {
    private Random random = new Random();
    private WanderBehaviour wanderBehaviour;
    /**
     * Creates a chicken with its initial health and wandering behaviour.
     */
    public Chicken(){
        super("Chicken",'c',10);
        this.wanderBehaviour = new WanderBehaviour();
    }

    /**
     * Performs a wandering action when possible.
     *
     * @param actions available actions
     * @param lastAction actor's previous action
     * @param map current game map
     * @param display current display
     * @return the selected movement or waiting action
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display){
        Action wanderAction = wanderBehaviour.operate(this,map.locationOf(this));

        if (wanderAction != null){
            return wanderAction;
        }

        return new DoNothingAction();

    }

    /**
     * Heals the chicken and drops an egg at a random adjacent location.
     *
     * @param actor actor feeding the chicken
     * @param location chicken's current location
     * @return a description of the feeding result
     */
    @Override
    public String feed(Actor actor, Location location){

        this.heal(3);

        List<Exit> exits = location.getExits();
        int randomidx = random.nextInt(exits.size());
        Location plot = exits.get(randomidx).getDestination();
        plot.addItem(new Egg());

        // 50% chance to drop a second egg
        if (random.nextBoolean()) {
            int randomidx2 = random.nextInt(exits.size());
            Location plot2 = exits.get(randomidx2).getDestination();
            plot2.addItem(new Egg());
            return "Chicken heals 3HP and drop two Egg";
        }

        return "Chicken heals 3HP and drops Egg";

    }

    /**
     * Adds feeding actions for animal food in the actor's inventory.
     *
     * @param actor actor interacting with the chicken
     * @param direction direction from the actor to the chicken
     * @param map current game map
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(actor, direction, map);

        for(AnimalFood food: actor.getInventory().getItemsAs(AnimalFood.class)){
            actions.add(new FeedAction(this,food,direction));
        }
        return actions;
    }

}
