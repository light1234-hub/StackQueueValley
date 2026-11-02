package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.item.TomatoItem;

/** Mature tomato crop behavior. */
public class TomatoCrop extends MaturePlantCrop {
    public TomatoCrop() { super('T', "Tomato crop"); }

    @Override
    protected String takeTurn(Location location) {
        if (due(7)) {
            Location target = randomAdjacent(location);
            if (target == null) return null;
            target.addItem(new TomatoItem());
            return "drops a Tomato at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
        }
        if (due(20) && random.nextDouble() < 0.10) {
            Location child = spread(location, PlantGrowth.create(PlantGrowth.Species.TOMATO, Plant.Stage.CROP), 1);
            return child == null ? null : spreadMessage("Tomato Crop", child);
        }
        return null;
    }


    @Override
    public String harvest(Actor actor, Location location) {
        location.setGround(new Dirt());
        actor.getInventory().add(new TomatoItem());
        actor.getInventory().add(new TomatoItem());
        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);
        return "Harvested Tomato!!";
    }

    @Override
    public int staminaCost() { return 2; }
}



