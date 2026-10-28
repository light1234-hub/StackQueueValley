package game.tavern;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents a stone wall in the Tavern and Quest Arena.
 * Prevents actors from entering the wall's location.
 */
public class TavernWall extends Ground {

    /**
     * Creates a stone wall with the display character '#'.
     */
    public TavernWall(){
        super('#',"Stone wall");
    }

    /**
     * Determines whether an actor can enter this wall.
     *
     * @param actor the actor attempting to enter the location
     * @return false because actors cannot enter stone walls
     */
    @Override
    public boolean canActorEnter(Actor actor){
        return false;
    }
}
