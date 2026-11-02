package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.Potion;

/** Asks the Wizard about one potion: ingredients, XP needed, brew time and XP still missing. */
public class RecipeInfoAction extends Action {
    private final Actor wizard;
    private final Potion potion;

    public RecipeInfoAction(Actor wizard, Potion potion) {
        this.wizard = wizard;
        this.potion = potion;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        int missing = Math.max(0, potion.getXpRequired() - Potion.currentXp(actor));
        return wizard + ": " + potion + " needs " + potion.describeIngredients()
                + "; requires " + potion.getXpRequired() + " XP; brews in " + potion.getBrewTurns()
                + " turns. You are missing " + missing + " XP.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " asks " + wizard + " about " + potion;
    }
}