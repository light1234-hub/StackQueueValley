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

    /**
     * Creates an impassable Quest Board linked to shared quest and dice services.
     *
     * @param quests quest manager that owns the available contracts
     * @param wager dice challenge whose state is controlled by this action
     * @param farmer Farmer participating in the quest system
     */
    public QuestBoardGround(QuestManager quests, DiceWager wager, Farmer farmer) {
        super('+', "Quest Board");
        this.quests = Objects.requireNonNull(quests, "quests");
        this.wager = Objects.requireNonNull(wager, "wager");
        this.farmer = Objects.requireNonNull(farmer, "farmer");
    }

    /**
     * Prevents actors from walking onto the Quest Board tile.
     *
     * @param actor actor performing the operation
     * @return {@code false}, because actors cannot occupy this ground
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Offers contract or dice actions when the Farmer stands next to the board.
     *
     * @param actor actor performing the operation
     * @param here location containing this ground
     * @param direction direction from the actor to this ground
     * @return actions available to the actor at this location
     */
    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList actions = super.allowableActions(actor, here, direction);
        if (direction.isEmpty() || !quests.canInteractAtBoard(actor, here.map()) || quests.hasActiveQuest()) {
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

    /**
     * Advances the wager timer and closes a stale interaction when needed.
     *
     * @param location tile containing the Quest Board
     */
    @Override
    public void tick(Location location) {
        String message = wager.tick();
        if (message != null) {
            farmer.endConversation();
            System.out.println(message);
        }
    }
}