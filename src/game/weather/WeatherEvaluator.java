package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.ability.WeatherEventAbility;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Evaluates the tile's weather against specified weather event requirements
 */
public class WeatherEvaluator
{
    private final List<WeatherConditions> conditions;

    /**
     * A constructor that creates an object of the WeatherEvaluator class
     * @param conditions
     */
    public WeatherEvaluator(List<WeatherConditions> conditions)
    {
        this.conditions = List.copyOf(conditions);
    }

    /**
     * Returns the first weather event whose condition has enough qualifying tile
     * @param tileWeather
     * @return a selected weather event or empty if there is no event that fulfils the conditions
     */
    public Optional<WeatherEventAbility> evaluate(Map<Location, TileWeather> tileWeather)
    {
        for (WeatherConditions condition : conditions)
        {
            int contributors = 0;

            for (TileWeather weather : tileWeather.values())
            {
                if (contributes(weather, condition))
                {
                    contributors++;
                }
            }

            if (contributors >= condition.getMinContributingTiles())
            {
                return Optional.of(condition.getEventType());
            }
        }

        return Optional.empty();
    }

    /**
     * Checks whether a tile meets at least one threshold for a weather event conditionAdd
     * @param weather
     * @param condition
     * @return
     */
    private boolean contributes(TileWeather weather, WeatherConditions condition)
    {
        return weather.getPrecipitationMmPerHour() >= condition.getMinRainMmPerHour() ||
                weather.getTemperatureC() >= condition.getMinTemperatureC() ||
                weather.getWindSpeedKmh() >= condition.getMinWindKmh() ||
                weather.getRelativeHumidity() >= condition.getMinHumidity();
    }
}
