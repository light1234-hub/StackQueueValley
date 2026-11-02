package game.weatherterrain;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Represents a specialized gamemap that is configured to utilize the weather-aware locations
 * overriding a location creation to create WeatherLocation instances across the grid
 */
public final class WeatherMap extends GameMap
{
    /**
     * A constructor that creates an object of the WeatherMap object
     * @param name
     * @param groundCreator
     * @param lines
     * @throws GameEngineException
     */
    public WeatherMap(String name, GroundCreator groundCreator, List<String> lines) throws GameEngineException
    {
        super(name,groundCreator,lines);
    }

    /**
     * Creates and returns a new weather-aware map location at the specified x and y coordinates
     * @param x X coordinate
     * @param y Y coordinate
     * @return a new WeatherLocation for this map at x and y
     */
    @Override
    protected Location makeNewLocation(int x, int y)
    {
        return new WeatherLocation(this,x,y);
    }
}
