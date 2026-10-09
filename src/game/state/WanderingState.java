package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.WanderBehaviour;
import game.terrain.CorruptedDirt;
import game.terrain.Dirt;


public class WanderingState implements CreatureState {
    /** How many tiles away the zombie notices targets and food while wandering. */
    private static final int SCAN_RANGE = 3;

    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();

    @Override
    public CreatureState nextState(Zombie zombie, GameMap map) {
        int health = ZombieSensor.healthPercent(zombie);

        // severely injured: healing takes priority over fleeing
        if (health < ZombieSensor.HEAL_BELOW_PERCENT && ZombieSensor.hasFoodNearby(zombie, map, SCAN_RANGE)) {
            return zombie.getHealingState();
        }

        if (ZombieSensor.hasTargetNearby(zombie, map, SCAN_RANGE)) {
            if (health < ZombieSensor.ATTACK_MIN_PERCENT) {
                return zombie.getFleeingState();
            }
            return zombie.getAttackingState();
        }

        return this;
    }

    @Override
    public Action getAction(Zombie zombie, GameMap map) {
        Action action = wanderBehaviour.operate(zombie, map.locationOf(zombie));
        return action != null ? action : new DoNothingAction();
    }

    /**
     * Entry effect: as the zombie starts to wander it corrupts the plain
     * dirt on every tile next to it. Only Dirt is affected, so tilled soil,
     * crops, ladders and other terrain are left alone.
     */
    @Override
    public void onEnter(Zombie zombie, GameMap map) {
        for (Exit exit : map.locationOf(zombie).getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround() instanceof Dirt) {
                adjacent.setGround(new CorruptedDirt());
            }
        }
    }
}