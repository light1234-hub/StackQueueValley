package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.item.CauliflowerItem;

/** Mature cauliflower crop behavior. */
public class CauliflowerCrop extends WaterablePlantCrop {
    public CauliflowerCrop() { super('Y', "Cauliflower crop"); }

    @Override
    protected String takeTurn(Location location) { return null; }

    @Override
    public boolean canHarvest() { return timeWatered >= 5; }

    @Override
    public String harvest(Actor actor, Location location) {
        location.setGround(new Dirt());
        if (random.nextBoolean()) {
            actor.getInventory().add(new CauliflowerItem());
            return "Harvested the Cauliflower successfully!";
        }
        return "Harvested the Cauliflower, but it yielded nothing.";
    }

    @Override
    public int staminaCost() { return 3; }

    @Override
    public int waterStamina() { return 2; }

    @Override
    public String water(Actor actor, Location location) {
        timeWatered++;
        if (!random.nextBoolean()) return "Watered Cauliflower";
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround().hasAbility(TerrainAbility.CAN_BE_PUDDLE)) {
                adjacent.setGround(new Puddle());
            }
        }
        return "Watered Cauliflower and adjacent ground turned into puddles!";
    }
}
