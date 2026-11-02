package game.questboard;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Objects;

/** Generic action for any choice during a round of the dice challenge. */
public class PlayDiceRoundAction extends Action {
    private final DiceWager wager;
    private final DiceChoice choice;

    /**
     * Chooses the wager and the die used by this menu action.
     *
     * @param wager dice challenge whose state is controlled by this action
     * @param choice dice choice executed by this action
     */
    public PlayDiceRoundAction(DiceWager wager, DiceChoice choice) {
        this.wager = Objects.requireNonNull(wager, "wager");
        this.choice = Objects.requireNonNull(choice, "choice");
    }

    /**
     * Plays the selected dice choice in the current challenge.
     *
     * @param actor actor performing the operation
     * @param map current game map or arena map
     * @return message describing the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return wager.play(actor, map, choice);
    }

    /**
     * Returns the description shown for this action in the Farmer menu.
     *
     * @param actor actor performing the operation
     * @return text shown for this action in the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return choice.menuText();
    }
}