package game.req2tests;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.IngredientAbility;
import game.actor.Farmer;
import game.blueprint.Ingredient;
import game.dish.HarvestFeast;
import game.dish.JamDish;
import game.dish.OmeletteDish;
import game.item.MelonItem;
import game.item.Egg;
import game.item.ParsnipItem;
import game.farmeraction.ConsumeAction;
import game.farmeraction.CookAction;
import game.recipe.*;
import game.terrain.CookingStation;
import game.terrain.Dirt;
import game.terrain.Soil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ2 regression tests for ingredient selection, the cooking station,
 * the three recipes and consumption of the resulting dishes.
 *
 * Each multi-case test uses at least three values or scenarios. No test
 * requires a live API, sleeping, or probabilistic success: controlled
 * ingredients remove the random effects of real Egg/Melon items.
 */
public class Req2CookingTest {

    /** Minimal deterministic ingredient with no side effects beyond a call counter. */
    private static final class TestIngredient extends Item implements Ingredient {
        private final IngredientAbility category;
        private int uses;

        TestIngredient(IngredientAbility category) {
            super("Controlled " + category, 'i');
            this.category = category;
            makePortable();
        }

        @Override
        public IngredientAbility category() {
            return category;
        }

        @Override
        public String useEffect(Actor actor, Location station) {
            uses++;
            return "Controlled ingredient used";
        }

        @Override
        public Item getItem() {
            return this;
        }

        int useCount() {
            return uses;
        }
    }

    /** Non-ingredient to check that recipe selection ignores unrelated items. */
    private static final class NonIngredient extends Item {
        NonIngredient() {
            super("Not food", 'n');
            makePortable();
        }
    }

    /** Deterministic status used when testing omelette status removal. */
    private static final class TestStatus implements Status {
        @Override
        public boolean isStatusActive() {
            return true;
        }
    }

    /** Small, independent gameplay fixture; the Farmer's weather turn is not invoked. */
    private static final class Kitchen {
        final GameMap map;
        final Farmer farmer;
        final Location station;

        Kitchen(int stationX, int stationY, int farmerX, int farmerY)
                throws GameEngineException {
            DefaultGroundCreator creator = new DefaultGroundCreator();
            creator.registerGround('.', Dirt::new);
            map = new GameMap("REQ2 isolated test kitchen", creator, '.', 5, 5);
            // The engine initializes shared actor locations when a map joins a World.
            World world = new World(new Display()) {};
            world.addGameMap(map);
            station = map.at(stationX, stationY);
            station.setGround(new CookingStation(List.of(
                    new JamRecipe(), new OmeletteRecipe(), new HarvestFeastRecipe())));
            farmer = new Farmer("REQ2 Test", null);
            map.addActor(farmer, map.at(farmerX, farmerY));
        }

        List<TestIngredient> add(IngredientAbility category, int count) {
            List<TestIngredient> added = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                TestIngredient ingredient = new TestIngredient(category);
                farmer.getInventory().add(ingredient);
                added.add(ingredient);
            }
            return added;
        }
    }

    private static void add(Inventory inventory, IngredientAbility category, int count) {
        for (int i = 0; i < count; i++) {
            inventory.add(new TestIngredient(category));
        }
    }

    private static int countCategory(Inventory inventory, IngredientAbility category) {
        int count = 0;
        for (Ingredient ingredient : inventory.getItemsAs(Ingredient.class)) {
            if (ingredient.category() == category) {
                count++;
            }
        }
        return count;
    }

    private static int countDish(Inventory inventory, Class<? extends Item> dishType) {
        int count = 0;
        for (Item item : inventory.getItems()) {
            if (item.getClass() == dishType) {
                count++;
            }
        }
        return count;
    }

    @Test
    void jamSelectsEveryFruitForOneTwoAndFiveFruits() {
        for (int fruitCount : new int[]{1, 2, 5}) {
            Inventory inventory = new Inventory();
            add(inventory, IngredientAbility.FRUIT, fruitCount);
            add(inventory, IngredientAbility.VEGETABLE, 2);
            add(inventory, IngredientAbility.ANIMAL_PRODUCT, 1);
            inventory.add(new NonIngredient());

            Optional<List<Ingredient>> found = new JamRecipe().selectIngredients(inventory);
            assertTrue(found.isPresent());
            assertEquals(fruitCount, found.get().size());
            for (Ingredient item : found.get()) {
                assertEquals(IngredientAbility.FRUIT, item.category());
            }
            assertEquals(fruitCount + 4, inventory.getItems().size(),
                    "Selection must not consume items");
        }
    }

    @Test
    void jamRejectsThreeInventoriesWithoutFruit() {
        for (int variant = 0; variant < 3; variant++) {
            Inventory inventory = new Inventory();
            if (variant >= 1) add(inventory, IngredientAbility.VEGETABLE, 1);
            if (variant >= 2) add(inventory, IngredientAbility.ANIMAL_PRODUCT, 1);
            assertTrue(new JamRecipe().selectIngredients(inventory).isEmpty());
        }
    }

    @Test
    void omeletteSelectsExactlyOneAnimalAndOneVegetableDespiteExtras() {
        for (int extra : new int[]{0, 1, 3}) {
            Inventory inventory = new Inventory();
            add(inventory, IngredientAbility.ANIMAL_PRODUCT, 1 + extra);
            add(inventory, IngredientAbility.VEGETABLE, 1 + extra);
            add(inventory, IngredientAbility.FRUIT, 2);

            List<Ingredient> selected = new OmeletteRecipe()
                    .selectIngredients(inventory).orElseThrow();
            assertEquals(2, selected.size());
            assertEquals(IngredientAbility.ANIMAL_PRODUCT, selected.get(0).category());
            assertEquals(IngredientAbility.VEGETABLE, selected.get(1).category());
            assertTrue(selected.get(0).getItem() != selected.get(1).getItem());
            assertEquals(4 + 2 * extra, inventory.getItems().size());
        }
    }

    @Test
    void omeletteRejectsEmptyAnimalOnlyAndVegetableOnlyCases() {
        for (int variant = 0; variant < 3; variant++) {
            Inventory inventory = new Inventory();
            if (variant == 1) add(inventory, IngredientAbility.ANIMAL_PRODUCT, 2);
            if (variant == 2) add(inventory, IngredientAbility.VEGETABLE, 2);
            assertTrue(new OmeletteRecipe().selectIngredients(inventory).isEmpty());
        }
    }

    @Test
    void harvestFeastConsumesExactlyFivePerCategoryForFiveSixAndNineAvailable() {
        for (int available : new int[]{5, 6, 9}) {
            Inventory inventory = new Inventory();
            add(inventory, IngredientAbility.FRUIT, available);
            add(inventory, IngredientAbility.ANIMAL_PRODUCT, available);
            add(inventory, IngredientAbility.VEGETABLE, available);

            List<Ingredient> selected = new HarvestFeastRecipe()
                    .selectIngredients(inventory).orElseThrow();
            assertEquals(15, selected.size());
            assertEquals(3 * available, inventory.getItems().size(),
                    "Ingredient selection must be side-effect free");
        }
    }

    @Test
    void harvestFeastRejectsShortageOfEachOfTheThreeCategories() {
        for (IngredientAbility missing : new IngredientAbility[]{
                IngredientAbility.FRUIT,
                IngredientAbility.ANIMAL_PRODUCT,
                IngredientAbility.VEGETABLE}) {
            Inventory inventory = new Inventory();
            for (IngredientAbility category : new IngredientAbility[]{
                    IngredientAbility.FRUIT,
                    IngredientAbility.ANIMAL_PRODUCT,
                    IngredientAbility.VEGETABLE}) {
                add(inventory, category, category == missing ? 4 : 5);
            }
            assertTrue(new HarvestFeastRecipe().selectIngredients(inventory).isEmpty());
            assertEquals(14, inventory.getItems().size());
        }
    }

    @Test
    void invalidIngredientRequirementsRejectZeroNegativeAndNullCategory() {
        assertThrows(RuntimeException.class,
                () -> new IngredientRequirement(IngredientAbility.FRUIT, 0));
        assertThrows(RuntimeException.class,
                () -> new IngredientRequirement(IngredientAbility.FRUIT, -3));
        assertThrows(RuntimeException.class,
                () -> new IngredientRequirement(null, 1));
    }

    @Test
    void sharedMaterialSelectorSelectsDistinctItemsWithoutConsumingForThreeAmounts() {
        for (int amount : new int[]{1, 2, 3}) {
            Inventory inventory = new Inventory();
            add(inventory, IngredientAbility.FRUIT, amount);
            List<ItemRequirement> requirements = List.of(
                    new ItemRequirement(item -> item.asCapability(Ingredient.class)
                            .map(i -> i.category() == IngredientAbility.FRUIT)
                            .orElse(false), amount, "Fruit"));

            List<Item> chosen = RecipeMaterials.select(inventory, requirements).orElseThrow();
            assertEquals(amount, chosen.size());
            assertEquals(amount, inventory.getItems().size());
            assertTrue(RecipeMaterials.consume(inventory, chosen));
            assertTrue(inventory.getItems().isEmpty());
        }
    }

    @Test
    void sharedMaterialSelectorRejectsThreeInsufficientInventoriesWithoutMutation() {
        for (int available : new int[]{0, 1, 2}) {
            Inventory inventory = new Inventory();
            add(inventory, IngredientAbility.FRUIT, available);
            List<ItemRequirement> requirements = List.of(new ItemRequirement(
                    item -> item.asCapability(Ingredient.class)
                            .map(i -> i.category() == IngredientAbility.FRUIT)
                            .orElse(false), 3, "Fruit"));
            assertTrue(RecipeMaterials.select(inventory, requirements).isEmpty());
            assertEquals(available, inventory.getItems().size());
        }
    }

    @Test
    void stationOffersThreeRecipeActionsOnlyWhenAdjacentAndBlocksEntry()
            throws GameEngineException {
        Kitchen kitchen = new Kitchen(2, 2, 2, 1);
        CookingStation ground = (CookingStation) kitchen.station.getGround();
        ActionList adjacent = ground.allowableActions(kitchen.farmer, kitchen.station, "South");
        assertEquals(3, adjacent.size());
        assertFalse(ground.canActorEnter(kitchen.farmer));
        assertEquals(0, ground.allowableActions(kitchen.farmer, kitchen.station, "").size());
        for (String name : new String[]{"Jam", "Omelette", "Harvest Feast"}) {
            boolean found = false;
            for (Action action : adjacent) {
                if (action.menuDescription(kitchen.farmer).contains(name)) found = true;
            }
            assertTrue(found, "Missing menu action for " + name);
        }
    }

    @Test
    void cookingAllThreeRecipesWithoutIngredientsDoesNotCreateDishes()
            throws GameEngineException {
        for (Recipe recipe : List.of(new JamRecipe(), new OmeletteRecipe(), new HarvestFeastRecipe())) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            int initial = kitchen.farmer.getInventory().getItems().size();
            String message = new CookAction(recipe, kitchen.station).execute(kitchen.farmer, kitchen.map);
            assertTrue(message.contains("aren't enough ingredients"));
            assertEquals(initial, kitchen.farmer.getInventory().getItems().size());
        }
    }

    @Test
    void cookingAllThreeRecipesRejectsDistantOrReplacedStations()
            throws GameEngineException {
        for (Recipe recipe : List.of(new JamRecipe(), new OmeletteRecipe(), new HarvestFeastRecipe())) {
            Kitchen kitchen = new Kitchen(2, 2, 0, 0);
            kitchen.add(IngredientAbility.FRUIT, 5);
            kitchen.add(IngredientAbility.ANIMAL_PRODUCT, 5);
            kitchen.add(IngredientAbility.VEGETABLE, 5);
            int before = kitchen.farmer.getInventory().getItems().size();
            CookAction action = new CookAction(recipe, kitchen.station);

            String distant = action.execute(kitchen.farmer, kitchen.map);
            assertTrue(distant.contains("must be beside"));
            assertEquals(before, kitchen.farmer.getInventory().getItems().size());

            kitchen.map.moveActor(kitchen.farmer, kitchen.map.at(2, 1));
            kitchen.station.setGround(new Dirt());
            String stale = action.execute(kitchen.farmer, kitchen.map);
            assertTrue(stale.contains("unavailable"));
            assertEquals(before, kitchen.farmer.getInventory().getItems().size());
        }
    }

    @Test
    void cookingJamConsumesAllFruitButNotVegetablesAndMakesJam()
            throws GameEngineException {
        for (int fruit : new int[]{1, 2, 4}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            kitchen.add(IngredientAbility.FRUIT, fruit);
            kitchen.add(IngredientAbility.VEGETABLE, 2);
            String message = new CookAction(new JamRecipe(), kitchen.station)
                    .execute(kitchen.farmer, kitchen.map);
            assertTrue(message.contains("Jam is ready"));
            assertEquals(0, countCategory(kitchen.farmer.getInventory(), IngredientAbility.FRUIT));
            assertEquals(2, countCategory(kitchen.farmer.getInventory(), IngredientAbility.VEGETABLE));
            assertEquals(1, countDish(kitchen.farmer.getInventory(), JamDish.class));
            for (Item item : kitchen.farmer.getInventory().getItems()) {
                if (item.getClass() == JamDish.class) {
                    assertEquals(fruit, ((JamDish) item).getFruitCount());
                }
            }
            for (Location adjacent : AdjacentLocations.from(kitchen.station)) {
                assertEquals(Soil.class, adjacent.getGround().getClass());
            }
        }
    }

    @Test
    void cookingOmeletteConsumesOneOfEachButPreservesExtras()
            throws GameEngineException {
        for (int excess : new int[]{0, 1, 3}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            kitchen.add(IngredientAbility.ANIMAL_PRODUCT, 1 + excess);
            kitchen.add(IngredientAbility.VEGETABLE, 1 + excess);
            kitchen.add(IngredientAbility.FRUIT, 1);
            String message = new CookAction(new OmeletteRecipe(), kitchen.station)
                    .execute(kitchen.farmer, kitchen.map);
            assertTrue(message.contains("Omelette is ready"));
            assertEquals(excess, countCategory(kitchen.farmer.getInventory(), IngredientAbility.ANIMAL_PRODUCT));
            assertEquals(excess, countCategory(kitchen.farmer.getInventory(), IngredientAbility.VEGETABLE));
            assertEquals(1, countCategory(kitchen.farmer.getInventory(), IngredientAbility.FRUIT));
            assertEquals(1, countDish(kitchen.farmer.getInventory(), OmeletteDish.class));
        }
    }

    @Test
    void cookingFeastConsumesFifteenIngredientsAndCreatesTwoDishes()
            throws GameEngineException {
        for (int excess : new int[]{0, 1, 2}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            kitchen.add(IngredientAbility.FRUIT, 5 + excess);
            kitchen.add(IngredientAbility.ANIMAL_PRODUCT, 5 + excess);
            kitchen.add(IngredientAbility.VEGETABLE, 5 + excess);
            String message = new CookAction(new HarvestFeastRecipe(), kitchen.station)
                    .execute(kitchen.farmer, kitchen.map);
            assertTrue(message.contains("Harvest Feast is ready"));
            assertEquals(excess, countCategory(kitchen.farmer.getInventory(), IngredientAbility.FRUIT));
            assertEquals(excess, countCategory(kitchen.farmer.getInventory(), IngredientAbility.ANIMAL_PRODUCT));
            assertEquals(excess, countCategory(kitchen.farmer.getInventory(), IngredientAbility.VEGETABLE));
            assertEquals(1, countDish(kitchen.farmer.getInventory(), HarvestFeast.class));
            int adjacentFeasts = 0;
            for (Location adjacent : AdjacentLocations.from(kitchen.station)) {
                for (Item item : adjacent.getItems()) {
                    if (item.getClass() == HarvestFeast.class) adjacentFeasts++;
                }
            }
            assertEquals(1, adjacentFeasts);
        }
    }

    @Test
    void jamConsumptionRestoresOneThreeAndSevenStaminaOnlyOnce()
            throws GameEngineException {
        for (int fruitCount : new int[]{1, 3, 7}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            kitchen.farmer.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.UPDATE, 0);
            JamDish jam = new JamDish(fruitCount);
            kitchen.farmer.getInventory().add(jam);
            ConsumeAction action = new ConsumeAction(jam);
            String first = action.execute(kitchen.farmer, kitchen.map);
            assertTrue(first.contains("restores"));
            assertEquals(fruitCount, kitchen.farmer.getStatistic(ActorStatistics.STAMINA));
            assertFalse(kitchen.farmer.getInventory().getItems().contains(jam));
            String second = action.execute(kitchen.farmer, kitchen.map);
            assertTrue(second.contains("no longer"));
            assertEquals(fruitCount, kitchen.farmer.getStatistic(ActorStatistics.STAMINA));
        }
    }

    @Test
    void omeletteConsumptionRemovesZeroOneOrThreeStatuses()
            throws GameEngineException {
        for (int statuses : new int[]{0, 1, 3}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            for (int i = 0; i < statuses; i++) {
                kitchen.farmer.addStatus(new TestStatus());
            }
            new OmeletteDish().consume(kitchen.farmer);
            assertEquals(0, kitchen.farmer.statuses().size());
        }
    }

    @Test
    void harvestFeastConsumptionIncreasesBothMaximumStatsOneTwoOrThreeTimes()
            throws GameEngineException {
        for (int servings : new int[]{1, 2, 3}) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            int maxHp = kitchen.farmer.getMaximumStatistic(ActorStatistics.HEALTH);
            int maxSp = kitchen.farmer.getMaximumStatistic(ActorStatistics.STAMINA);
            for (int i = 0; i < servings; i++) {
                new HarvestFeast().consume(kitchen.farmer);
            }
            assertEquals(maxHp + servings,
                    kitchen.farmer.getMaximumStatistic(ActorStatistics.HEALTH));
            assertEquals(maxSp + servings,
                    kitchen.farmer.getMaximumStatistic(ActorStatistics.STAMINA));
        }
    }

    @Test
    void realGameIngredientsAreRecognizedByAllThreeRecipeCategories() {
        Inventory inventory = new Inventory();
        inventory.add(new MelonItem());
        inventory.add(new Egg());
        inventory.add(new ParsnipItem());
        assertEquals(1, new JamRecipe().selectIngredients(inventory).orElseThrow().size());
        assertEquals(2, new OmeletteRecipe().selectIngredients(inventory).orElseThrow().size());
        assertTrue(new HarvestFeastRecipe().selectIngredients(inventory).isEmpty(),
                "One ingredient per category is not enough for a Harvest Feast");
    }

    @Test
    void ingredientEffectsRunOnceForEveryConsumedItemAcrossThreeRecipes()
            throws GameEngineException {
        for (Recipe recipe : List.of(new JamRecipe(), new OmeletteRecipe(), new HarvestFeastRecipe())) {
            Kitchen kitchen = new Kitchen(2, 2, 2, 1);
            List<TestIngredient> fruit = kitchen.add(IngredientAbility.FRUIT, 5);
            List<TestIngredient> animal = kitchen.add(IngredientAbility.ANIMAL_PRODUCT, 5);
            List<TestIngredient> vegetable = kitchen.add(IngredientAbility.VEGETABLE, 5);
            new CookAction(recipe, kitchen.station).execute(kitchen.farmer, kitchen.map);

            int expectedFruit = recipe.name().equals("Omelette") ? 0 : 5;
            int expectedAnimal = recipe.name().equals("Jam") ? 0
                    : recipe.name().equals("Omelette") ? 1 : 5;
            int expectedVegetable = recipe.name().equals("Jam") ? 0
                    : recipe.name().equals("Omelette") ? 1 : 5;

            assertEquals(expectedFruit, totalUses(fruit));
            assertEquals(expectedAnimal, totalUses(animal));
            assertEquals(expectedVegetable, totalUses(vegetable));
            for (TestIngredient ingredient : fruit) assertTrue(ingredient.useCount() <= 1);
            for (TestIngredient ingredient : animal) assertTrue(ingredient.useCount() <= 1);
            for (TestIngredient ingredient : vegetable) assertTrue(ingredient.useCount() <= 1);
        }
    }

    private static int totalUses(List<TestIngredient> ingredients) {
        int total = 0;
        for (TestIngredient ingredient : ingredients) total += ingredient.useCount();
        return total;
    }

    @Test
    void jamConvertsDirtOnAllAdjacentLocationsAtCenterEdgeAndCorner()
            throws GameEngineException {
        int[][] positions = {{2, 2, 8}, {0, 2, 5}, {0, 0, 3}};
        for (int[] pos : positions) {
            // Centre/edge/corner positions are deliberately all tested.
            Kitchen kitchen = new Kitchen(pos[0], pos[1],
                    pos[0] == 0 ? 1 : 2, pos[1] == 0 ? 0 : 1);
            new JamRecipe().applyCookEffect(kitchen.farmer, kitchen.station, List.of());
            int soilTiles = 0;
            for (Location adjacent : AdjacentLocations.from(kitchen.station)) {
                if (adjacent.getGround().getClass() == Soil.class) soilTiles++;
            }
            assertEquals(pos[2], soilTiles);
        }
    }
}
