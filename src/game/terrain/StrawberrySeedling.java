package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/** The first, non-interactive stage of a strawberry plant. */
public class StrawberrySeedling extends Plant {
    public StrawberrySeedling() { super('\'', "Strawberry Seedling"); }

    @Override
    protected String takeTurn(Location location, int age) {
        return grow(location, age, 5, 0.50, StrawberrySprout::new)
                ? "grows into a Strawberry Sprout." : null;
    }
}
