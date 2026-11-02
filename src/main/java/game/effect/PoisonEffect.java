package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A biological poison status effect that damages an actor over time.
 * This effect stores its target directly to apply damage safely without type casting.
 */
public class PoisonEffect implements Status {
    private int duration = 3;
    private final Actor victim;

    /**
     * Creates a poison effect tied directly to the actor who consumed it.
     *
     * @param victim the actor who will take damage from this poison
     */
    public PoisonEffect (Actor victim){
        this.victim = victim;
    }

    /**
     * Executes every turn to deal damage to the victim and decrease the remaining time.
     *
     * @param currEntity the entity this status is attached to
     * @param location the current location of the entity on the map
     */
    @Override
    public void tickStatus (GameEntity currEntity, Location location){
        if(duration > 0){
            victim.hurt(1);
            duration--;
        }
    }

    /**
     * Checks if the poison effect is still active.
     * The game engine uses this to know when to automatically delete the status.
     *
     * @return true if there are turns remaining, false if it has expired
     */
    @Override
    public boolean isStatusActive(){
        return duration>0;
    }
}
