package game.weatherterrain;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

public final class WeatherMap extends GameMap
{
    public WeatherMap(String name, GroundCreator groundCreator, List<String> lines) throws GameEngineException
    {
        super(name,groundCreator,lines);
    }

    @Override
    protected Location makeNewLocation(int x, int y)
    {
        return new WeatherLocation(this,x,y);
    }
}
