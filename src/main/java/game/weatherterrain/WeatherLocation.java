package game.weatherterrain;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.SlowedMoveAction;
import game.weather.WeatherLocationMapper;

public final class WeatherLocation extends Location
{
    public WeatherLocation(WeatherMap map, int x, int y )
    {
        super(map,x,y);
    }

    public MoveActorAction getMoveAction(Actor actor, String direction, String hotKey)
    {
        if (!canActorEnter(actor))
        {
            return null;
        }

        return new SlowedMoveAction(this,direction,hotKey);
    }

}
