package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.item.ParsnipItem;

/** Mature parsnip crop behavior. */
public class ParsnipCrop extends WaterablePlantCrop {
    public ParsnipCrop() { super('P', "Parsnip crop"); }

    @Override
    protected String takeTurn(Location location) {
        if (!due(15)) return null;
        Location target = randomAdjacent(location);
        if (target == null) return null;
        target.addItem(new ParsnipItem());
        return "drops a Parsnip at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
    }

    @Override
    public String harvest(Actor actor, Location location) {
        location.setGround(new Dirt());
        actor.getInventory().add(new ParsnipItem());
        Location target = randomAdjacent(location);
        if (target != null && random.nextBoolean()) target.addItem(new ParsnipItem());
        return "Harvested parsnip!!";
    }

    @Override
    public int staminaCost() { return 1; }

    @Override
    public String water(Actor actor, Location location) {
        timeWatered++;
        actor.heal(20);
        double destructionChance = (10.0 + timeWatered) / 100.0;
        if (random.nextDouble() < destructionChance) {
            location.setGround(new Dirt());
            return "Watered Parsnip, but the crop was destroyed";
        }
        return "Watered Parsnip and healed 20 HP!";
    }
}
