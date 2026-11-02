package game.weatherterrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Waterable;
import game.effect.FireEffect;
import game.farmeraction.WaterAction;

public final class ScorchedGround extends WeatherGround implements Waterable
{
    public ScorchedGround(Ground originalGround)
    {
        super('d',"Scorched Ground",originalGround);
        addStatus(new FireEffect(3,0,2,3));
    }

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

    @Override
    public int waterStamina()
    {
        return 2;
    }

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
