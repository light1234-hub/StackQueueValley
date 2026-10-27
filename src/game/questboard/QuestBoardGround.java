package game.questboard;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.quest.AcceptContractAction;
import game.quest.QuestDefinition;
import game.quest.QuestManager;

import java.util.Objects;

/**
 * Farm furniture offering the same three generic quest contracts. If the
 * Farmer cannot afford a selected quest, the board offers a dice challenge.
 */
public class QuestBoardGround extends Ground {
    private final QuestManager quests;
    private final DiceWager wager;
    private final Farmer farmer;

    /** Constructs the board using shared services for quests and dice rounds. */
    public QuestBoardGround(QuestManager quests, DiceWager wager, Farmer farmer) {
        super('+', "Quest Board");
        this.quests = Objects.requireNonNull(quests, "quests");
        this.wager = Objects.requireNonNull(wager, "wager");
        this.farmer = Objects.requireNonNull(farmer, "farmer");
    }

    /** Quest board is inspected from a neighbouring farm tile. */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Builds one reusable action per offered contract, or the rolling choices
     * if a challenge is underway. The Farmer's temporary menu is restricted
     * during a wager and restored automatically when it ends.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList actions = super.allowableActions(actor, here, direction);
        if (direction.isEmpty() || !quests.canInteractAtBoard(actor, here.map())
                || quests.hasActiveQuest()) {
            return actions;
        }

        ActionList boardActions = new ActionList();
        if (wager.inProgress()) {
            for (DiceChoice choice : DiceChoice.values()) {
                boardActions.add(new PlayDiceRoundAction(wager, choice));
            }
            farmer.startConversation(boardActions, here.map());
        } else {
            farmer.endConversation();
            int balance = actor.getStatistic(FinanceStatistics.COINS);
            for (QuestDefinition contract : quests.contracts()) {
                if (balance >= contract.entryFee()) {
                    boardActions.add(new AcceptContractAction(quests, contract));
                } else if (wager.canChallenge(actor, here.map(), contract)) {
                    boardActions.add(new StartDiceWagerAction(wager, contract));
                }
            }
        }
        actions.add(boardActions);
        return actions;
    }

    /** One turn of dice cooldown; resets stale wagers if Farmer is displaced. */
    @Override
    public void tick(Location location) {
        String message = wager.tick();
        if (message != null) {
            farmer.endConversation();
            System.out.println(message);
        }
    }
}