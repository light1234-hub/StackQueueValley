package game.terrain;

import edu.monash.fit2099.engine.positions.Location;

/** The non-interactive growing stage of a cauliflower plant. */
public class CauliflowerSprout extends Plant {
    public CauliflowerSprout() { super('y', "Cauliflower Sprout"); }

    @Override
    protected String takeTurn(Location location, int age) {
        return grow(location, age, 15, 0.25, CauliflowerCrop::new)
                ? "grows into a Cauliflower Crop." : null;
    }
}
