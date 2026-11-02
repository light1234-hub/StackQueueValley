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
            long contributors = tileWeather.values().stream()
                    .filter(condition::isContributedBy)
                    .count();

            if (contributors >= condition.getMinContributingTiles()) {
                return Optional.of(condition.getEventType());
            }
        }

        return Optional.empty();
    }
}
