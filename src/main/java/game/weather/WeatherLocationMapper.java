package game.weather;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A class that converts in game locations into geographical coordinates
 */
public class WeatherLocationMapper
{
    private final double originLatitude;
    private final double originLongitude;
    private final double latitudePerTile;
    private final double longitudePerTile;

    /**
     * A constructor that creates an object of WeatherLocationMapper class
     * @param originLatitude
     * @param originLongitude
     * @param latitudePerTile
     * @param longitudePerTile
     */
    public WeatherLocationMapper(double originLatitude, double originLongitude, double latitudePerTile,
                                 double longitudePerTile)
    {
        this.originLatitude = originLatitude;
        this.originLongitude = originLongitude;
        this.latitudePerTile = latitudePerTile;
        this.longitudePerTile = longitudePerTile;
    }

    /**
     * Converts a map location into a latitude and longitude coordinate;
     * @param location
     * @return a geographical coordinate with latitude and longitude values
     */
    public GeographicCoordinate toCoordinate(Location location)
    {
        double latitude = originLatitude - location.y() * latitudePerTile;
        double longitude = originLongitude + location.x() * longitudePerTile;

        return new GeographicCoordinate(latitude,longitude);
    }
}
