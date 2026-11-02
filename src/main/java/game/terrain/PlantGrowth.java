package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;

/** Factory methods for creating plants at supported life stages. */
public final class PlantGrowth {
    /** Plant species supported by REQ1. */
    public enum Species { STRAWBERRY, TOMATO, PARSNIP, MELON, CAULIFLOWER }

    private PlantGrowth() { }

    /** Creates the initial strawberry seedling. */
    public static Plant seedling(Species species) {
        if (species != Species.STRAWBERRY) {
            throw new IllegalArgumentException("Only strawberries have a seedling stage.");
        }
        return new Plant(species, Plant.Stage.SEEDLING);
    }

    /** Creates a supported immature plant stage. */
    public static Plant sprout(Species species) {
        if (species != Species.STRAWBERRY && species != Species.MELON && species != Species.CAULIFLOWER) {
            throw new IllegalArgumentException(species + " does not have a sprout stage.");
        }
        return new Plant(species, Plant.Stage.SPROUT);
    }

    /** Creates a species-specific mature crop. */
    public static Ground crop(Species species) {
        return create(species, Plant.Stage.CROP);
    }

    static PlantLifeGround create(Species species, Plant.Stage stage) {
        if (species == null || stage == null) {
            throw new IllegalArgumentException("Species and stage are required.");
        }
        if (stage == Plant.Stage.SEEDLING && species != Species.STRAWBERRY) {
            throw new IllegalArgumentException("Only strawberries have a seedling stage.");
        }
        if (stage == Plant.Stage.SPROUT && species != Species.STRAWBERRY
                && species != Species.MELON && species != Species.CAULIFLOWER) {
            throw new IllegalArgumentException(species + " does not have a sprout stage.");
        }
        if (stage == Plant.Stage.CROP) {
            return createCrop(species);
        }
        return new Plant(species, stage);
    }

    private static PlantLifeGround createCrop(Species species) {
        switch (species) {
            case STRAWBERRY: return new StrawberryCrop();
            case TOMATO: return new TomatoCrop();
            case PARSNIP: return new ParsnipCrop();
            case MELON: return new MelonCrop();
            case CAULIFLOWER: return new CauliflowerCrop();
            default: throw new IllegalStateException("Unsupported plant species: " + species);
        }
    }
}


