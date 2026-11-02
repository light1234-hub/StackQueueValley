package game.questboard;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.req3tests.QuestDefinition;

import java.util.Objects;

/** Generic action that starts a dice challenge for a selected quest. */
public class StartDiceWagerAction extends Action {
    private final DiceWager wager;
    private final QuestDefinition contract;

    /**
     * Creates one action instance for the indicated contract.
     *
     * @param wager dice challenge whose state is controlled by this action
     * @param contract selected quest definition
     */
    public StartDiceWagerAction(DiceWager wager, QuestDefinition contract) {
        this.wager = Objects.requireNonNull(wager, "wager");
        this.contract = Objects.requireNonNull(contract, "contract");
    }

    /**
     * Starts the dice challenge for the selected contract.
     *
     * @param actor actor performing the operation
     * @param map current game map or arena map
     * @return message describing the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return wager.start(actor, map, contract);
    }

    /**
     * Returns the description shown for this action in the Farmer menu.
     *
     * @param actor actor performing the operation
     * @return text shown for this action in the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Challenge the dice for " + contract.title() + " (entry fee " + contract.entryFee() + " coins waived if you win)";
    }
}