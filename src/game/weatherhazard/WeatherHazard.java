package game.weatherhazard;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Ground;
import game.weather.TileWeather;

import java.util.Objects;

/**
 * An abstract that class that is extended by all different Hazards
 */
public abstract class WeatherHazard extends GameEntity
{
    private final Location location;
    private final Ground originalGround;
    private boolean active;

    /**
     * A constructor that creates an object of the WeatherHazard class
     * @param location
     * @param hazardAbility
     * @param originalGround
     */
    protected WeatherHazard(Location location, WeatherHazardAbility hazardAbility, Ground originalGround)
    {
        this.location = location;
        this.originalGround = originalGround;
        this.active = true;
        this.enableAbility(Objects.requireNonNull(hazardAbility));
    }

    /**
     * A getter the returns the location of the hazard
     * @return the location of the hazard
     */
    public final Location getLocation()
    {
        return location;
    }

    /**
     * A getter that returns the original terrain of the ground before the weather hazard
     * @return original terrain
     */
    public final Ground getOriginalGround()
    {
        return originalGround;
    }

    /**
     * Checks whether a hazard is still active on the game map
     * @return true if active, false if not active
     */
    public final boolean isActive()
    {
        return active;
    }

    /**
     * Ends the hazard's lifecycle
     */
    protected final void deactivate()
    {
        active = false;
    }

    /**
     * Progresses the Hazard by one game turn
     * @param tileWeather
     * @param context
     */
    public abstract void tick(TileWeather tileWeather, WeatherContext context);

}
