package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/** A strawberry plant that can mature or spread seedlings. */
public class StrawberrySprout extends Plant {
    public StrawberrySprout() { super('v', "Strawberry Sprout"); }

    @Override
    protected String takeTurn(Location location, int age) {
        if (grow(location, age, 10, 0.50, StrawberryCrop::new))
            return "grows into a Strawberry Crop.";
        Location child = spread(location, new StrawberrySeedling(), age, 5);
        return child == null ? null : spreadMessage("Strawberry Seedling", child);
    }
}
