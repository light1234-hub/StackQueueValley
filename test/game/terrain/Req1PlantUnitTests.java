package game.terrain;

import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.item.ParsnipItem;
import game.item.StrawberryItem;
import game.item.TomatoItem;
import game.products.PlantingLocations;

import java.util.List;
import java.util.Objects;

/** Standalone checks for the REQ1 plant lifecycle. */
public final class Req1PlantUnitTests {
    private int passed;

    private Req1PlantUnitTests() { }

    public static void main(String[] args) throws Exception {
        new Req1PlantUnitTests().runAll();
    }

    private void runAll() throws Exception {
        run("strawberry sprout spreads a seedling", this::strawberrySproutSpreadsSeedling);
        run("newborn spread plants stay age zero for their creation turn", this::newbornKeepsAgeZero);
        run("plant factories create new plants at age zero", this::plantFactoriesStartAtAgeZero);
        run("strawberry, tomato, and parsnip produce on schedule", this::matureCropsProduceOnSchedule);
        run("melon crop spreads a sprout every twentieth turn", this::melonCropSpreadsAtTurnTwenty);
        run("planting helper accepts only plantable ground", this::plantingHelperFindsSoil);
        run("immature plants wait until their growth intervals", this::immatureStagesWaitUntilDue);
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
        for (int turn = 0; turn < 5; turn++) parent.getGround().tick(parent);
        assertEquals("Strawberry Seedling", soil.getGround().toString(),
                "A strawberry sprout must spread a seedling.");
        assertEquals('\'', soil.getGround().getDisplayChar(), "Seedling glyph should be retained.");
    }

    private void newbornKeepsAgeZero() throws Exception {
        GameMap map = newMap("..");
        map.at(0, 0).setGround(PlantGrowth.sprout(PlantGrowth.Species.MELON));
        map.at(1, 0).setGround(new Soil());
        for (int turn = 0; turn < 10; turn++) tickGrounds(map, 2, 1);
        Plant child = map.at(1, 0).getGroundAs(Plant.class);
        assertEquals(0, child.age(), "A newborn spread onto a later tile must stay age zero this turn.");
        tickGrounds(map, 2, 1);
        assertEquals(1, child.age(), "The newborn should age on the next world turn.");
    }

    private void plantFactoriesStartAtAgeZero() {
        assertEquals(0, PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY).age(), "Seedlings start at age zero.");
        assertEquals(0, PlantGrowth.sprout(PlantGrowth.Species.MELON).age(), "Sprouts start at age zero.");
        assertEquals(0, PlantGrowth.crop(PlantGrowth.Species.TOMATO).age(), "Crops start at age zero.");
    }

    private void matureCropsProduceOnSchedule() throws Exception {
        assertProductionSchedule(PlantGrowth.crop(PlantGrowth.Species.STRAWBERRY), 5, StrawberryItem.class);
        assertProductionSchedule(PlantGrowth.crop(PlantGrowth.Species.TOMATO), 7, TomatoItem.class);
        assertProductionSchedule(PlantGrowth.crop(PlantGrowth.Species.PARSNIP), 15, ParsnipItem.class);
    }

    private void assertProductionSchedule(Plant crop, int interval, Class<?> itemType) throws Exception {
        GameMap map = newMap("..");
        Location cropLocation = map.at(0, 0);
        Location adjacent = map.at(1, 0);
        cropLocation.setGround(crop);
        for (int turn = 1; turn < interval; turn++) {
            crop.tick(cropLocation);
            assertEquals(0, adjacent.getItems().size(), crop + " produced before turn " + interval + ".");
        }
        crop.tick(cropLocation);
        assertEquals(1, adjacent.getItems().size(), crop + " should produce on turn " + interval + ".");
        assertEquals(itemType, adjacent.getItems().get(0).getClass(), "Produced item type should match the crop.");
    }

    private void melonCropSpreadsAtTurnTwenty() throws Exception {
        GameMap map = newMap("..");
        Location crop = map.at(0, 0);
        Location soil = map.at(1, 0);
        crop.setGround(PlantGrowth.crop(PlantGrowth.Species.MELON));
        soil.setGround(new Soil());
        for (int turn = 1; turn < 20; turn++) {
            crop.getGround().tick(crop);
            assertEquals("Soil", soil.getGround().toString(), "Melon crop must wait until turn twenty to spread.");
        }
        crop.getGround().tick(crop);
        assertEquals("Melon Sprout", soil.getGround().toString(), "Melon crop spreads a sprout on turn twenty.");
    }

    private void plantingHelperFindsSoil() throws Exception {
        GameMap map = newMap("..", "..");
        Location origin = map.at(0, 0);
        Location soil = map.at(1, 0);
        map.at(0, 1).setGround(new Dirt());
        soil.setGround(new Soil());
        List<Location> eligible = PlantingLocations.plantableLocations(origin, 1);
        assertEquals(1, eligible.size(), "Only soil should be eligible for planting.");
        assertEquals(soil, eligible.get(0), "Planting helper should return the soil tile.");
    }

    private void immatureStagesWaitUntilDue() throws Exception {
        assertDoesNotChangeBeforeDue(PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY), 4, "Strawberry Seedling");
        assertDoesNotChangeBeforeDue(PlantGrowth.sprout(PlantGrowth.Species.CAULIFLOWER), 14, "Cauliflower Sprout");
        assertDoesNotChangeBeforeDue(PlantGrowth.sprout(PlantGrowth.Species.MELON), 14, "Melon Sprout");
    }

    private void assertDoesNotChangeBeforeDue(Plant plant, int turns, String expected) throws Exception {
        GameMap map = newMap(".");
        Location location = map.at(0, 0);
        location.setGround(plant);
        for (int turn = 0; turn < turns; turn++) plant.tick(location);
        assertEquals(expected, location.getGround().toString(), expected + " should remain before its interval.");
    }

    /** Ticks map locations in GameMap's y-then-x order without requiring a World actor iterator. */
    private void tickGrounds(GameMap map, int width, int height) {
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) map.at(x, y).tick();
    }

    private GameMap newMap(String... rows) throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        return new GameMap("Plant Unit Test Map", creator, List.of(rows));
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual))
            throw new AssertionError(message + " Expected <" + expected + "> but was <" + actual + ">");
    }

    @FunctionalInterface
    private interface CheckedTest { void run() throws Exception; }
}
