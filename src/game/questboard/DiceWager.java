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

    /** Creates a dice challenge using fresh randomness. */
    public DiceWager(QuestManager quests) {
        this(quests, new Random());
    }

    /** Injects randomness for deterministic automated tests. */
    public DiceWager(QuestManager quests, Random random) {
        this.quests = Objects.requireNonNull(quests, "quests");
        this.random = Objects.requireNonNull(random, "random");
    }

    /** @return whether the player is mid-challenge */
    public boolean inProgress() { return contender != null; }

    /** @return remaining cooldown in farm turns */
    public int cooldownTurns() { return cooldown; }

    /** @return total points rolled during the active challenge */
    public int points() { return points; }

    /** @return number of rolls completed */
    public int roundsPlayed() { return roundsPlayed; }

    /** @return selected contract, or null if there is no active challenge */
    public QuestDefinition selectedContract() { return chosenContract; }

    /**
     * Offers the challenge only for a selected contract that this Farmer cannot
     * afford, while standing adjacent to the board and off cooldown.
     */
    public boolean canChallenge(Actor actor, GameMap map, QuestDefinition contract) {
        return !inProgress() && cooldown == 0 && !quests.hasActiveQuest()
                && quests.canInteractAtBoard(actor, map)
                && contract != null && quests.contracts().contains(contract)
                && actor.getStatistic(FinanceStatistics.COINS) < contract.entryFee();
    }

    /** Starts a new challenge without charging coins. */
    public String start(Actor actor, GameMap map, QuestDefinition contract) {
        if (!canChallenge(actor, map, contract)) {
            return "Dice challenge unavailable: stand beside the board, select an "
                    + "unaffordable contract, and finish any cooldown.";
        }
        contender = actor;
        farm = map;
        chosenContract = contract;
        roundsPlayed = 0;
        points = 0;
        return "Dice challenge for " + contract.title() + ": score at least "
                + TARGET_SCORE + " points in " + ROUNDS + " rolls to enter free. "
                + "Each round choose Safe (d6, never bust) or Risky (d10, "
                + "1-2 means instant failure). Losing or forfeiting costs "
                + LOSS_STAMINA + " stamina. The chosen quest is the prize.";
    }

    /**
     * Rolls a die for the active contender. The final win grants the selected
     * quest, preserving normal quest setup/validation without taking coins.
     */
    public String play(Actor actor, GameMap map, DiceChoice choice) {
        if (!inProgress() || actor != contender || map != farm
                || !quests.canInteractAtBoard(actor, map) || quests.hasActiveQuest()
                || choice == null) {
            return "No active dice challenge here.";
        }
        if (choice == DiceChoice.FORFEIT) {
            return fail("You forfeited the dice challenge.");
        }

        int roll = 1 + random.nextInt(choice.sides());
        roundsPlayed++;
        if (roll <= choice.bustAtOrBelow()) {
            return fail("Round " + roundsPlayed + ": rolled " + roll
                    + " on the risky die — bust!");
        }
        points += roll;
        String summary = "Round " + roundsPlayed + "/" + ROUNDS + ": "
                + choice.name() + " rolled " + roll + ". Total "
                + points + "/" + TARGET_SCORE + ". ";

        if (roundsPlayed < ROUNDS) {
            return summary + "Choose your next die at the quest board.";
        }
        if (points < TARGET_SCORE) {
            return fail(summary + "Not enough points.");
        }
        QuestDefinition prize = chosenContract;
        String result = quests.acceptDicePrize(actor, map, prize);
        if (!quests.hasActiveQuest()) {
            // Technical errors (e.g., a blocked arena) must not take the prize.
            resetAttempt(false);
            return summary + "You reached the target, but the arena could not be "
                    + "prepared: " + result + " No penalty; try again.";
        }
        resetAttempt(true);
        return summary + "You won free entry to " + prize.title() + "! " + result;
    }

    /**
     * Called by the board once each world turn to progress cooldown and avoid
     * trapping the Farmer in a stale choice menu if moved away by game effects.
     */
    public String tick() {
        if (cooldown > 0) cooldown--;
        if (inProgress() && !quests.canInteractAtBoard(contender, farm)) {
            return fail("Leaving the quest board forfeited the dice challenge.");
        }
        return null;
    }

    private String fail(String reason) {
        contender.modifyStatistic(ActorStatistics.STAMINA,
                StatisticOperations.DECREASE, LOSS_STAMINA);
        resetAttempt(true);
        return reason + " Lost " + LOSS_STAMINA + " stamina; try again after "
                + COOLDOWN_TURNS + " turns.";
    }

    /** Resets per-attempt state and optionally sets a cooldown. */
    private void resetAttempt(boolean applyCooldown) {
        contender = null;
        farm = null;
        chosenContract = null;
        roundsPlayed = 0;
        points = 0;
        cooldown = applyCooldown ? COOLDOWN_TURNS : 0;
    }
}
