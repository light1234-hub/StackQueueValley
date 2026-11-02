package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/** Temporary stun. While active, a creature's stun behaviour makes it skip its turn. */
public class StunStatus implements Status {
    private int turns;

    /** @param turns number of turns the creature stays stunned */
    public StunStatus(int turns) {
        this.turns = turns;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turns > 0) {
            turns--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turns > 0;
    }
}