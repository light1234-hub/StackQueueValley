package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.IndestructibleAbility;
import game.ability.WeatherEventAbility;
import game.weatherterrain.FallenTreeGround;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public final class TornadoEvent implements WeatherEvent
{
    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.TORNADO;
    }

    @Override
    public void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context)
    {
        for (Location location : affectedLocations)
        {
            scatterItems(location);
            displaceActor(location);

            if(!location.containsAnActor() && location.getGroundAs(FallenTreeGround.class)== null
                && location.getGround().hasAbility(IndestructibleAbility.PROTECTED))
            {
                location.setGround(new FallenTreeGround());
            }
        }
    }

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
