package game.item;

import edu.monash.fit2099.engine.items.Item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;
import game.recipe.CraftingMaterial;
import game.recipe.CraftingMaterialType;
import game.terrain.Rock;

import java.util.List;
import java.util.Random;

/**
 * A stone, dropped whenever a rock is broken.
 * can be sold or used as a crafting material.
 */
public class StoneItem extends Item implements Sellable, CraftingMaterial
{
    private static final int SALE_PRICE = 10;
    private static final int ROCK_RADIUS = 1;
    private final Random random = new Random();

    public StoneItem() {
        super("Stone", 'o');
        this.makePortable();
    }

    private void buildAdjacentRock(Location merchantLocation)
    {
        List<Location> adjacentLocations = merchantLocation.getNearbyLocations(ROCK_RADIUS);

        if (!adjacentLocations.isEmpty())
        {
            Location buildLocation = adjacentLocations.get(
                    random.nextInt(adjacentLocations.size()));

            buildLocation.setGround(new Rock());
        }
    }

    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,
                SALE_PRICE);

        buildAdjacentRock(merchantLocation);

        return seller + " sells a Stone to the " + merchant + " for "
                + SALE_PRICE + " coins.";
    }

    /**
     * @return return the item's crafting maetreial category
     */
    @Override
    public CraftingMaterialType materialType(){
        return CraftingMaterialType.STONE;
    }

}
