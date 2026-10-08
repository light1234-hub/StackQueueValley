package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.blueprint.Coatable;
import game.blueprint.Coating;
import java.util.Random;

/** Portable weapon sold by the merchant and capable of holding one coating. */
public class Sword extends Item implements Coatable, Weapon
{
    private Coating coating;

    /** Creates an uncoated, portable sword. */
    public Sword()
    {
        super("Sword",'!');
        this.makePortable();
    }

    @Override
    public Coating getCoating() { return coating; }

    @Override
    public void setCoating(Coating coating) { this.coating = coating; }

    /** Performs the sword's 20-damage attack with an 85 percent hit chance. */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        if (new Random().nextInt(100) < 85) {
            target.hurt(20);
            return attacker + " hits " + target + " with the sword for 20 damage.";
        }
        return attacker + " misses " + target + " with the sword.";
    }
}
