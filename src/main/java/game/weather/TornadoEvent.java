package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.IndestructibleAbility;
import game.ability.WeatherEventAbility;
import game.weatherblueprint.WeatherRecoverable;
import game.weatherterrain.FallenTreeGround;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Represents a Tornado WeatherEvent in the game
 */
public final class TornadoEvent implements WeatherEvent
{
    /**
     * Returns the weather event ability type that is associated with the Tornado Event
     * @return the tornado from the WeatherEventAbility enum
     */
    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.TORNADO;
    }

    /**
     * Applies the tornado weather effect across the specified affected locations
     * Scatters items, displaces any actors presents and changes eligible ground into FallenTreeGround
     *
     * @param map
     * @param affectedLocations
     * @param context
     */
    @Override
    public void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context)
    {
        for (Location location : affectedLocations)
        {
            scatterItems(location);
            displaceActor(location);

            if(!location.containsAnActor() && location.getGroundAs(WeatherRecoverable.class)==null && !location.getGround().hasAbility(IndestructibleAbility.PROTECTED))
            {
                location.setGround(new FallenTreeGround());
            }
        }
    }

    /**
     * Scatters all items found in the adjacent tiles around the Source location
     * @param source
     */
    private void scatterItems(Location source)
    {
        List<Location> destinations = adjacentLocations(source);

        if (destinations.isEmpty())
        {
            return;
        }

        List<Item> items = new ArrayList<>(source.getItems());

        for (int index = 0; index < items.size(); index++)
        {
            Item item = items.get(index);
            source.removeItem(item);
            destinations.get(index % destinations.size()).addItem(item);
        }
    }

    /**
     * Displaces an actor standing on the source location to the first
     * available and enterable adjacent location
     * @param source
     */
    private void displaceActor(Location source)
    {
        if (!source.containsAnActor())
        {
            return;
        }

        Actor actor = source.getActor();

        for (Exit exit : source.getExits())
        {
            Location destination = exit.getDestination();

            if (destination.canActorEnter(actor) && !destination.containsAnActor())
            {
                source.map().moveActor(actor,destination);
                return;
            }
        }
    }

    /**
     * Retrieves a list of all neighboring adjacent locations around a source location
     * @param source
     * @return a list of adjacent locations
     */
    private List<Location> adjacentLocations(Location source)
    {
        List<Location> locations = new ArrayList<>();

        for (Exit exit : source.getExits())
        {
            locations.add(exit.getDestination());
        }

        return locations;
    }
}
