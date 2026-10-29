package game.terrain;

import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.item.ParsnipItem;
import game.item.StrawberryItem;
import game.item.TomatoItem;
import game.products.PlantingLocations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dependency-free unit checks for the REQ1 plant lifecycle.
 * Run this class with assertions enabled or invoke its main method directly.
 */
public final class Req1PlantUnitTests {
    private int passed;

    private Req1PlantUnitTests() { }

    public static void main(String[] args) throws Exception {
        new Req1PlantUnitTests().runAll();
    }

    private void runAll() throws Exception {
        run("strawberry sprout spreads a seedling", this::strawberrySproutSpreadsSeedling);
        run("new spread plant does not tick twice in its creation turn", this::newbornKeepsAgeZeroDuringCreationTurn);
        run("growth replacement starts at age zero", this::growthReplacementStartsAtAgeZero);
        run("strawberry, tomato, and parsnip produce on schedule", this::matureCropsProduceOnSchedule);
        run("melon crop spreads a sprout every twentieth turn", this::melonCropSpreadsAtTurnTwenty);
        run("planting helper accepts only plantable ground", this::plantingHelperFindsSoil);
        run("immature plants do not act before their intervals", this::immatureStagesWaitUntilDue);
        System.out.println("Passed " + passed + " REQ1 plant unit tests.");
    }

    private void run(String name, CheckedTest test) throws Exception {
        test.run();
        passed++;
        System.out.println("PASS: " + name);
    }

    private void strawberrySproutSpreadsSeedling() throws Exception {
        GameMap map = newMap("..");
        Location parent = map.at(0, 0);
        Location soil = map.at(1, 0);
        parent.setGround(PlantGrowth.sprout(PlantGrowth.Species.STRAWBERRY));
        soil.setGround(new Soil());

        for (int turn = 0; turn < 5; turn++) {
            parent.getGround().tick(parent);
        }

        assertEquals("Strawberry Seedling", soil.getGround().toString(),
                "A strawberry sprout must spread a seedling, not another sprout.");
        assertEquals('\'', soil.getGround().getDisplayChar(),
                "A strawberry seedling must use the seedling display character.");
    }

    private void newbornKeepsAgeZeroDuringCreationTurn() throws Exception {
        GameMap map = newMap("..");
        List<Integer> childAges = new ArrayList<>();
        SpawnerPlant parent = new SpawnerPlant(childAges);
        map.at(0, 0).setGround(parent);
        map.at(1, 0).setGround(new Soil());

        tickGrounds(map, 2, 1);
        assertEquals(0, childAges.size(),
                "A plant spread to a later tile must skip the remainder of its creation turn.");

        tickGrounds(map, 2, 1);
        assertEquals(List.of(1), childAges,
                "The newborn's first world-turn tick must advance it from age zero to age one.");
    }

    private void growthReplacementStartsAtAgeZero() throws Exception {
        GameMap map = newMap(".");
        List<Integer> replacementAges = new ArrayList<>();
        map.at(0, 0).setGround(new ReplacingPlant(replacementAges));

        tickGrounds(map, 1, 1);
        assertEquals(0, replacementAges.size(),
                "A replacement created during a tile's tick must not tick again immediately.");

        tickGrounds(map, 1, 1);
        assertEquals(List.of(1), replacementAges,
                "A replacement's first tick on the following turn must see age one.");
    }

    private void matureCropsProduceOnSchedule() throws Exception {
        assertProductionSchedule(new StrawberryCrop(), 5, StrawberryItem.class);
        assertProductionSchedule(new TomatoCrop(), 7, TomatoItem.class);
        assertProductionSchedule(new ParsnipCrop(), 15, ParsnipItem.class);
    }

    private void assertProductionSchedule(Plant crop, int interval, Class<?> itemType) throws Exception {
        GameMap map = newMap("..");
        Location cropLocation = map.at(0, 0);
        Location onlyAdjacentTile = map.at(1, 0);
        cropLocation.setGround(crop);

        for (int turn = 1; turn < interval; turn++) {
            crop.tick(cropLocation);
            assertEquals(0, onlyAdjacentTile.getItems().size(),
                    crop + " must not produce before turn " + interval + ".");
        }

        crop.tick(cropLocation);
        assertEquals(1, onlyAdjacentTile.getItems().size(),
                crop + " must produce on turn " + interval + ".");
        assertEquals(itemType, onlyAdjacentTile.getItems().get(0).getClass(),
                "The produced item must match the crop type.");
    }

    private void melonCropSpreadsAtTurnTwenty() throws Exception {
        GameMap map = newMap("..");
        Location cropLocation = map.at(0, 0);
        Location soil = map.at(1, 0);
        cropLocation.setGround(new MelonCrop());
        soil.setGround(new Soil());

        for (int turn = 1; turn < 20; turn++) {
            cropLocation.getGround().tick(cropLocation);
            assertEquals("Soil", soil.getGround().toString(),
                    "A melon crop must not spread before turn twenty.");
        }

        cropLocation.getGround().tick(cropLocation);
        assertEquals("Melon Sprout", soil.getGround().toString(),
                "A mature melon crop must spread a melon sprout at turn twenty.");
    }

    private void plantingHelperFindsSoil() throws Exception {
        GameMap map = newMap("..", "..");
        Location origin = map.at(0, 0);
        Location soil = map.at(1, 0);
        map.at(0, 1).setGround(new Dirt());
        soil.setGround(new Soil());

        List<Location> eligible = PlantingLocations.plantableLocations(origin, 1);
        assertEquals(1, eligible.size(), "Only the soil tile should be eligible for planting.");
        assertEquals(soil, eligible.get(0), "The helper should return the plantable soil tile.");
    }

    private void immatureStagesWaitUntilDue() throws Exception {
        assertDoesNotChangeBeforeDue(PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY), 4,
                "Strawberry Seedling");
        assertDoesNotChangeBeforeDue(PlantGrowth.sprout(PlantGrowth.Species.CAULIFLOWER), 14,
                "Cauliflower Sprout");
        assertDoesNotChangeBeforeDue(PlantGrowth.sprout(PlantGrowth.Species.MELON), 14,
                "Melon Sprout");
    }

    private void assertDoesNotChangeBeforeDue(Plant plant, int turns, String expectedName) throws Exception {
        GameMap map = newMap(".");
        Location location = map.at(0, 0);
        location.setGround(plant);
        for (int turn = 0; turn < turns; turn++) {
            plant.tick(location);
        }
        assertEquals(expectedName, location.getGround().toString(),
                expectedName + " must remain in its current stage before its growth interval.");
    }

    /** Ticks map locations in the same y-then-x order as GameMap.tick(). */
    private void tickGrounds(GameMap map, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map.at(x, y).tick();
            }
        }
    }

    private GameMap newMap(String... rows) throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        return new GameMap("Plant Unit Test Map", creator, List.of(rows));
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected <" + expected + "> but was <" + actual + ">.");
        }
    }

    @FunctionalInterface
    private interface CheckedTest {
        void run() throws Exception;
    }

    private static final class RecordingPlant extends Plant {
        private final List<Integer> observedAges;

        private RecordingPlant(List<Integer> observedAges) {
            super('r', "Recording Plant");
            this.observedAges = observedAges;
        }

        @Override
        protected String takeTurn(Location location, int age) {
            observedAges.add(age);
            return null;
        }
    }

    private static final class SpawnerPlant extends Plant {
        private final List<Integer> childAges;
        private boolean spawned;

        private SpawnerPlant(List<Integer> childAges) {
            super('p', "Spawner Plant");
            this.childAges = childAges;
        }

        @Override
        protected String takeTurn(Location location, int age) {
            if (!spawned) {
                spawned = true;
                spread(location, new RecordingPlant(childAges), age, 1);
            }
            return null;
        }
    }

    private static final class ReplacingPlant extends Plant {
        private final List<Integer> replacementAges;
        private boolean replaced;

        private ReplacingPlant(List<Integer> replacementAges) {
            super('g', "Replacing Plant");
            this.replacementAges = replacementAges;
        }

        @Override
        protected String takeTurn(Location location, int age) {
            if (!replaced) {
                replaced = grow(location, age, 1, 1.0,
                        () -> new RecordingPlant(replacementAges));
            }
            return null;
        }
    }
}
