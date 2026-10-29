package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Data-driven representation of an immature plant. A species and life stage
 * describe its appearance and lifecycle, so adding a stage does not require a
 * dedicated Ground subclass.
 */
public final class PlantGrowth extends Plant {
    public enum Species {
        STRAWBERRY("Strawberry", '\'', 'v'),
        CAULIFLOWER("Cauliflower", 'y', 'y'),
        MELON("Melon", 't', 't');

        private final String displayName;
        private final char seedlingCharacter;
        private final char sproutCharacter;

        Species(String displayName, char seedlingCharacter, char sproutCharacter) {
            this.displayName = displayName;
            this.seedlingCharacter = seedlingCharacter;
            this.sproutCharacter = sproutCharacter;
        }
    }

    public enum Stage { SEEDLING, SPROUT }

    private final Species species;
    private final Stage stage;

    public PlantGrowth(Species species, Stage stage) {
        super(displayCharacter(species, stage), displayName(species, stage));
        if (species == null || stage == null || (stage == Stage.SEEDLING && species != Species.STRAWBERRY)) {
            throw new IllegalArgumentException("Unsupported plant species/stage combination");
        }
        this.species = species;
        this.stage = stage;
    }

    private static char displayCharacter(Species species, Stage stage) {
        if (species == null || stage == null) throw new IllegalArgumentException("Species and stage are required");
        return stage == Stage.SEEDLING ? species.seedlingCharacter : species.sproutCharacter;
    }

    private static String displayName(Species species, Stage stage) {
        if (species == null || stage == null) throw new IllegalArgumentException("Species and stage are required");
        return species.displayName + (stage == Stage.SEEDLING ? " Seedling" : " Sprout");
    }

    @Override
    protected String takeTurn(Location location, int age) {
        if (species == Species.STRAWBERRY && stage == Stage.SEEDLING) {
            return grow(location, age, 5, 0.50, () -> sprout(Species.STRAWBERRY))
                    ? "grows into a Strawberry Sprout." : null;
        }
        if (species == Species.STRAWBERRY) {
            if (grow(location, age, 10, 0.50, StrawberryCrop::new))
                return "grows into a Strawberry Crop.";
            Location child = spread(location, sprout(Species.STRAWBERRY), age, 5);
            return child == null ? null : spreadMessage("Strawberry Seedling", child);
        }
        if (species == Species.CAULIFLOWER) {
            return grow(location, age, 15, 0.25, CauliflowerCrop::new)
                    ? "grows into a Cauliflower Crop." : null;
        }
        if (stage == Stage.SPROUT) {
            if (grow(location, age, 15, 0.25, MelonCrop::new))
                return "grows into a Melon Crop.";
            Location child = spread(location, sprout(Species.MELON), age, 10);
            return child == null ? null : spreadMessage("Melon Sprout", child);
        }
        return null;
    }

    public static PlantGrowth seedling(Species species) {
        return new PlantGrowth(species, Stage.SEEDLING);
    }

    public static PlantGrowth sprout(Species species) {
        return new PlantGrowth(species, Stage.SPROUT);
    }
}
