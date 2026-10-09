package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ZombieTarget;
import game.effect.PoisonEffect;


public class CorruptedDirt extends Ground {
    private static final int LIFETIME_TURNS = 10;

    private int turnsLeft = LIFETIME_TURNS;

    /**
     * Creates a corrupted dirt tile.
     */
    public CorruptedDirt() {
        super('x', "Corrupted dirt");
    }


    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                actor.addStatus(new PoisonEffect(actor)); // check this matches how you attach statuses elsewhere
                location.setGround(new Dirt());
                return;
            }
        }

        turnsLeft--;
        if (turnsLeft <= 0) {
            location.setGround(new Dirt());
        }
    }
}