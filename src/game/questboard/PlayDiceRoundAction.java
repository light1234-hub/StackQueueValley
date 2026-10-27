package game.questboard;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Objects;

/** Generic action for any choice during a round of the dice challenge. */
public class PlayDiceRoundAction extends Action {
    private final DiceWager wager;
    private final DiceChoice choice;

    /** Chooses the wager and the die used by this menu action. */
    public PlayDiceRoundAction(DiceWager wager, DiceChoice choice) {
        this.wager = Objects.requireNonNull(wager, "wager");
        this.choice = Objects.requireNonNull(choice, "choice");
    }

    /** {@inheritDoc} */
    @Override
    public String execute(Actor actor, GameMap map) {
        return wager.play(actor, map, choice);
    }

    /** {@inheritDoc} */
    @Override
    public String menuDescription(Actor actor) {
        return choice.menuText();
    }
}