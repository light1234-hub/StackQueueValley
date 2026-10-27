package game.questboard;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.quest.QuestDefinition;

import java.util.Objects;

/** Generic action that starts a dice challenge for a selected quest. */
public class StartDiceWagerAction extends Action {
    private final DiceWager wager;
    private final QuestDefinition contract;

    /** Creates one action instance for the indicated contract. */
    public StartDiceWagerAction(DiceWager wager, QuestDefinition contract) {
        this.wager = Objects.requireNonNull(wager, "wager");
        this.contract = Objects.requireNonNull(contract, "contract");
    }

    /** {@inheritDoc} */
    @Override
    public String execute(Actor actor, GameMap map) {
        return wager.start(actor, map, contract);
    }

    /** {@inheritDoc} */
    @Override
    public String menuDescription(Actor actor) {
        return "Challenge the dice for " + contract.title()
                + " (entry fee " + contract.entryFee() + " coins waived if you win)";
    }
}