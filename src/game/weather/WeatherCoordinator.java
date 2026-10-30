package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.WeatherEventAbility;
import game.weatherapi.WeatherDataException;
import game.weatherapi.WeatherDataProvider;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Periodically fetches weather, generates tile conditions, evaluates events,
 * and applies a selected event around the Farmer.
 */
public final class WeatherCoordinator {
    private static final int DEFAULT_REFRESH_INTERVAL = 5;
    private static final int DEFAULT_EVENT_RADIUS = 2;

    private final GameMap farmMap;
    private final WeatherDataProvider provider;
    private final WeatherLocationMapper coordinateMapper;
    private final TileWeatherGenerator tileWeatherGenerator;
    private final WeatherEvaluator evaluator;
    private final Map<WeatherEventAbility, WeatherEvent> events;
    private final WeatherContext context;
    private final int refreshInterval;
    private final int eventRadius;

    private int turnsUntilRefresh;

    public WeatherCoordinator(
            GameMap farmMap,
            WeatherDataProvider provider,
            WeatherLocationMapper coordinateMapper,
            TileWeatherGenerator tileWeatherGenerator,
            WeatherEvaluator evaluator,
            Map<WeatherEventAbility, WeatherEvent> events,
            WeatherContext context,
            int refreshInterval,
            int eventRadius) {
        if (refreshInterval < 1 || eventRadius < 0) {
            throw new IllegalArgumentException(
                    "Refresh interval must be positive and radius cannot be negative."
            );
        }

        this.farmMap = farmMap;
        this.provider = provider;
        this.coordinateMapper = coordinateMapper;
        this.tileWeatherGenerator = tileWeatherGenerator;
        this.evaluator = evaluator;
        this.events = new EnumMap<>(events);
        this.context = context;
        this.refreshInterval = refreshInterval;
        this.eventRadius = eventRadius;
    }

    /**
     * Periodically updates weather using the supplied location as the weather centre.
     *
     * @param weatherCentre current location used for API coordinates and event radius
     */
    public void updateOnTurn(Location weatherCentre) {

        if (weatherCentre.map() != farmMap)
        {
            return;
        }

        if (turnsUntilRefresh > 0) {
            turnsUntilRefresh--;
            return;
        }

        turnsUntilRefresh = refreshInterval - 1;

        GeographicCoordinate coordinate =
                coordinateMapper.toCoordinate(weatherCentre);

        try {
            WeatherSnapshot snapshot = provider.fetch(
                    coordinate.getLatitude(),
                    coordinate.getLongitude()
            );

            Map<Location, TileWeather> tileWeather =
                    generateWeatherForEveryTile(snapshot,farmMap);

            context.updateTileWeather(tileWeather);
            triggerQualifiedEvent(tileWeather, weatherCentre);
        } catch (WeatherDataException exception) {
            System.err.println("Weather API Error: " + exception.getMessage());
        }
    }

    private Map<Location, TileWeather> generateWeatherForEveryTile(
            WeatherSnapshot snapshot,
            GameMap map) {
        Map<Location, TileWeather> tileWeather = new LinkedHashMap<>();

        for (int x : farmMap.getXRange()) {
            for (int y : farmMap.getYRange()) {
                Location location = map.at(x, y);
                tileWeather.put(
                        location,
                        tileWeatherGenerator.generate(snapshot, location)
                );
            }
        }

        return tileWeather;
    }

    private void triggerQualifiedEvent(
            Map<Location, TileWeather> tileWeather,
            Location farmerLocation) {
        evaluator.evaluate(tileWeather).ifPresent(eventType -> {
            WeatherEvent event = events.get(eventType);

            if (event != null) {
                event.apply(
                        farmMap,
                        locationsWithinRadius(farmerLocation),
                        context
                );
            }
        });
    }

    private Set<Location> locationsWithinRadius(Location centre) {
        Set<Location> locations = new LinkedHashSet<>();
        int radiusSquared = eventRadius * eventRadius;

        for (int x : farmMap.getXRange()) {
            for (int y : farmMap.getYRange()) {
                int dx = x - centre.x();
                int dy = y - centre.y();

                if (dx * dx + dy * dy <= radiusSquared) {
                    locations.add(centre.map().at(x, y));
                }
            }
        }

        return locations;
    }

    /**
     * Creates the default event rules. Adjust the coordinate origin to the
     * real-world location represented by the farm.
     */
    public static WeatherCoordinator createDefault(GameMap farmMap) {
        List<WeatherConditions> conditions = List.of(
                new WeatherConditions(
                        WeatherEventAbility.TORNADO, 5,
                        null, null, 70.0, null
                ),
                new WeatherConditions(
                        WeatherEventAbility.HEATWAVE, 5,
                        null, 35.0, null, null
                ),
                new WeatherConditions(
                        WeatherEventAbility.RAINSTORM, 5,
                        1.0, null, null, null
                )
        );

        Map<WeatherEventAbility, WeatherEvent> events =
                new EnumMap<>(WeatherEventAbility.class);
        events.put(WeatherEventAbility.RAINSTORM, new RainstormEvent());
        events.put(WeatherEventAbility.HEATWAVE, new HeatwaveEvent());
        events.put(WeatherEventAbility.TORNADO, new TornadoEvent());

        return new WeatherCoordinator(
                farmMap,
                new game.weatherapi.OpenMeteoProvider(),
                new WeatherLocationMapper(Double.parseDouble(System.getenv("WEATHER_LATITUDE")),
                                            Double.parseDouble(System.getenv("WEATHER_LONGITUDE")),
                                            0.00009,0.00014),
                new TileWeatherGenerator(),
                new WeatherEvaluator(conditions),
                events,
                new WeatherContext(),
                DEFAULT_REFRESH_INTERVAL,
                DEFAULT_EVENT_RADIUS
        );
    }
}