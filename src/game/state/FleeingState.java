package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.FleeBehaviour;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FleeingState implements CreatureState
{
    private final FleeBehaviour fleeBehaviour = new FleeBehaviour();
    private static final int SCAN_RANGE = 3;
    private static final int ADJACENT_RANGE = 1;
    private static final int FOOD_SCAN_RANGE = 2;

    @Override
    public CreatureState nextState(Zombie zombie, GameMap map)
    {
        int hpPercent = ZombieSensor.healthPercent(zombie);

        boolean hasAdjThreat = ZombieSensor.hasTargetNearby(zombie,map,ADJACENT_RANGE);
        boolean hasFoodNearby = ZombieSensor.hasFoodNearby(zombie,map,FOOD_SCAN_RANGE);
        boolean hasThreatInScanRange = ZombieSensor.hasTargetNearby(zombie,map,SCAN_RANGE);

        if (hpPercent < ZombieSensor.HEAL_BELOW_PERCENT && (!hasAdjThreat || hasFoodNearby))
        {
            return zombie.getHealingState();
        }

        if (!hasThreatInScanRange && hpPercent >= ZombieSensor.HEAL_BELOW_PERCENT)
        {
            return zombie.getWanderingState();
        }

        return this;
    }

    @Override
    public Action getAction(Zombie zombie, GameMap map)
    {
        Action fleeAction = fleeBehaviour.operate(zombie,map.locationOf(zombie));

        return fleeAction != null ? fleeAction : new DoNothingAction();
    }

    @Override
    public void onEnter(Zombie zombie, GameMap map)
    {
        Location zombieLoc = map.locationOf(zombie);
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();

        queue.add(zombieLoc);
        visited.add(zombieLoc);

        while (!queue.isEmpty())
        {
            Location current = queue.poll();
            int distance = Math.max(Math.abs(current.x() - zombieLoc.x())
            , Math.abs(current.y() - zombieLoc.y()));

            if (distance > 0 && distance <= 2 && current.containsAnActor())
            {
                Actor victim = current.getActor();
                if (ZombieSensor.isTarget(victim))
                {
                    int pushDx = Integer.compare(current.x(), zombieLoc.x());
                    int pushDy = Integer.compare(current.y(), zombieLoc.y());

                    Location pushDestination = getRelativeLocation(map,current,pushDx * 2, pushDy * 2);
                    if (pushDestination == null || pushDestination.containsAnActor()
                    || !pushDestination.canActorEnter(victim))
                    {
                        pushDestination = getRelativeLocation(map,current,pushDx,pushDy);
                    }

                    if (pushDestination != null && !pushDestination.containsAnActor()
                    && pushDestination.canActorEnter(victim))
                    {
                        map.moveActor(victim,pushDestination);
                    }
                }
            }

            if (distance < 2)
            {
                for (Exit exit : current.getExits())
                {
                    Location destination = exit.getDestination();
                    if (!visited.contains(destination))
                    {
                        visited.add(destination);
                        queue.add(destination);
                    }
                }
            }
        }
    }

    private Location getRelativeLocation(GameMap map, Location base, int deltaX, int deltaY)
    {
        try
        {
            return map.at(base.x() + deltaX, base.y() + deltaY);
        } catch (Exception e)
        {
            return null;
        }
    }
}
