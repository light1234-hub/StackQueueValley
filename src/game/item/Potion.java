package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.blueprint.Consumable;
import game.farmeraction.ConsumeAction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A potion brewed in a cauldron. It stores its own recipe (ingredients, XP
 * requirement, brew time) and defines what happens when it is drunk.
 * <p>
 * Drinking reuses the existing {@link Consumable} / {@link ConsumeAction}
 * pair. Because {@link Consumable#consume(Actor)} does not receive a map or
 * location, the potion remembers the drinker's location every time the action
 * menu is built (see {@link #allowableActions(Actor, GameMap)}), which always
 * happens immediately before the drinker chooses an action.
 */
public abstract class Potion extends Item implements Consumable {

    private final Map<Class<? extends Item>, Integer> ingredients;
    private final int xpRequired;
    private final int brewTurns;
    private Location drinkerLocation;

    /**
     * @param name display name of the potion
     * @param displayChar character shown if the potion lies on the ground
     * @param ingredients required item types and how many of each
     * @param xpRequired XP threshold (checked, never spent)
     * @param brewTurns number of turns the cauldron needs to brew it
     */
    protected Potion(String name, char displayChar,
                     Map<Class<? extends Item>, Integer> ingredients,
                     int xpRequired, int brewTurns) {
        super(name, displayChar);
        this.ingredients = Collections.unmodifiableMap(new LinkedHashMap<>(ingredients));
        this.xpRequired = xpRequired;
        this.brewTurns = brewTurns;
        this.makePortable();
    }

    /**
     * What happens when the potion is drunk.
     *
     * @param drinker the actor drinking the potion
     * @param location the drinker's current location
     * @return a description of the effect
     */
    public abstract String drink(Actor drinker, Location location);

    /** @return the required ingredients (item type to quantity), unmodifiable */
    public Map<Class<? extends Item>, Integer> getIngredients() {
        return ingredients;
    }

    /** @return the XP threshold needed to brew this potion */
    public int getXpRequired() {
        return xpRequired;
    }

    /** @return the number of turns this potion takes to brew */
    public int getBrewTurns() {
        return brewTurns;
    }

    /**
     * Single place that decides which XP value is compared against the
     * threshold. It only reads the value and never spends it.
     * (Change to getAvailableExperience() here if your REQ3 rules prefer that.)
     *
     * @param actor the actor whose XP is read
     * @return the actor's XP, or 0 if the actor is not a Farmer
     */
    public static int currentXp(Actor actor) {
        if (actor instanceof Farmer) {
            return ((Farmer) actor).getTotalExperience();
        }
        return 0;
    }

    /**
     * @param type an ingredient item class, e.g. StrawberryItem.class
     * @return a readable name, e.g. "Strawberry"
     */
    public static String ingredientName(Class<? extends Item> type) {
        String simple = type.getSimpleName();
        return simple.endsWith("Item") ? simple.substring(0, simple.length() - 4) : simple;
    }

    /** @return the recipe as text, e.g. "3 x Strawberry, 1 x Tomato, 1 x Copper" */
    public String describeIngredients() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<Class<? extends Item>, Integer> entry : ingredients.entrySet()) {
            joiner.add(entry.getValue() + " x " + ingredientName(entry.getKey()));
        }
        return joiner.toString();
    }

    /** Offers the existing ConsumeAction while the potion is carried. */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        this.drinkerLocation = map.locationOf(owner);
        return new ActionList(new ConsumeAction(this));
    }

    @Override
    public String consume(Actor actor) {
        if (drinkerLocation == null) {
            return actor + " cannot work out where to drink the " + this + ".";
        }
        return drink(actor, drinkerLocation);
    }

    @Override
    public Item getItem() {
        return this;
    }
}