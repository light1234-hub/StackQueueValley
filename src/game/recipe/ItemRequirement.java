package game.recipe;

import edu.monash.fit2099.engine.items.Item;

import java.util.function.Predicate;

/**
 * Represents a req for a specified quantity of items
 * supports both cooking ingredient and crafting materials
 */
public class ItemRequirement {

    private final Predicate<Item> matches;
    private final int count;
    private final String description;

    /**
     *  Constructor for item requirement
     * @param matches condtion used to identify matching items
     * @param count quantity required
     * @param description name of the materials
     * @throws  IllegalArgumentException if the maching condition or description is null or quantity is not positive
     */
    public ItemRequirement(Predicate<Item> matches, int count, String description){

        if(matches == null || description == null){
            throw new IllegalArgumentException("Requirement fields cannot be null");

        }

        if (count <= 0){
            throw new IllegalArgumentException("Requirement quantity must be positive");
        }

        this.matches = matches;
        this.description = description;
        this.count = count;
    }

    /**
     * Creates a requirement for a crafting material
     * @param type material category
     * @param count quantity required
     * @param name material name
     * @return item requirement
     */
    public static ItemRequirement ofMaterial(CraftingMaterialType type, int count, String name){
        return new ItemRequirement( item -> item.asCapability(CraftingMaterial.class).map(material -> material.materialType() == type).orElse(false),count,name);
    }

    /**
     * checks whether item satisfy the req
     * @param item item to check
     * @return true if the item matches
     */
    public boolean matches( Item item){
        return matches.test(item);
    }
    public int getCount(){
        return count;
    }
    public String getDescription(){
        return description;
    }
}
