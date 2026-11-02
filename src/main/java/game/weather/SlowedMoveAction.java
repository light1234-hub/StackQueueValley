package game.weather;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weatherblueprint.MovementSlowingTerrain;


/**
 * Represents a Slowed Move Action by enforcing cooldowns and restrictions on the player's mvoement
 */
public final class SlowedMoveAction extends MoveActorAction
{
    private static final String WAIT_MESSAGE =
            "The weather and terrain slow your movement. You must wait before moving again";

    private final Location destination;

    /**
     * A constructor that creates an object of the SlowedMoveAction class
     * @param destination
     * @param direction
     * @param hotKey
     */
    public SlowedMoveAction(Location destination, String direction, String hotKey)
    {
        super(destination,direction,hotKey);
        this.destination = destination;
    }

    /**
     * Executes the move action, checks if the movement is slowed
     * If a cooldown is active, the actor has to wait, else the actor
     * peforms the movement and triggers the weather movement cooldown
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a string describing the result of the move action or wait notification
     */
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

    /**
     * Checks whether an actor has it's movement slowed through applied statuses
     * or weather affected terrain
     * @param actor
     * @param source
     * @param destination
     * @return true if the movement is slowed, false if otherwise
     */
    private boolean isMovementSlowed(Actor actor, Location source, Location destination)
    {
        return actor.hasStatus(SoakedStatus.class) || hasMovementSlowingTerrain(source) || hasMovementSlowingTerrain(destination);
    }

    /**
     * Checks if the specified location has a terrain that slows down the movement of an actor
     * @param location
     * @return true if the terrain slows down an actor, false if the terrain doesn't slow the actor
     */
    private boolean hasMovementSlowingTerrain(Location location)
    {
        return location.getGroundAs(MovementSlowingTerrain.class) != null;
    }
}
