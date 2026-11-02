package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Harvestable;
import game.farmeraction.HarvestAction;

/** Shared behavior and harvesting contract for mature crop grounds. */
abstract class MaturePlantCrop extends PlantLifeGround implements Harvestable {
    protected MaturePlantCrop(char glyph, String name) { super(glyph, name); }

    @Override
    public boolean canHarvest() { return true; }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new HarvestAction(this, location));
        return actions;
    }
}
