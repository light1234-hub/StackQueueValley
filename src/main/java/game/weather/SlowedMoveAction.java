package game.weather;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weatherblueprint.MovementSlowingTerrain;

import java.util.List;

public final class SlowedMoveAction extends MoveActorAction
{
    private static final String WAIT_MESSAGE =
            "The weather and terrain slow your movement. You must wait before moving again";

    private final Location destination;

    public SlowedMoveAction(Location destination, String direction, String hotKey)
    {
        super(destination,direction,hotKey);
        this.destination = destination;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        Location source = map.locationOf(actor);

        if (!isMovementSlowed(actor,source,destination))
        {
            return super.execute(actor,map);
        }

        WeatherMovementCooldown cooldown = actor.statusesOf(WeatherMovementCooldown.class).stream().findFirst().orElse(null);

        if (cooldown != null && cooldown.isCoolingDown())
        {
            return WAIT_MESSAGE;
        }

        String result = super.execute(actor,map);

        if (cooldown == null)
        {
            cooldown = new WeatherMovementCooldown();
            actor.addStatus(cooldown);
        }

        cooldown.start();
        return result + " The conditon slow your movement.";
    }

    private boolean isMovementSlowed(Actor actor, Location source, Location destination)
    {
        return actor.hasStatus(SoakedStatus.class) || hasMovementSlowingTerrain(source) || hasMovementSlowingTerrain(destination);
    }

    private boolean hasMovementSlowingTerrain(Location location)
    {
        return location.getGroundAs(MovementSlowingTerrain.class) != null;
    }
}
