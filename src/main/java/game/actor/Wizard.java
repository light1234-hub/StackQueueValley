package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.farmeraction.RecipeInfoAction;
import game.item.AegisElixir;
import game.item.GaleDraught;
import game.item.ProspectorElixir;
import game.item.Potion;

import java.util.List;

/** Stationary NPC who explains each potion's recipe and how much XP the farmer still needs. */
public class Wizard extends Actor {
    private final List<Potion> potions = List.of(new GaleDraught(), new AegisElixir(), new ProspectorElixir());

    public Wizard() {
        super("Wizard", 'Y', 100);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        return new DoNothingAction();
    }

    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        for (Potion p : potions) {
            actions.add(new RecipeInfoAction(this, p));
        }
        return actions;
    }
}