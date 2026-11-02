package game.questboard;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.quest.QuestDefinition;
import game.quest.QuestManager;

import java.util.Objects;
import java.util.Random;

/**
 * Runs a three-round push-your-luck challenge for one selected unaffordable
 * contract. The quest manager still owns quest creation, fees, and rewards.
 */
public class DiceWager {
    private static final int ROUNDS = 3;
    private static final int TARGET_SCORE = 12;
    private static final int LOSS_STAMINA = 4;
    private static final int COOLDOWN_TURNS = 5;

    private final QuestManager quests;
    private final Random random;
    private Actor contender;
    private GameMap farm;
    private QuestDefinition chosenContract;
    private int roundsPlayed;
    private int points;
    private int cooldown;


    /**
     * Creates a dice challenge with a newly generated random source.
     *
     * @param quests quest manager that owns the available contracts
     */
    public DiceWager(QuestManager quests) {

        this(quests, new Random());
    }

    /**
     * Creates a dice challenge with an injected random generator for repeatable tests.
     *
     * @param quests quest manager that owns the available contracts
     * @param random random generator used for dice rolls and deterministic tests
     */
    public DiceWager(QuestManager quests, Random random) {
        this.quests = Objects.requireNonNull(quests, "quests");
        this.random = Objects.requireNonNull(random, "random");
    }

    /**
     * Checks whether a contestant currently has an active wager.
     *
     * @return  true if a dice wager is active
     */
    public boolean inProgress() {
        return contender != null;
    }
    /**
     * Checks whether the actor may wager for the selected unaffordable contract.
     *
     * @param actor prospective contestant
     * @param map map containing the Quest Board
     * @param contract selected quest definition
     * @return true if the contestant may start a wager for this contract
     */
    public boolean canChallenge(Actor actor, GameMap map, QuestDefinition contract) {
        return !inProgress() && cooldown == 0 && !quests.hasActiveQuest() && quests.canInteractAtBoard(actor, map) && contract != null && quests.contracts().contains(contract) && actor.getStatistic(FinanceStatistics.COINS) < contract.entryFee();
    }

    /**
     * Starts a three-round wager for a particular quest without charging entry fees.
     *
     * @param actor contestant starting the wager
     * @param map map where the wager is initiated
     * @param contract selected quest definition
     * @return message describing whether the wager started
     */
    public String start(Actor actor, GameMap map, QuestDefinition contract) {
        if (!canChallenge(actor, map, contract)) {
            return "Dice challenge unavailable: stand beside the board, select an " + "unaffordable contract, and finish any cooldown.";
        }
        contender = actor;
        farm = map;
        chosenContract = contract;
        roundsPlayed = 0;
        points = 0;
        return "Dice challenge for " + contract.title() + ": score at least " + TARGET_SCORE + " points in " + ROUNDS + " rolls to enter free. " + "Each round choose Safe (d6, never bust) or Risky (d10, " + "1-2 means instant failure). Losing or forfeiting costs " + LOSS_STAMINA + " stamina. The chosen quest is the prize.";
    }

    /**
     * Processes a dice choice, awarding free quest entry on a successful final round.
     *
     * @param actor contestant performing the dice roll
     * @param map map where the contestant started the wager
     * @param choice dice choice selected by the contestant
     * @return message describing the dice result and any quest reward or penalty
     */
    public String play(Actor actor, GameMap map, DiceChoice choice) {
        if (!inProgress() || actor != contender || map != farm || !quests.canInteractAtBoard(actor, map) || quests.hasActiveQuest() || choice == null) {
            return "No active dice challenge here.";
        }
        if (choice == DiceChoice.FORFEIT) {
            return fail("You forfeited the dice challenge.");
        }

        int roll = 1 + random.nextInt(choice.sides());
        roundsPlayed++;
        if (roll <= choice.bustAtOrBelow()) {
            return fail("Round " + roundsPlayed + ": rolled " + roll + " on the risky die — bust!");
        }
        points += roll;
        String summary = "Round " + roundsPlayed + "/" + ROUNDS + ": " + choice.name() + " rolled " + roll + ". Total " + points + "/" + TARGET_SCORE + ". ";
        if (roundsPlayed < ROUNDS) {
            return summary + "Choose your next die at the quest board.";
        }
        if (points < TARGET_SCORE) {
            return fail(summary + "Not enough points.");
        }
        QuestDefinition prize = chosenContract;
        String result = quests.acceptDicePrize(actor, map, prize);
        if (!quests.hasActiveQuest()) {
            resetAttempt(false);
            return summary + "You reached the target, but the arena could not be " + "prepared: " + result + " No penalty; try again.";
        }
        resetAttempt(true);
        return summary + "You won free entry to " + prize.title() + "! " + result;
    }

    /**
     * Advances wager cooldown and forfeits a wager if the contestant leaves the board.
     *
     * @return forfeit message if the contestant has left, otherwise {@code null}
     */
    public String tick() {
        if (cooldown > 0) cooldown--;
        if (inProgress() && !quests.canInteractAtBoard(contender, farm)) {
            return fail("Leaving the quest board forfeited the dice challenge.");
        }
        return null;
    }

    /**
     * Applies the stamina penalty and cooldown, then clears the current wager.
     *
     * @param reason message explaining why the wager failed
     * @return message describing the loss and stamina penalty
     */
    private String fail(String reason) {
        contender.modifyStatistic(ActorStatistics.STAMINA,
                StatisticOperations.DECREASE, LOSS_STAMINA);
        resetAttempt(true);
        return reason + " Lost " + LOSS_STAMINA + " stamina; try again after "
                + COOLDOWN_TURNS + " turns.";
    }

    /**
     * Clears wager state and optionally begins its cooldown.
     *
     * @param applyCooldown whether to start the post-wager cooldown
     */
    private void resetAttempt(boolean applyCooldown) {
        contender = null;
        farm = null;
        chosenContract = null;
        roundsPlayed = 0;
        points = 0;
        cooldown = applyCooldown ? COOLDOWN_TURNS : 0;
    }

    /**
     * Returns the number of turns remaining before another dice
     * challenge can be attempted.
     *
     * @return remaining cooldown turns
     */
    public int cooldownTurns() {
        return cooldown;
    }
}
