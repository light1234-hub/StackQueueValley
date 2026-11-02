package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;

/**
 * Lightweight, dependency-free unit checks for the restored crop classes.
 * Run this class as a Java application from IntelliJ.
 */
public final class PlantCropFactoryUnitTest {
    private static int checks;

    private PlantCropFactoryUnitTest() { }

    public static void main(String[] args) {
        runTest("createsEachSpeciesSpecificCrop", PlantCropFactoryUnitTest::createsEachSpeciesSpecificCrop);
        runTest("keepsSeedlingAndSproutStagesInPlant", PlantCropFactoryUnitTest::keepsSeedlingAndSproutStagesInPlant);
        runTest("preservesCropInteractionContracts", PlantCropFactoryUnitTest::preservesCropInteractionContracts);
        runTest("rejectsUnsupportedSproutSpecies", PlantCropFactoryUnitTest::rejectsUnsupportedSproutSpecies);
        System.out.println("PASS: " + checks + " REQ1 crop refactor checks");
    }

    private static void runTest(String testName, Runnable test) {
        System.out.println("RUN: " + testName);
        test.run();
        System.out.println("PASS: " + testName);
    }

    private static void createsEachSpeciesSpecificCrop() {
        checkType(StrawberryCrop.class, PlantGrowth.crop(PlantGrowth.Species.STRAWBERRY),
                "strawberry crop factory");
        checkType(TomatoCrop.class, PlantGrowth.crop(PlantGrowth.Species.TOMATO),
                "tomato crop factory");
        checkType(ParsnipCrop.class, PlantGrowth.crop(PlantGrowth.Species.PARSNIP),
                "parsnip crop factory");
        checkType(MelonCrop.class, PlantGrowth.crop(PlantGrowth.Species.MELON),
                "melon crop factory");
        checkType(CauliflowerCrop.class, PlantGrowth.crop(PlantGrowth.Species.CAULIFLOWER),
                "cauliflower crop factory");
    }

    private static void keepsSeedlingAndSproutStagesInPlant() {
        Plant seedling = PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY);
        check(seedling.species() == PlantGrowth.Species.STRAWBERRY, "seedling species");
        check(seedling.stage() == Plant.Stage.SEEDLING, "seedling stage");

        checkSprout(PlantGrowth.Species.STRAWBERRY);
        checkSprout(PlantGrowth.Species.MELON);
        checkSprout(PlantGrowth.Species.CAULIFLOWER);
    }

    private static void checkSprout(PlantGrowth.Species species) {
        Plant sprout = PlantGrowth.sprout(species);
        check(sprout.species() == species, species + " sprout species");
        check(sprout.stage() == Plant.Stage.SPROUT, species + " sprout stage");
    }

    private static void preservesCropInteractionContracts() {
        checkCropContract(PlantGrowth.Species.STRAWBERRY, 1, true, 1);
        checkCropContract(PlantGrowth.Species.TOMATO, 2, true, null);
        checkCropContract(PlantGrowth.Species.PARSNIP, 1, true, 1);
        checkCropContract(PlantGrowth.Species.MELON, 5, true, 1);
        checkCropContract(PlantGrowth.Species.CAULIFLOWER, 3, false, 2);
    }

    private static void checkCropContract(PlantGrowth.Species species, int harvestCost,
                                         boolean initiallyHarvestable, Integer waterCost) {
        Ground ground = PlantGrowth.crop(species);
        Harvestable crop = Harvestable.class.cast(ground);
        check(crop.staminaCost() == harvestCost, species + " harvest stamina cost");
        check(crop.canHarvest() == initiallyHarvestable, species + " initial harvest readiness");

        boolean supportsWatering = Waterable.class.isAssignableFrom(ground.getClass());
        check(supportsWatering == (waterCost != null), species + " watering support");
        if (supportsWatering) {
            Waterable waterable = Waterable.class.cast(ground);
            check(waterable.waterStamina() == waterCost, species + " water stamina cost");
        }
    }

    private static void rejectsUnsupportedSproutSpecies() {
        expectIllegalArgument(() -> PlantGrowth.sprout(PlantGrowth.Species.TOMATO),
                "tomato cannot be created as a sprout");
        expectIllegalArgument(() -> PlantGrowth.sprout(PlantGrowth.Species.PARSNIP),
                "parsnip cannot be created as a sprout");
    }

    private static void checkType(Class<?> expected, Object actual, String description) {
        check(actual.getClass() == expected, description + " should create " + expected.getSimpleName());
    }

    private static void expectIllegalArgument(Runnable operation, String description) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("FAIL: " + description + " should throw IllegalArgumentException");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("FAIL: " + description);
        checks++;
    }
}

