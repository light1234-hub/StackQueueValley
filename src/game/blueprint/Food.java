package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * An interface that is implemented by all items that can be fed to Animals
 */
public interface Food
{
    void applyFarmerBonus(Actor actor);
}
