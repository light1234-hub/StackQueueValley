package game.weatherapi;

import game.weather.WeatherSnapshot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OpenMeteoProvider implements WeatherDataProvider
{
    private final HttpClient client;

    public OpenMeteoProvider()
    {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @Override
    public WeatherSnapshot fetch(double latitude, double longitude) throws WeatherDataException
    {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m,rain";

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build();

        try
        {
            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new WeatherDataException("OpenMeteo returned HTTP " + response.statusCode());
            }

            return parseCsv(response.body());
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

    private WeatherSnapshot parseCsv(String csv) throws WeatherDataException
    {
        String[] lines = csv.split("\\R");
        String[] headers = null;
        String[] values = null;

        for (String line : lines)
        {
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#"))
            {
                continue;
            }

            String[] columns = trimmed.split(",",1);

            if (headers == null)
            {
                headers = columns;
            }
            else
            {
                values = columns;
                break;
            }
        }

        if (headers == null || values == null)
        {
            throw new WeatherDataException("CSV response has no weather rows");
        }

        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < Math.min(headers.length, values.length); i++)
        {
            data.put(headers[i].trim(),values[i].trim());
        }

        try
        {
            return new WeatherSnapshot(Double.parseDouble(data.get("temperature_2m")),
                                        Double.parseDouble(data.get("precipitation")),
                                        Double.parseDouble(data.get("wind_speed_10m")),
                                        Double.parseDouble(data.get("relative_humidity_2m")),
                                        Instant.now());
        }
        catch (RuntimeException exception)
        {
            throw new WeatherDataException("CSV response is missing valid weather values", exception);
        }

    }
}
