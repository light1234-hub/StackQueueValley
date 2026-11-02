package game.actor;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.behaviour.AttackBehaviour;
import game.behaviour.WanderBehaviour;
import game.blueprint.AttackReactive;
import game.blueprint.Coatable;
import game.farmeraction.SwordAttackAction;

/** Shared registered attack and wandering behaviors for hostile creatures. */
public abstract class HostileCreature extends NPC implements AttackReactive {
    /**
     * Creates a hostile mine creature and registers its turn behaviors.
     * Attack has priority over wandering.
     *
     * @param name creature name
     * @param glyph map display character
     * @param hp starting health points
     */
    protected HostileCreature(String name, char glyph, int hp) {
        super(name, glyph, hp);
        addBehaviour(new AttackBehaviour(this::damage, this::hitChance));
        addBehaviour(new WanderBehaviour());
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
    @Override
    public void onAttacked(Location location, boolean killed) { }

    /** Offers sword attacks to an adjacent target carrying a coatable weapon. */
    @Override
    public ActionList allowableActions(Actor other, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if (other.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
            for (Item item : other.getInventory().getItems()) {
                item.asCapability(Weapon.class).ifPresent(weapon ->
                        item.asCapability(Coatable.class).ifPresent(coatable ->
                                actions.add(new SwordAttackAction(this, direction, item, weapon, coatable))));
            }
        }
        return actions;
    }
}
