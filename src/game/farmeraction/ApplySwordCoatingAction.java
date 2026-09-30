package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.Sword;

public class ApplySwordCoatingAction extends Action {
    private final Sword sword;
    private final Sword.Coating coating;
    private final String source;
    public ApplySwordCoatingAction(Sword sword, Sword.Coating coating, String source) {
        this.sword = sword; this.coating = coating; this.source = source;
    }
    @Override public String execute(Actor actor, GameMap map) {
        if (coating == Sword.Coating.MELON) {
            for (edu.monash.fit2099.engine.items.Item item : actor.getInventory().getItems()) {
                if (item instanceof game.item.MelonItem) {
                    actor.getInventory().remove(item);
                    break;
                }
            }
        }
        sword.setCoating(coating);
        return actor + " coats the sword with " + source + ".";
    }
    @Override public String menuDescription(Actor actor) { return actor + " coats Sword with " + source; }
}
