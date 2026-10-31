package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.BuyAction;
import game.farmeraction.SellAction;
import game.behaviour.WanderBehaviour;
import game.item.Sword;
import game.products.TomatoCropProduct;
import game.products.ParsnipCropProduct;
import game.products.SwordProduct;
import game.blueprint.Sellable;
import game.blueprint.Buyable;
import game.weatherblueprint.WeatherShelterable;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the merchant that wanders around the map that can buy and sell items to the farmer
 */
public class Merchant extends PassiveCreature implements WeatherShelterable
{
    private final List<Buyable> productsForSale = new ArrayList<>();

    /**
     * A constructor that creates an object of the merchant with an inventory of sellable items
     */
    public Merchant()
    {
        super("Merchant", '&', 100);
        productsForSale.add(new ParsnipCropProduct());
        productsForSale.add(new SwordProduct());
        productsForSale.add(new TomatoCropProduct());
    }

    /**
     * Returns a list of actions that the farmer can perform on/too the merchant
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
            item.asCapability(Sellable.class).ifPresent(sellable ->
                    actions.add(new SellAction(this,item,sellable)));
        }

        for (Buyable product : productsForSale)
        {
            actions.add(new BuyAction(this,product));
        }

        return actions;
    }

    @Override
    public void onRelocated(Location destination)
    {
        Sword freeReward = new Sword();
        destination.addItem(freeReward);
        System.out.println(this + " found refuge in the shelter and left a token of their appreciation on the ground: " + freeReward);

    }
}
