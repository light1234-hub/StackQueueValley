package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.WanderBehaviour;
import game.blueprint.Coatable;
import game.farmeraction.SwordAttackAction;
import game.farmeraction.CreatureAttackAction;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Shared turn, wandering, and attack behavior for hostile mine creatures.
 * Concrete creatures provide their own attack damage and hit chance.
 */
public abstract class HostileCreature extends Actor {
    private final WanderBehaviour wander = new WanderBehaviour();

    /**
     * Creates a hostile mine creature.
     * @param name creature name
     * @param glyph map display character
     * @param hp starting health points
     */
    protected HostileCreature(String name, char glyph, int hp) {
        super(name, glyph, hp);
    }

    /** @return damage dealt by this creature's attack */
    protected abstract int damage();
    /** @return percentage chance that this creature's attack hits */
    protected abstract int hitChance();

    /**
     * Hook for creature-specific effects when a sword attack hits or misses.
     *
     * @param location creature's location when attacked
     * @param killed whether the attack has reduced the creature to zero health
     */
    public void onAttacked(Location location, boolean killed) { }

    /** Attacks an adjacent farmer; otherwise wanders to an available tile. */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            unconscious(map);
            return new edu.monash.fit2099.engine.actions.DoNothingAction();
        }
        Location here = map.locationOf(this);
        for (edu.monash.fit2099.engine.positions.Exit exit : here.getExits()) {
            Location next = exit.getDestination();
            if (next.containsAnActor() && next.getActor() instanceof Farmer)
                return new CreatureAttackAction((Farmer) next.getActor(), exit.getName(), damage(), hitChance());
        }
        Action action = wander.operate(this, here);
        return action == null ? new edu.monash.fit2099.engine.actions.DoNothingAction() : action;
    }

    /** Offers sword attacks only to an adjacent farmer carrying a coatable weapon. */
    @Override
    public ActionList allowableActions(Actor other, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if (other instanceof Farmer) {
            for (edu.monash.fit2099.engine.items.Item item : other.getInventory().getItems()) {
                if (item instanceof Weapon && item instanceof Coatable)
                    actions.add(new SwordAttackAction(this, direction, item, (Weapon) item, (Coatable) item));
            }
        }
        return actions;
    }
}
