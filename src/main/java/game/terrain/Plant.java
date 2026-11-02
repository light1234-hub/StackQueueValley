package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/** A seedling or sprout whose species and stage are represented as state. */
public final class Plant extends PlantLifeGround {
    /** Stages represented by the state-based immature plant. */
    public enum Stage { SEEDLING, SPROUT, CROP }

    private final PlantGrowth.Species species;
    private final Stage stage;

    Plant(PlantGrowth.Species species, Stage stage) {
        super(glyph(species, stage), displayName(species, stage));
        this.species = species;
        this.stage = stage;
    }

    /** @return species represented by this plant */
    public PlantGrowth.Species species() { return species; }

    /** @return lifecycle stage represented by this plant */
    public Stage stage() { return stage; }


    @Override
    protected String takeTurn(Location location) {
        if (species == PlantGrowth.Species.STRAWBERRY && stage == Stage.SEEDLING) {
            return growInto(location, 5, 0.50,
                    PlantGrowth.create(species, Stage.SPROUT))
                    ? "grows into a Strawberry Sprout." : null;
        }
        if (species == PlantGrowth.Species.STRAWBERRY && stage == Stage.SPROUT) {
            if (growInto(location, 10, 0.50, PlantGrowth.create(species, Stage.CROP))) {
                return "grows into a Strawberry Crop.";
            }
            Location child = spread(location, PlantGrowth.create(species, Stage.SEEDLING), 5);
            return child == null ? null : spreadMessage("Strawberry Seedling", child);
        }
        if (species == PlantGrowth.Species.CAULIFLOWER && stage == Stage.SPROUT) {
            return growInto(location, 15, 0.25, PlantGrowth.create(species, Stage.CROP))
                    ? "grows into a Cauliflower Crop." : null;
        }
        if (species == PlantGrowth.Species.MELON && stage == Stage.SPROUT) {
            if (growInto(location, 15, 0.25, PlantGrowth.create(species, Stage.CROP))) {
                return "grows into a Melon Crop.";
            }
            Location child = spread(location, PlantGrowth.create(species, Stage.SPROUT), 10);
            return child == null ? null : spreadMessage("Melon Sprout", child);
        }
        return null;
    }

    private static char glyph(PlantGrowth.Species species, Stage stage) {
        switch (species) {
            case STRAWBERRY: return stage == Stage.SEEDLING ? '\'' : 'v';
            case MELON: return 't';
            case CAULIFLOWER: return 'y';
            default: throw new IllegalArgumentException(species + " has no immature stage.");
        }
    }

    private static String displayName(PlantGrowth.Species species, Stage stage) {
        String speciesName = species.name().charAt(0) + species.name().substring(1).toLowerCase();
        String stageName = stage == Stage.SEEDLING ? "Seedling" : "Sprout";
        return speciesName + " " + stageName;
    }

    @Override
    public String toString() { return displayName(species, stage); }
}

