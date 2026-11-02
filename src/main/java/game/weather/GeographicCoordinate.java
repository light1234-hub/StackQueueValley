package game.weather;

/**
 * A geographical coordinate represented by longitude and latitude
 */
public final class GeographicCoordinate
{
    private final double latitude;
    private final double longitude;

    /**
     * Creates an object of the GeographicalCoordinate object
     * @param latitude
     * @param longitude
     */
    public GeographicCoordinate(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * A getter that returns the latitude value of a geographical coordinate
     * @return the latitude value
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * A getter that returns the longitude value of a geographical coordinate
     * @return the longitude value
     */
    public double getLongitude()
    {
        return longitude;
    }


}
