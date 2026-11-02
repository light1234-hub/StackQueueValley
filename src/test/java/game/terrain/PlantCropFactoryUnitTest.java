package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** JUnit tests for the plant growth factory and crop interaction contracts. */
class PlantCropFactoryUnitTest {

    @BeforeEach
    void reportTestName(TestInfo testInfo) {
        System.out.println("RUN: " + testInfo.getDisplayName());
    }

    @Test
    void createsEachSpeciesSpecificCrop() {
        assertEquals(StrawberryCrop.class,
                PlantGrowth.crop(PlantGrowth.Species.STRAWBERRY).getClass());
        assertEquals(TomatoCrop.class,
                PlantGrowth.crop(PlantGrowth.Species.TOMATO).getClass());
        assertEquals(ParsnipCrop.class,
                PlantGrowth.crop(PlantGrowth.Species.PARSNIP).getClass());
        assertEquals(MelonCrop.class,
                PlantGrowth.crop(PlantGrowth.Species.MELON).getClass());
        assertEquals(CauliflowerCrop.class,
                PlantGrowth.crop(PlantGrowth.Species.CAULIFLOWER).getClass());
    }

    @Test
    void keepsSeedlingAndSproutStagesInPlant() {
        Plant seedling = PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY);
        assertEquals(PlantGrowth.Species.STRAWBERRY, seedling.species());
        assertEquals(Plant.Stage.SEEDLING, seedling.stage());

        assertSprout(PlantGrowth.Species.STRAWBERRY);
        assertSprout(PlantGrowth.Species.MELON);
        assertSprout(PlantGrowth.Species.CAULIFLOWER);
    }

    @Test
    void preservesCropInteractionContracts() {
        assertCropContract(PlantGrowth.Species.STRAWBERRY, 1, true, 1);
        assertCropContract(PlantGrowth.Species.TOMATO, 2, true, null);
        assertCropContract(PlantGrowth.Species.PARSNIP, 1, true, 1);
        assertCropContract(PlantGrowth.Species.MELON, 5, true, 1);
        assertCropContract(PlantGrowth.Species.CAULIFLOWER, 3, false, 2);
    }

    @Test
    void rejectsUnsupportedSproutSpecies() {
        assertThrows(IllegalArgumentException.class,
                () -> PlantGrowth.sprout(PlantGrowth.Species.TOMATO));
        assertThrows(IllegalArgumentException.class,
                () -> PlantGrowth.sprout(PlantGrowth.Species.PARSNIP));
    }

    private void assertSprout(PlantGrowth.Species species) {
        Plant sprout = PlantGrowth.sprout(species);
        assertEquals(species, sprout.species());
        assertEquals(Plant.Stage.SPROUT, sprout.stage());
    }

    private void assertCropContract(PlantGrowth.Species species, int harvestCost,
                                    boolean initiallyHarvestable, Integer waterCost) {
        Ground ground = PlantGrowth.crop(species);
        Harvestable crop = (Harvestable) ground;
        assertEquals(harvestCost, crop.staminaCost(), species + " harvest stamina cost");
        assertEquals(initiallyHarvestable, crop.canHarvest(),
                species + " initial harvest readiness");

        boolean supportsWatering = Waterable.class.isAssignableFrom(ground.getClass());
        assertEquals(waterCost != null, supportsWatering, species + " watering support");
        if (waterCost != null) {
            Waterable waterable = (Waterable) ground;
            assertEquals(waterCost, Integer.valueOf(waterable.waterStamina()),
                    species + " water stamina cost");
        }
    }
}
