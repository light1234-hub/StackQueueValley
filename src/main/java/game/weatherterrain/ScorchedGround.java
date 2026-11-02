package game.weatherterrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Waterable;
import game.effect.FireEffect;
import game.farmeraction.WaterAction;

/**
 * Represents a Scorched ground that is the resulting effect of a Heatwave weather event
 */
public final class ScorchedGround extends WeatherGround implements Waterable
{
    /**
     * A constructor that creates an object of the Scorched Ground
     * @param originalGround
     */
    public ScorchedGround(Ground originalGround)
    {
        super('d',"Scorched Ground",originalGround);
        addStatus(new FireEffect(3,0,2,3));
    }

    /**
     * Waters the scorched ground, restoring it back its original terrain
     * @param actor
     * @param location location containing the ground
     * @return a string describing the result of the watering
     */
    @Override
    public String water(Actor actor, Location location)
    {
        if (location.getGround() != this)
        {
            return "This ground has already been watered and restored to its original terrain.";
        }

        location.setGround(getOriginalGround());
        return "The ground has been watered and returns to " + getOriginalGround() + ".";
    }

    /**
     * The stamina cost that is required to water a scorched ground
     * @return 2
     */
    @Override
    public int waterStamina()
    {
        return 2;
    }

    /**
     * Returns a list of allowable actions that can be performed on the Scorched Ground
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction)
    {
        ActionList actions = super.allowableActions(actor,location,direction);

        if (direction.isEmpty())
        {
            actions.add(new WaterAction(this, location));
        }

        return actions;
    }
}
