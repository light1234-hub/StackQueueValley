package game.weatherterrain;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.SlowedMoveAction;
import game.weather.WeatherLocationMapper;

/**
 * Represents a specialized map location that integrates the weather mechanics,
 * returning the weather-aware movement actions for actors navigating the terrain
 */
public final class WeatherLocation extends Location
{
    /**
     * A constructor that creates an object of the WeatherLocation class
     * @param map
     * @param x
     * @param y
     */
    public WeatherLocation(WeatherMap map, int x, int y )
    {
        super(map,x,y);
    }

    /**
     * Returns a movement action for an actor navigating to this location
     * @param actor the Actor to move
     * @param direction the direction of the destination from actor
     * @param hotKey the character that will trigger the movement command
     * @return a SlowedMoveAction if the actor can enter, null if otherwise
     */
    public MoveActorAction getMoveAction(Actor actor, String direction, String hotKey)
    {
        if (!canActorEnter(actor))
        {
            return null;
        }

        return new SlowedMoveAction(this,direction,hotKey);
    }

}
