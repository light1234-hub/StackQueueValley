package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.item.MelonItem;

/** Mature melon crop behavior. */
public class MelonCrop extends WaterablePlantCrop {
    public MelonCrop() { super('Q', "Melon Crop"); }

    @Override
    protected String takeTurn(Location location) {
        Location child = spread(location, PlantGrowth.create(PlantGrowth.Species.MELON, Plant.Stage.SPROUT), 20);
        return child == null ? null : spreadMessage("Melon Sprout", child);
    }

    @Override
    public String harvest(Actor actor, Location location) {
        location.setGround(new Dirt());
        if (random.nextBoolean()) {
            int damage = 20 - timeWatered;
            if (damage > 0) {
                if (location.containsAnActor()) location.getActor().hurt(damage);
                for (Exit exit : location.getExits()) {
                    Location adjacent = exit.getDestination();
                    if (adjacent.containsAnActor()) adjacent.getActor().hurt(damage);
                }
            }
            return "The Melon exploded, damaging all nearby actors!";
        }
        actor.getInventory().add(new MelonItem());
        return "Melon has been harvested and added into inventory";
    }

    @Override
    public int staminaCost() { return 5; }

    @Override
    public String water(Actor actor, Location location) {
        timeWatered++;
        if (random.nextBoolean()) {
            actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);
            return "Watered Melon and MAX SP increase by 1 .";
        }
        return "Watered Melon.";
    }
}
