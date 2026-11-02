package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.item.StrawberryItem;

/** Mature strawberry crop behavior. */
public class StrawberryCrop extends WaterablePlantCrop {
    public StrawberryCrop() { super('S', "Strawberry crop"); }

    @Override
    protected String takeTurn(Location location) {
        if (!due(5)) return null;
        Location target = randomAdjacent(location);
        if (target == null) return null;
        target.addItem(new StrawberryItem());
        return "drops a Strawberry at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
    }

    @Override
    public String harvest(Actor actor, Location location) {
        actor.getInventory().add(new StrawberryItem());
        actor.getInventory().add(new StrawberryItem());
        double destructionChance = Math.max(0, 50.0 - timeWatered) / 100.0;
        if (random.nextDouble() < destructionChance) {
            location.setGround(new Dirt());
            return "Harvested two Strawberries, but the crop was destroyed!";
        }
        return "Harvested Two Strawberry!!";
    }

    @Override
    public int staminaCost() { return 1; }

    @Override
    public String water(Actor actor, Location location) {
        timeWatered++;
        int added = 0;
        for (int i = 0; i < 2; i++) {
            Location target = randomAdjacent(location);
            if (target != null) {
                target.addItem(new StrawberryItem());
                added++;
            }
        }
        return "Watered Strawberry and added " + added + " Strawberry.";
    }
}
