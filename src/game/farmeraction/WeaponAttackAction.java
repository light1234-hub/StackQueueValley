package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.blueprint.AttackReactive;
import game.blueprint.Coatable;
import game.blueprint.Coating;

import java.util.Objects;

/**
 * Attacks with any carried item exposing the engine's Weapon capability.
 * Optional coatings and target reactions are discovered by capability, not by
 * naming a particular item or hard-coding a concrete creature subclass.
 */
public class WeaponAttackAction extends Action {
    private final Actor target;
    private final String direction;
    private final Item weaponItem;
    private final Weapon weapon;

    /** Sets up a generic carried-weapon attack. */
    public WeaponAttackAction(Actor target, String direction, Item weaponItem, Weapon weapon) {
        this.target = Objects.requireNonNull(target);
        this.direction = Objects.requireNonNull(direction);
        this.weaponItem = Objects.requireNonNull(weaponItem);
        this.weapon = Objects.requireNonNull(weapon);
    }

    /** Performs the weapon attack and invokes optional coating/reaction hooks. */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!map.contains(actor) || !map.contains(target)) {
            return "That target is no longer here.";
        }
        if (!actor.getInventory().getItems().contains(weaponItem)) {
            return "The selected weapon is no longer in your inventory.";
        }
        Location sourceLocation = map.locationOf(actor);
        Location targetLocation = map.locationOf(target);
        int separation = Math.max(Math.abs(sourceLocation.x() - targetLocation.x()),
                Math.abs(sourceLocation.y() - targetLocation.y()));
        if (sourceLocation == targetLocation || separation != 1) {
            return "Move adjacent to the target before attacking.";
        }
        String result = weapon.attack(actor, target, map);
        Coating coating = weaponItem.asCapability(Coatable.class)
                .map(Coatable::getCoating).orElse(null);
        if (coating != null) {
            result += coating.apply(actor, target, map);
        }
        boolean killed = !target.isConscious();
        target.asCapability(AttackReactive.class)
                .ifPresent(reactive -> reactive.onAttacked(targetLocation, killed));
        if (killed && map.contains(target)) {
            result += "\n" + target.unconscious(actor, map);
        }
        return result;
    }

    /** Describes the attack using the selected weapon item. */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction + " with " + weaponItem;
    }
}
