package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Feedable;
import game.behaviours.WanderBehaviour;
import game.farmeraction.FeedAction;
import game.blueprint.Food;


import java.util.List;
import java.util.Random;

/**
 * An abstract class representing an Animal that wanders through the Map, can be fed by the Farmer
 */
public abstract class Animal extends Actor implements Feedable
{
    private static final int ADJACENT_RADIUS = 1;

    private final WanderBehaviour wandering = new WanderBehaviour();

    protected final Random random;

    /**
     * A Constructor with 3 inputs, generates a random value by itself
     * Creates an object of the Animal class
     * @param name
     * @param displayChar
     * @param health
     */
    public Animal(String name, char displayChar, int health)
    {
        this(name,displayChar,health,new Random());
    }

    /**
     * A Constructor with 4 inputs
     * Creates an object of the Animal class
     * @param name
     * @param displayChar
     * @param health
     * @param random
     */
    public Animal(String name, char displayChar, int health, Random random)
    {
        super(name,displayChar,health);
        this.random = random;
    }

    /**
     * A method that carrioues of the wandering of the Animal each turn
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action performed by the Animal, either not moving at all
     * or moving to an adjacent spot
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display)
    {
        if (!this.isConscious())
        {
            this.unconscious(map);
            return new DoNothingAction();
        }
        else
        {
            Action wanderAction = wandering.operate(this,map.locationOf(this));
            return wanderAction != null ? wanderAction : new DoNothingAction();
        }
    }

    /**
     * A method that identifies all the feed actions for a specific animal
     * @param otherActor the Actor that might be performing attack
     * @param direction String representing the direction of the other Actor
     * @param map current GameMap
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map)
    {
        ActionList actions = new ActionList();
        for (Item item : otherActor.getInventory().getItems())
        {
            if (item.asCapability(Food.class).isPresent())
            {
                actions.add(new FeedAction(this,item));
            }
        }

        return actions;
    }

    /**
     * A method that handles the probability of something happening
     * @param chance
     * @return true or false
     */
    public boolean RandomChance(double chance)
    {
        return random.nextDouble() < chance;
    }

    /**
     * A method that represents the animal dropping an item onto the ground
     * @param location
     * @param item
     */
    public void dropItem(Location location, Item item)
    {
        List<Location> adjacentLocations = location.getNearbyLocations(ADJACENT_RADIUS);

        if (!adjacentLocations.isEmpty())
        {
            Location dropLocation = adjacentLocations.get(random.nextInt(adjacentLocations.size()));
            dropLocation.addItem(item);
        }
    }
}
