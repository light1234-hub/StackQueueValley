package game.weatherapi;

import game.weather.WeatherSnapshot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the communication between the codebase and the OpenMeteo weather API
 */
public class OpenMeteoProvider implements WeatherDataProvider
{
    private static final Pattern CURRENT_OBJECT = Pattern.compile("\"current\"\\s*:\\s*\\{([^}]*)\\}");

    private final HttpClient client;

    /**
     * Creates an Object of the OpenMeteoProvider class with a HTTP client with a 5-second connection timeout
     */
    public OpenMeteoProvider()
    {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    /**
     * Fetches real time weather data for a specified latitude and longitude coordinates from the Open-Meteo API
     * Validates the coordinate ranges, issues a HTTP Get request, verifies the HTTP status code and parses the response
     * @param latitude
     * @param longitude
     * @return
     * @throws WeatherDataException
     */
    @Override
    public WeatherSnapshot fetch(double latitude, double longitude) throws WeatherDataException
    {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
        {
            throw new IllegalArgumentException("Invalid latitude or longitude");
        }

        String url = String.format(
                Locale.ROOT,
                "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f"
                        + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,rain",
                latitude, longitude);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build();

        try
        {
            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new WeatherDataException("OpenMeteo returned HTTP " + response.statusCode());
            }

            return parseCurrentWeather(response.body());
        }
        catch (InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new WeatherDataException("Weather request was interrupted", exception);
        }
        catch (IOException exception)
        {
            throw new WeatherDataException("Could not retrieve the weather data", exception);
        }
    }

    /**
     * Parses the current weather object block from the JSON response and creates a WeatherSnapshot
     * @param json
     * @return
     * @throws WeatherDataException
     */
    private WeatherSnapshot parseCurrentWeather(String json)
            throws WeatherDataException {
        Matcher objectMatcher = CURRENT_OBJECT.matcher(json);

        if (!objectMatcher.find()) {
            throw new WeatherDataException(
                    "Open Meteo response did not contain current weather."
            );
        }

        String currentObject = objectMatcher.group(1);

        try {
            return new WeatherSnapshot(
                    readNumber(currentObject, "temperature_2m"),
                    readNumber(currentObject, "rain"),
                    readNumber(currentObject, "wind_speed_10m"),
                    readNumber(currentObject, "relative_humidity_2m"),
                    Instant.now()
            );
        } catch (IllegalArgumentException exception) {
            throw new WeatherDataException(
                    "Open Meteo returned incomplete current weather data.",
                    exception
            );
        }
    }

    /**
     * A helper method that uses a regular expression to extract a numeric property from the JSON object substring
     * @param jsonObject
     * @param property
     * @return
     */
    private double readNumber(String jsonObject, String property) {
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(property) + "\"\\s*:\\s*"
                        + "(-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)"
        );
        Matcher matcher = pattern.matcher(jsonObject);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Missing numeric property: " + property
            );
        }

        return Double.parseDouble(matcher.group(1));
    }
}
