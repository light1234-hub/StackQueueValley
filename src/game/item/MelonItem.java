package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.IngredientAbility;
import game.blueprint.Ingredient;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;
import game.blueprint.CoatingSource;
import game.effect.MelonCoating;
import game.recipe.AdjacentLocations;

import java.util.Random;

public class MelonItem extends Item implements Consumable, CoatingSource, Ingredient
{
    private static final double BURST_CHANCE = 0.5;
    private final Random random = new Random();

    public MelonItem(){
        super("Melon",'q');
        this.makePortable();
    }


    @Override
    public String consume(Actor actor)
    {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE,10);
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

    /**
     * Returns this Melonitem as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }

    /** Creates the coating supplied by this melon. */
    @Override
    public MelonCoating createCoating() {
        return new MelonCoating();
    }

    /** A melon is consumed when it is used as a coating source. */
    @Override
    public boolean isConsumedOnUse() {
        return true;
    }

    /**
     * A getter that returns the ingredient category of the Melon
     * @return the ingredient cateogry Fruit
     */
    public IngredientAbility category()
    {
        return IngredientAbility.FRUIT;
    }

    /**
     * Applies the effect of the Melon onto the dish
     * @param farmer
     * @param station
     * @return a string describing the result of using the melon as an ingredient
     */
    @Override
    public String useEffect(Actor farmer, Location station)
    {
        if (random.nextDouble() >= BURST_CHANCE)
        {
            return "The melon goes into the dish without bursting.";
        }

        for (Location adjacent : AdjacentLocations.from(station))
        {
            if (adjacent.containsAnActor())
            {
                adjacent.getActor().hurt(15);
            }
        }

        return "The melon bursts and damages all actors around the station," +
                " the melon still goes into the dish";
    }

}
