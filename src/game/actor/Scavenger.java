package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.terrain.ScavengerTrapGround;

import java.util.ArrayList;
import java.util.List;

public class Scavenger extends HostileCreature{

    private static final int TRAP_INTERVAL_TURNS=3;
    private static final int MAX_TRAPS = 3;

    private int turns;
    private int trapsBuilt;

    /**
     * Creates a scavenger with its own combat statistic and trap limit
     */
    public Scavenger(){
        super("Scavenger",'s',90);
    }

    /**
     * Returns the damage dealt by the scavenger's attack.
     *
     * @return twelve damage points
     */

    @Override
    protected int damage(){
        return 12;
    }

    /**
     * Returns the chance that the scavenger's attack hits.
     *
     * @return hit percentage
     */
    @Override
    protected int hitChance() {
        return 65;
    }

    /**
     * Steals loose portable items, lays a trap when due, and then performs the
     * inherited hostile-creature turn behaviour.
     *
     * @param actions actions available this turn
     * @param lastAction action performed on the previous turn
     * @param map map containing this scavenger
     * @param display display used by the game engine
     * @return the action selected for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            return super.playTurn(actions, lastAction, map, display);
        }

        Location current = map.locationOf(this);
        stealLooseResources(current);
        turns++;

        if (turns % TRAP_INTERVAL_TURNS == 0 && trapsBuilt < MAX_TRAPS) {
            buildTrap(current);
        }
        return super.playTurn(actions, lastAction, map, display);
    }

    /**
     * Moves portable items from the current ground tile into the scavenger's inventory.
     *
     * @param location scavenger's current location
     */
    private void stealLooseResources(Location location) {
        for (Item item : new ArrayList<>(location.getItems())) {
            if (item.hasAbility(ItemAbility.PORTABLE)) {
                location.removeItem(item);
                getInventory().add(item);
            }
        }
    }

    /**
     * Places a temporary snare on the first adjacent unoccupied tile that advertises
     * the trap-buildable capability.
     *
     * @param location scavenger's current location
     */
    private void buildTrap(Location location) {
        List<Location> candidates = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location target = exit.getDestination();
            Ground ground = target.getGround();
            if (!target.containsAnActor()
                    && target.canActorEnter(this)
                    && ground.hasAbility(GroundAbility.TRAP_BUILDABLE)) {
                candidates.add(target);
            }
        }

        if (!candidates.isEmpty()) {
            Location target = candidates.get(0);
            target.setGround(new ScavengerTrapGround(target.getGround()));
            trapsBuilt++;
        }
    }

    /**
     * Drops the scavenger's stolen items at its location when it is defeated.
     *
     * @param location location where the scavenger was attacked
     * @param killed whether the attack defeated the scavenger
     */
    @Override
    public void onAttacked(Location location, boolean killed) {
        if (!killed) {
            return;
        }
        for (Item item : new ArrayList<>(getInventory().getItems())) {
            getInventory().remove(item);
            location.addItem(item);
        }
    }
}
