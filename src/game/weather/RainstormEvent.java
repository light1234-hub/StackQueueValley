package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.ability.WeatherEventAbility;
import game.weatherblueprint.Drainable;
import game.weatherterrain.FloodedGround;

import java.util.Set;

public final class RainstormEvent implements WeatherEvent
{
    private static final int SOAKED_TURNS = 3;

    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.RAINSTORM;
    }

    @Override
    public void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context)
    {
        for (Location location : affectedLocations)
        {
            Ground originalGround = location.getGround();

            location.setGround(new FloodedGround(originalGround));
            context.applyLatestWeather(location);
            markSoaked(location);
        }
    }

    private void markSoaked(Location location)
    {
        if (location.containsAnActor())
        {
            Actor actor = location.getActor();
            actor.addStatus(new SoakedStatus(SOAKED_TURNS));
        }
    }
}
