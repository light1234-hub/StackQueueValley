package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.AlchemyAbility;
import game.actor.CreatureControl;
import game.actor.HostileCreature;
import game.item.AegisElixir;
import game.item.Lure;

import java.util.ArrayList;

/** Brews Aegis Elixir; each brew turn drops a Lure that draws hostiles in and tames them. */
public class TamingCauldron extends BrewingStation {
    public TamingCauldron() {
        super('U', "Taming Cauldron", AegisElixir::new);
    }

    @Override
    protected void applyBrewEffect(Location location) {
        GameMap map = location.map();
        Location tile = TileFinder.randomEmptyTile(map);
        if (tile != null) {
            tile.addItem(new Lure());
        }
        for (Location l : TileFinder.all(map)) {
            Actor a = l.getActor();
            if (a != null) {
                a.asCapability(HostileCreature.class)
                        .filter(c -> !CreatureControl.isTamed(c))
                        .ifPresent(c -> CreatureControl.installLureSeeking(c, map));
            }
        }
    }

    /** Unused lures vanish on collect. */
    @Override
    protected void onCollected(Actor collector, Location location) {
        for (Location l : TileFinder.all(location.map())) {
            for (Item item : new ArrayList<>(l.getItems())) {
                if (item.hasAbility(AlchemyAbility.LURE)) {
                    l.removeItem(item);
                }
            }
        }
    }
}