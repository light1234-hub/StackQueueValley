package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an immature plant using a species and life-stage value rather than
 * a dedicated ground subclass for every seedling or sprout.
 * Growth rules are attempted in the order specified by REQ1.
 */
public final class PlantGrowth extends Plant {
    /** Immature plant species supported by the lifecycle rules. */
    public enum Species {
        /** Strawberry has both a seedling and a sprout stage. */
        STRAWBERRY("Strawberry", '\'', 'v'),
        /** Cauliflower has a sprout stage before its mature crop. */
        CAULIFLOWER("Cauliflower", 'y', 'y'),
        /** Melon has a sprout stage before its mature crop. */
        MELON("Melon", 't', 't');

        /** Name prefix used to construct the map display name. */
        private final String displayName;
        /** Map character used for the strawberry seedling stage. */
        private final char seedlingCharacter;
        /** Map character used for the sprout stage. */
        private final char sproutCharacter;

        /**
         * Defines the display properties for a supported species.
         *
         * @param displayName species name prefix for display
         * @param seedlingCharacter map character used by its seedling stage
         * @param sproutCharacter map character used by its sprout stage
         */
        Species(String displayName, char seedlingCharacter, char sproutCharacter) {
            this.displayName = displayName;
            this.seedlingCharacter = seedlingCharacter;
            this.sproutCharacter = sproutCharacter;
        }
    }

    /** Life-stage values supported by {@link PlantGrowth}. */
    public enum Stage {
        /** First strawberry stage. */
        SEEDLING,
        /** Immature stage used by strawberry, cauliflower, and melon. */
        SPROUT
    }

    /** Species whose growth rules and display properties apply. */
    private final Species species;
    /** Immature life stage represented by this instance. */
    private final Stage stage;

    /**
     * Creates an immature plant for the requested species and stage.
     *
     * @param species plant species to represent
     * @param stage immature stage to represent
     * @throws IllegalArgumentException if either value is null or the combination
     *                                  is unsupported
     */
    public PlantGrowth(Species species, Stage stage) {
        super(displayCharacter(species, stage), displayName(species, stage));
        if (species == null || stage == null || (stage == Stage.SEEDLING && species != Species.STRAWBERRY)) {
            throw new IllegalArgumentException("Unsupported plant species/stage combination");
        }
        this.species = species;
        this.stage = stage;
    }

    /**
     * Selects the map character for a species-stage combination.
     *
     * @param species plant species
     * @param stage immature stage
     * @return map character for that stage
     * @throws IllegalArgumentException if species or stage is null
     */
    private static char displayCharacter(Species species, Stage stage) {
        if (species == null || stage == null) throw new IllegalArgumentException("Species and stage are required");
        return stage == Stage.SEEDLING ? species.seedlingCharacter : species.sproutCharacter;
    }

    /**
     * Creates the display name for a species-stage combination.
     *
     * @param species plant species
     * @param stage immature stage
     * @return display name shown for the plant
     * @throws IllegalArgumentException if species or stage is null
     */
    private static String displayName(Species species, Stage stage) {
        if (species == null || stage == null) throw new IllegalArgumentException("Species and stage are required");
        return species.displayName + (stage == Stage.SEEDLING ? " Seedling" : " Sprout");
    }

    /**
     * Attempts the configured stage's growth and spreading actions in priority order.
     *
     * @param location location occupied by this plant
     * @param age current age in turns
     * @return description of the first successful action, or {@code null} if none occurs
     */
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

    /**
     * Creates a strawberry seedling.
     *
     * @param species species for the seedling; only strawberry supports this stage
     * @return a new seedling plant with age zero
     * @throws IllegalArgumentException if the species does not support a seedling
     */
    public static PlantGrowth seedling(Species species) {
        return new PlantGrowth(species, Stage.SEEDLING);
    }

    /**
     * Creates a sprout for a supported plant species.
     *
     * @param species strawberry, cauliflower, or melon
     * @return a new sprout plant with age zero
     * @throws IllegalArgumentException if species is null
     */
    public static PlantGrowth sprout(Species species) {
        return new PlantGrowth(species, Stage.SPROUT);
    }
}
