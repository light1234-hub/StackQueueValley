package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.ability.ToolAbility;
import game.blueprint.Breakable;
import game.farmeraction.BreakAction;
import game.item.StoneItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Impassable rock used as an obstacle. It can be broken with a mining tool,
 * drops a stone and can be repaired by the survivor-settlement mechanic.
 */
public class Rock extends Ground implements Breakable {
    private final Random random = new Random();

    /** Creates an impassable rock and marks it as repairable farm damage. */
    public Rock() {
        super('R', "Rock");
        enableAbility(GroundAbility.SURVIVOR_REPAIRABLE);
    }

    /**
     * Prevents actors from walking through this rock.
     *
     * @param actor actor attempting to enter the rock tile
     * @return always {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Breaks this rock, drops a stone, and may push the breaking actor to a free adjacent tile.
     *
     * @param actor actor breaking the rock
     * @param location location containing this rock
     * @return a description of the result
     */
    @Override
    public String breakBlock(Actor actor, Location location) {
        location.addItem(new StoneItem());
        location.setGround(new Dirt());
        String message = actor + " breaks the rock and a stone drops out.";

        if (random.nextBoolean()) {
            List<Location> landingSpots = new ArrayList<>();
            for (Exit exit : location.getExits()) {
                Location candidate = exit.getDestination();
                if (candidate.canActorEnter(actor) && !candidate.containsAnActor()) {
                    landingSpots.add(candidate);
                }
            }
            if (!landingSpots.isEmpty()) {
                Location destination = landingSpots.get(random.nextInt(landingSpots.size()));
                location.map().moveActor(actor, destination);
                message += " A shockwave throws " + actor + " onto a nearby tile!";
            }
        }
        return message;
    }

    /**
     * Returns the stamina cost for breaking rock.
     *
     * @return zero stamina points
     */
    @Override
    public int breakStaminaCost() {
        return 0;
    }

    /**
     * Offers a break action to actors with mining ability.
     *
     * @param actor actor interacting with the rock
     * @param location location containing the rock
     * @param direction direction from the actor to the rock
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (actor.hasAbility(ToolAbility.MINE)) {
            actions.add(new BreakAction(this, location));
        }
        return actions;
    }
}