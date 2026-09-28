package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A healing status effect that restores health over 2 turns.
 */
public class HealingEffect implements Status{
    private int duration = 2;
    private final Actor victim;

    /**
     * Creates a healing effect tied directly to the actor who consumed it.
     *
     * @param victim the actor who will receive healing
     */
    public HealingEffect(Actor victim){
        this.victim = victim;
    }

    /**
     * Executes every turn to heal the victim and decrease the remaining time.
     *
     * @param currEntity the entity this status is attached to
     * @param location the current location of the entity on the map
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location){
        if(duration > 0){
            victim.heal(1);
            duration--;
        }
    }

    /**
     * Checks if the healing effect is still active.
     * The game engine uses this to know when to automatically delete the status.
     *
     * @return true if there are turns remaining, false if it has expired
     */
    @Override
    public  boolean isStatusActive(){
        return duration > 0;
    }

}
