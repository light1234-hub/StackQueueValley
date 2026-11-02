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
import game.item.CopperItem;
import game.item.DiamondItem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * A mineable crystal that participates in a connected resonant vein. Breaking one
 * crystal breaks connected crystals, drops resources, and may reveal a Copper Node.
 */
public class ResonantCrystalGround extends Ground implements Breakable {

    /** Creates one crystal and marks it as part of a resonant vein. */
    public ResonantCrystalGround() {
        super('*', "Resonant Crystal");
        enableAbility(GroundAbility.RESONANT_CRYSTAL);
    }

    /**
     * Prevents actors from walking onto a crystal tile.
     *
     * @param actor actor attempting to enter the tile
     * @return always {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Offers a mining action to actors that have a mining tool.
     *
     * @param actor actor interacting with the crystal
     * @param location location of this crystal
     * @param direction direction from the actor to the crystal
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

    /**
     * Returns the stamina cost of mining one crystal.
     *
     * @return two stamina points
     */
    @Override
    public int breakStaminaCost() {
        return 2;
    }

    /**
     * Breaks all adjacent connected crystals, drops mineral rewards, and attempts
     * to expose a Copper Node on a neighbouring resource-revealable tile.
     *
     * @param actor actor mining the crystal vein
     * @param location location of the initially mined crystal
     * @return a description of the chain reaction and resulting resources
     */
    @Override
    public String breakBlock(Actor actor, Location location) {
        if (location.getGround() != this) {
            return "The crystal vein has already been mined.";
        }

        List<Location> vein = connectedVein(location);
        Set<Location> veinLocations = new HashSet<>(vein);

        for (int index = 0; index < vein.size(); index++) {
            Location crystal = vein.get(index);
            crystal.addItem(index == 0 ? new DiamondItem() : new CopperItem());
            crystal.setGround(new Dirt());
        }

        for (Location crystal : vein) {
            for (Exit exit : crystal.getExits()) {
                Location neighbour = exit.getDestination();
                if (!veinLocations.contains(neighbour)
                        && !neighbour.containsAnActor()
                        && neighbour.getGround().hasAbility(
                        GroundAbility.RESOURCE_REVEALABLE)) {
                    neighbour.setGround(new CopperNode());
                    return "The crystal vein resonates! " + vein.size()
                            + " connected crystals break, resources scatter, "
                            + "and a Copper Node is exposed.";
                }
            }
        }

        return "The crystal vein resonates! " + vein.size()
                + " connected crystals break and drop resources.";
    }

    /**
     * Finds every crystal connected to the starting crystal using breadth-first search.
     *
     * @param start location of the initial crystal
     * @return all locations that belong to the connected vein
     */
    private List<Location> connectedVein(Location start) {
        List<Location> result = new ArrayList<>();
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.remove();
            if (!current.getGround().hasAbility(GroundAbility.RESONANT_CRYSTAL)) {
                continue;
            }
            result.add(current);

            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();
                if (!visited.contains(next)
                        && next.getGround().hasAbility(GroundAbility.RESONANT_CRYSTAL)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return result;
    }
}
