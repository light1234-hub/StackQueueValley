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
import game.item.Milk;
import game.weatherblueprint.WeatherShelterable;

import java.util.List;
import java.util.Random;

/**
 * A cow that wanders around the farm and produces milk when fed.
 */
public class Cow extends Actor implements Feedable, WeatherShelterable {
    private Random random = new Random();
    private WanderBehaviour wanderBehaviour;
    /**
     * Creates a cow with its initial health and wandering behaviour.
     */
    public Cow(){
        super("Cow",'C',20);
        this.wanderBehaviour = new WanderBehaviour();
    }

    /**
     *  Produces a Milk  when it relocates to a safer place
     * @param destination the animal's new location
     */
    @Override
    public void onRelocated(Location destination){
        destination.addItem(new Milk());
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
     * Heals the cow and drops milk at a random adjacent location.
     *
     * @param actor actor feeding the cow
     * @param location cow's current location
     * @return a description of the feeding result
     */
    @Override
    public String feed(Actor actor, Location location){

        this.heal(5);

        List<Exit> exits = location.getExits();
        int randomidx = random.nextInt(exits.size());
        Location plot = exits.get(randomidx).getDestination();
        plot.addItem(new Milk());

        return "Cow heals 5HP and drops Milk";

    }

    /**
     * Adds feeding actions for animal food in the actor's inventory.
     *
     * @param actor actor interacting with the cow
     * @param direction direction from the actor to the cow
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
