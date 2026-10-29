package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.farmeraction.ConsumeAction;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for brewable potions. A potion is a portable Item that reuses the
 * existing Consumable / ConsumeAction pair; the brewing recipe is stored here so
 * cauldrons and the Wizard can read it.
 */
public abstract class Potion extends Item implements Consumable {
    private final Map<Class<? extends Item>, Integer> ingredients;
    private final int xpRequired;
    private final int brewTurns;
    private Location drinkerLocation;

    protected Potion(String name, char displayChar, Map<Class<? extends Item>, Integer> ingredients,
                     int xpRequired, int brewTurns) {
        super(name, displayChar);
        this.ingredients = ingredients;
        this.xpRequired = xpRequired;
        this.brewTurns = brewTurns;
        makePortable();
    }

    /** Applies the potion's effect. @param location where the drinker is standing. */
    public abstract String drink(Actor drinker, Location location);

    public Map<Class<? extends Item>, Integer> getIngredients() {
        return ingredients;
    }

    public int getXpRequired() {
        return xpRequired;
    }

    public int getBrewTurns() {
        return brewTurns;
    }

    /**
     * XP is only a threshold: it is read here and never spent. Only the farmer can execute
     * the load / wizard actions that call this, so the cast is safe.
     */
    public static int currentXp(Actor farmer) {
        return ((Farmer) farmer).getTotalExperience();
    }

    public static String ingredientName(Class<? extends Item> type) {
        return type.getSimpleName().replace("Item", "");
    }

    public String describeIngredients() {
        return ingredients.entrySet().stream()
                .map(e -> e.getValue() + " x " + ingredientName(e.getKey()))
                .collect(Collectors.joining(", "));
    }

    /** Remembers where the owner is, because Consumable.consume(Actor) gets no map. */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        drinkerLocation = map.locationOf(owner);
        return new ActionList(new ConsumeAction(this));
    }

    @Override
    public String consume(Actor actor) {
        return drink(actor, drinkerLocation);
    }

    @Override
    public Item getItem() {
        return this;
    }
}