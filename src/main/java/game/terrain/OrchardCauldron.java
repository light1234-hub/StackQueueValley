package game.terrain;

import edu.monash.fit2099.engine.positions.Location;
import game.item.GaleDraught;

/** Brews Gale Draught; each brew turn plants an ArcaneSapling on a random empty tile. */
public class OrchardCauldron extends BrewingStation {
    public OrchardCauldron() {
        super('U', "Orchard Cauldron", GaleDraught::new);
    }

    @Override
    protected void applyBrewEffect(Location location) {
        Location tile = TileFinder.randomEmptyTile(location.map());
        if (tile != null) {
            tile.setGround(new ArcaneSapling(tile.getGround()));
        }
    }
}