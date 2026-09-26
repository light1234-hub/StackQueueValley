package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Processable;
import game.terrain.ProcessingFacility;


/**
 * Represents an action that processes an Item into a Processed Item
 */
public class ProcessAction extends Action
{
    private final ProcessingFacility facility;
    private final Item product;
    private final Processable processableProduct;

    /**
     * A constructor that creates an object of the ProcessAction class
     * @param facility
     * @param product
     * @param processableProduct
     */
    public ProcessAction(ProcessingFacility facility, Item product, Processable processableProduct)
    {
        this.facility = facility;
        this.processableProduct = processableProduct;
        this.product = product;
    }


    /**
     * A method that carries out the action of processing an Item
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a String describing the processing of an item
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        Item processedProduct = facility.process(processableProduct);

        actor.getInventory().remove(product);
        actor.getInventory().add(processedProduct);

        return actor + " processes " + product + " into "
                + processedProduct;
    }

    /**
     * A method that describes the act of processing an Item
     * @param actor The actor performing the action.
     * @return a String representing the processing of said item in the menu
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " processes " + product + " using the " + facility;
    }
}
