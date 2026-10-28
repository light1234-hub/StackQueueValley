package game.actor;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.behaviour.AdjacentAttackBehaviour;
import game.behaviour.WanderBehaviour;
import game.blueprint.AttackReactive;
import game.farmeraction.WeaponAttackAction;

/**
 * Shared hostile-NPC capabilities. A reusable adjacent-attack brain takes
 * priority over wandering; subclasses supply stats and optional reactions.
 */
public abstract class HostileCreature extends NonPlayerCharacter implements AttackReactive {

    /** Creates a hostile character using the same reusable combat behaviours. */
    protected HostileCreature(String name, char glyph, int hp) {
        super(name, glyph, hp);
        addBehaviour(new AdjacentAttackBehaviour(
                ZombieTarget.ZOMBIE_TARGET, this::damage, this::hitChance));
        addBehaviour(new WanderBehaviour());
    }

    /** @return this creature's attack damage */
    protected abstract int damage();

    /** @return this creature's attack hit probability, in percent */
    protected abstract int hitChance();

    /** Hook for distinctive effects when a creature is attacked. */
    @Override
    public void onAttacked(Location location, boolean killed) { }

    /** Offers the same generic attack action for every carried weapon. */
    @Override
    public ActionList allowableActions(Actor other, String direction, GameMap map) {
        ActionList actions = super.allowableActions(other, direction, map);
        if (!other.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
            return actions;
        }
        for (Item item : other.getInventory().getItems()) {
            item.asCapability(Weapon.class).ifPresent(weapon ->
                    actions.add(new WeaponAttackAction(this, direction, item, weapon)));
        }
        return actions;
    }
}
