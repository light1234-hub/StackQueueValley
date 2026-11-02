package game.app;

public class EnvironmentValidator
{
    private static final String[] REQUIRED_ENV_VAR =
            {"WEATHER_LATITUDE",
            "WEATHER_LONGITUDE"};

    public static void validateEnvironment()
    {
        boolean missingVariables = false;

        StringBuilder errorMessage = new StringBuilder("ERROR: Missing required environment variables: \n");

        for (String envVar : REQUIRED_ENV_VAR)
        {
            String value = System.getenv(envVar);

            if (value == null || value.trim().isEmpty())
            {
                errorMessage.append(" - ").append(envVar).append("\n");
                missingVariables = true;
            }
        }

        if (missingVariables)
        {
            errorMessage.append("\nPlease define your environment variables first before initializing the game."
            + "\nRefer to the README.md file for further instructions on how to initialize the variables.");
            System.err.println(errorMessage.toString());
            System.exit(1);
        }
    }
}
