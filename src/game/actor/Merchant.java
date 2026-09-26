package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.farmeraction.BuyAction;
import game.farmeraction.SellAction;
import game.behaviour.WanderBehaviour;
import game.products.TomatoCropProduct;
import game.products.ParsnipCropProduct;
import game.products.SwordProduct;
import game.blueprint.Sellable;
import game.blueprint.Buyable;

import java.util.List;
import java.util.ArrayList;

public class Merchant extends Actor
{
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private final List<Buyable> productsForSale = new ArrayList<>();

    public Merchant()
    {
        super("Merchant", '&', 100);
        productsForSale.add(new ParsnipCropProduct());
        productsForSale.add(new SwordProduct());
        productsForSale.add(new TomatoCropProduct());
    }

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
            Action wanderAction = wanderBehaviour.operate(this,map.locationOf(this));

            return wanderAction != null ? wanderAction : new DoNothingAction();
        }
    }

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
}
