package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.CollectAction;
import game.farmeraction.LoadAction;
import game.item.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A cauldron that brews one potion over several turns.
 * <p>
 * It holds the brew timer, the READY state and the load/collect logic.
 * Each concrete cauldron supplies only its in-brew effect through
 * {@link #applyBrewEffect(Location)}, plus optional start/collect hooks.
 * Like a door, it is impassable furniture: the farmer uses it from a
 * neighbouring tile.
 */
public abstract class BrewingStation extends Ground {

    /** Lifecycle of a cauldron. A cauldron can only be loaded when IDLE. */
    public enum BrewState { IDLE, BREWING, READY }

    private final Supplier<? extends Potion> potionFactory;
    private final Potion recipe;
    private BrewState state = BrewState.IDLE;
    private int turnsRemaining;

    /**
     * @param displayChar character shown on the map
     * @param name display name of the cauldron
     * @param potionFactory creates a fresh potion of the type this cauldron brews
     */
    protected BrewingStation(char displayChar, String name, Supplier<? extends Potion> potionFactory) {
        super(displayChar, name);
        this.potionFactory = potionFactory;
        this.recipe = potionFactory.get();
    }

    /** Applied once on every turn of the brew, including the final one. */
    protected abstract void applyBrewEffect(Location location);

    /** Hook: called right after the farmer loads the cauldron. */
    protected void onBrewStarted(Location location) { }

    /** Hook: called right after the potion is collected (clean-up / extra output goes here). */
    protected void onCollected(Actor collector, Location location) { }

    /** @return a prototype of the potion this cauldron brews (for recipe info) */
    public Potion getRecipe() {
        return recipe;
    }

    public BrewState getState() {
        return state;
    }

    public boolean isIdle() {
        return state == BrewState.IDLE;
    }

    public boolean isBrewing() {
        return state == BrewState.BREWING;
    }

    public boolean isReady() {
        return state == BrewState.READY;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Validates XP and ingredients, consumes the ingredients and starts the brew.
     *
     * @param actor the farmer loading the cauldron
     * @param location this cauldron's location
     * @return what happened
     */
    public String load(Actor actor, Location location) {
        if (state == BrewState.BREWING) {
            return "The " + this + " is already brewing (" + turnsRemaining + " turn(s) left).";
        }
        if (state == BrewState.READY) {
            return "The " + this + " holds a finished potion. Collect it before loading again.";
        }

        int xp = Potion.currentXp(actor);
        if (xp < recipe.getXpRequired()) {
            return actor + " needs " + (recipe.getXpRequired() - xp)
                    + " more XP to brew " + recipe + ".";
        }

        List<Item> toConsume = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (Map.Entry<Class<? extends Item>, Integer> need : recipe.getIngredients().entrySet()) {
            int found = 0;
            for (Item item : actor.getInventory().getItems()) {
                if (found < need.getValue() && item.getClass() == need.getKey()) {
                    toConsume.add(item);
                    found++;
                }
            }
            if (found < need.getValue()) {
                missing.add((need.getValue() - found) + " x " + Potion.ingredientName(need.getKey()));
            }
        }
        if (!missing.isEmpty()) {
            return actor + " is missing: " + String.join(", ", missing) + ".";
        }

        for (Item item : toConsume) {
            actor.getInventory().remove(item);
        }
        state = BrewState.BREWING;
        turnsRemaining = recipe.getBrewTurns();
        onBrewStarted(location);
        return actor + " loads the " + this + " and starts brewing " + recipe
                + " (" + turnsRemaining + " turns).";
    }

    /**
     * Hands the finished potion to the actor and resets the cauldron.
     *
     * @param actor the farmer collecting
     * @param location this cauldron's location
     * @return what happened
     */
    public String collect(Actor actor, Location location) {
        if (state != BrewState.READY) {
            return "There is no finished potion in the " + this + ".";
        }
        Potion potion = potionFactory.get();
        actor.getInventory().add(potion);
        state = BrewState.IDLE;
        turnsRemaining = 0;
        onCollected(actor, location);
        return actor + " collects " + potion + " from the " + this + ".";
    }

    /** Counts the brew down and applies the in-brew effect each turn. */
    @Override
    public void tick(Location location) {
        if (state != BrewState.BREWING) {
            return;
        }
        applyBrewEffect(location);
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            state = BrewState.READY;
        }
    }

    /** Cauldrons are furniture; the farmer interacts from an adjacent tile. */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /** Offers LoadAction when idle and CollectAction when ready. */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (state == BrewState.IDLE) {
            actions.add(new LoadAction(this, location, direction));
        } else if (state == BrewState.READY) {
            actions.add(new CollectAction(this, location, direction));
        }
        return actions;
    }
}