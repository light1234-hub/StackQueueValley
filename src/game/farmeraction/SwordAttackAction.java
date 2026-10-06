package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actor.HostileCreature;
import game.blueprint.Coatable;
import game.blueprint.Coating;
import edu.monash.fit2099.engine.items.Item;

/** Performs the farmer's attack with a carried coatable weapon. */
public class SwordAttackAction extends Action {
    private final HostileCreature target;
    private final String direction;
    private final Item weaponItem;
    private final Weapon weapon;
    private final Coatable coatable;

    /**
     * Creates an attack against a hostile creature.
     * @param target hostile creature being attacked
     * @param direction direction to the target, for display
     * @param weaponItem carried item used as the weapon
     * @param weapon attack behavior supplied by the item
     * @param coatable coating behavior supplied by the item
     */
    public SwordAttackAction(HostileCreature target, String direction, Item weaponItem, Weapon weapon, Coatable coatable) {
        this.target = target;
        this.direction = direction;
        this.weaponItem = weaponItem;
        this.weapon = weapon;
        this.coatable = coatable;
    }

    /** Resolves the 20-damage, 85%-accurate strike and any active coating. */
    @Override public String execute(Actor actor, GameMap map) {
        String result = weapon.attack(actor, target, map);
        Location targetLocation = map.locationOf(target);
        Coating coating = coatable.getCoating();
        if (coating != null) result += coating.apply(actor, target, map);
        boolean killed = !target.isConscious();
        target.onAttacked(targetLocation, killed);
        if (killed) result += "\n" + target.unconscious(actor, map);
        return result;
    }
    /** Describes the attack in the action menu. */
    @Override public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction + " with " + weaponItem;
    }
}
