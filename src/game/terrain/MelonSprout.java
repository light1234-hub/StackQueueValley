package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/** A melon plant that can mature or spread another sprout. */
public class MelonSprout extends Plant {
    public MelonSprout() { super('t', "Melon Sprout"); }

    @Override
    protected String takeTurn(Location location, int age) {
        if (grow(location, age, 15, 0.25, MelonCrop::new))
            return "grows into a Melon Crop.";
        Location child = spread(location, new MelonSprout(), age, 10);
        return child == null ? null : spreadMessage("Melon Sprout", child);
    }
}
