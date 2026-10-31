package game.terrain;

/** Factory methods for creating plant species at a requested lifecycle stage. */
public final class PlantGrowth {
    /** Plant species supported by REQ1. */
    public enum Species { STRAWBERRY, TOMATO, PARSNIP, MELON, CAULIFLOWER }

    private PlantGrowth() { }

    /** Creates the initial strawberry stage. */
    public static Plant seedling(PlantGrowth.Species species) {
        if (species != PlantGrowth.Species.STRAWBERRY)
            throw new IllegalArgumentException("Only strawberries have a seedling stage.");
        return create(species, Plant.Stage.SEEDLING);
    }

    /** Creates a species' sprout stage. */
    public static Plant sprout(PlantGrowth.Species species) {
        if (species != PlantGrowth.Species.STRAWBERRY && species != PlantGrowth.Species.MELON
                && species != PlantGrowth.Species.CAULIFLOWER)
            throw new IllegalArgumentException(species + " does not have a sprout stage.");
        return create(species, Plant.Stage.SPROUT);
    }

    /** Creates a species' mature crop stage. */
    public static Plant crop(PlantGrowth.Species species) {
        return create(species, Plant.Stage.CROP);
    }

    static Plant create(PlantGrowth.Species species, Plant.Stage stage) {
        if (species == null || stage == null) throw new IllegalArgumentException("Species and stage are required.");
        if (stage == Plant.Stage.SEEDLING && species != PlantGrowth.Species.STRAWBERRY)
            throw new IllegalArgumentException("Only strawberries have a seedling stage.");
        if (stage == Plant.Stage.SPROUT && species != PlantGrowth.Species.STRAWBERRY
                && species != PlantGrowth.Species.MELON && species != PlantGrowth.Species.CAULIFLOWER)
            throw new IllegalArgumentException(species + " does not have a sprout stage.");
        return new Plant(species, stage);
    }
}
