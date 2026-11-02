package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.IngredientAbility;
import game.blueprint.AnimalFood;
import game.blueprint.Ingredient;
import game.blueprint.Processable;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;
import game.recipe.AdjacentLocations;
import game.terrain.PlantableGround;
import game.terrain.Plant;
import game.terrain.PlantGrowth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A harvested parsnip that can be consumed, fed to animals, or dried.
 */
public class ParsnipItem extends Item implements Consumable, AnimalFood, Processable, Ingredient {
    /**
     * Creates a portable parsnip.
     */
    public ParsnipItem(){
        super("Parsnip",'p');
        this.makePortable();

    }

    private final Random random = new Random();

    /**
     * Restores the actor's health and removes the parsnip from the inventory.
     *
     * @param actor actor consuming the parsnip
     * @return a description of the consumption result
     */
    @Override
    public String consume(Actor actor){
        actor.heal(1);
        return "Consumed Parsnip! Restore 1 HP";
    }

    /**
     * Applies the parsnip's feeding effect to an actor.
     *
     * @param actor actor receiving the food effect
     * @return a description of the effect
     */
    @Override
    public String foodEffect(Actor actor){
        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);
        return actor +" Max Stamina increased by 1";
    }

    /**
     * Returns this parsnip as an engine item.
     *
     * @return this item
     */
    @Override
    public Item getItem(){
        return this;
    }

    /**
     * Adds the action that allows the actor to consume this parsnip.
     *
     * @param actor actor interacting with the parsnip
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
     * A getter that returns the ingredient category of the Parsnip
     * @return the ingredient category Vegetable
     */
    @Override
    public IngredientAbility category()
    {
        return IngredientAbility.VEGETABLE;
    }

    /**
     * Applies the effect of adding a Parsnip into the dish
     * @param farmer
     * @param station
     * @return a string describing the result of using a Parsnip as an ingredient
     */
    @Override
    public String useEffect(Actor farmer, Location station)
    {
        List<Location> plantableLocations = new ArrayList<>();

        for (Location adjacent : AdjacentLocations.from(station))
        {
            if (adjacent.getGroundAs(PlantableGround.class) != null)
            {
                plantableLocations.add(adjacent);
            }
        }

        if (plantableLocations.isEmpty())
        {
            return "The parsnip is used, but there is no Soil in" +
                    " the area around the cooking station";
        }

        Location plantedLocation = plantableLocations.get(random.nextInt(plantableLocations.size()));
        plantedLocation.setGround(PlantGrowth.crop(PlantGrowth.Species.PARSNIP));

        return "A Parsnip Crop has been planted next to the Cooking Station";
    }
}
