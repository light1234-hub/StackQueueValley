package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Waterable;
import game.farmeraction.WaterAction;

/** Shared interaction setup for crops that can be watered. */
abstract class WaterablePlantCrop extends MaturePlantCrop implements Waterable {
    protected int timeWatered;
    protected WaterablePlantCrop(char glyph, String name) {
        super(glyph, name);
    }

    @Override
    public int waterStamina() { return 1; }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new WaterAction(this, location));
        return actions;
    }
}
