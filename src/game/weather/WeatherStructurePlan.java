package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import game.recipe.CraftingMaterialType;
import game.recipe.ItemRequirement;
import game.recipe.RecipeMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a crafting plan for a weather structure.
 */
public class WeatherStructurePlan {
    private final String name;
    private final char kitSymbol;
    private final WeatherStructureType type;
    private final List<ItemRequirement> requirements;

    /**
     * Creates a weather structure crafting plan.
     *
     * @param name structure name
     * @param kitSymbol symbol of the crafted kit
     * @param type type of weather structure
     * @param stoneRequired amount of stone required
     * @param copperRequired amount of copper required
     */
    public WeatherStructurePlan(String name,char kitSymbol,WeatherStructureType type,int stoneRequired,int copperRequired) {
        if (stoneRequired < 0 || copperRequired < 0 || (stoneRequired == 0 && copperRequired == 0)) {
            throw new IllegalArgumentException(
                    "A weather structure must require valid materials.");
        }
        this.name = name;
        this.kitSymbol = kitSymbol;
        this.type = type;
        this.requirements = new ArrayList<>();

        if (stoneRequired > 0) {
            requirements.add(ItemRequirement.ofMaterial(CraftingMaterialType.STONE, stoneRequired, "Stone"));

        }
        if (copperRequired > 0) {
            requirements.add(ItemRequirement.ofMaterial(CraftingMaterialType.COPPER, copperRequired, "Copper"));
        }
    }
    public String displayName(){
        return name;
    }

    public char kitSymbol() {
        return kitSymbol;
    }

    /**
     * Creates the appropriate ground structure.
     * @return the newly created ground
     */
    public Ground createGround() {

        switch (type) {
            case DRAINAGE:
                return new DrainageStructure();

            case WILDLIFE:
                return new Shelter();

            case RECOVERY:
                return new ResourceRecoveryStructure();

            default:
                throw new IllegalStateException(
                        "Unknown weather structure type");
        }
    }

    /**
     * Selects items from inventory using shared recipe logic.
     * @param inventory actor inventory
     * @return matching items, if available
     */
    public Optional<List<Item>> selectItems(Inventory inventory) {
        return RecipeMaterials.select(inventory, requirements);
    }

    /**
     * Checks whether the actor has enough materials.
     * @param actor actor attempting to craft
     * @return true if the recipe can be crafted
     */
    public boolean canCraft(Actor actor) {
        return selectItems(actor.getInventory()).isPresent();
    }

    /**
     * Consumes the required materials.
     * @param actor actor crafting the structure
     * @return true if materials were consumed
     */
    public boolean consumeMaterials(Actor actor) {
        Optional<List<Item>> selected =
                selectItems(actor.getInventory());
        if (selected.isEmpty()) {
            return false;
        }
        return RecipeMaterials.consume(actor.getInventory(), selected.get());
    }

    /**
     * Returns a readable description of the recipe.
     * @return recipe requirements
     */
    public String recipeDescription() {
        List<String> parts = new ArrayList<>();
        for (ItemRequirement requirement : requirements) {
            parts.add(requirement.getCount() + " " + requirement.getDescription());
        }
        return String.join(", ", parts);
    }
}
