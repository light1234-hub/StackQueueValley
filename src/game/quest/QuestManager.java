package game.quest;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.arena.ArenaQuest;
import game.terrain.Soil;

import java.util.List;

/**
 * Coordinates quest acceptance, objective progress, deadlines, rewards,
 * failure consequences and safe return to the inherited farm.
 * The manager depends on QuestScenario and QuestConsequence abstractions.
 */
public class QuestManager {
    private final Farmer farmer;
    private final ArenaQuest arena;
    private final Location farmReturn;
    private final Location boardLocation;
    private final QuestOutcomeService outcomeService;
    private final List<QuestDefinition> contracts;
    private QuestSession session;
    private boolean awaitingReturn;

    /**
     * Creates one quest service for the Farmer and a single reusable arena.
     *
     * @param farmer the player who can sign contracts
     * @param arena reusable arena to configure for each contract
     * @param farmReturn safe return location on the inherited farm
     * @param boardLocation the position of the farm quest board
     * @param farm persistent overworld changed by quest outcomes
     * @param contracts immutable list of offered contracts
     */
    public QuestManager(Farmer farmer, ArenaQuest arena, Location farmReturn,
                        Location boardLocation, GameMap farm, List<QuestDefinition> contracts) {
        this.farmer = farmer;
        this.arena = arena;
        this.farmReturn = farmReturn;
        this.boardLocation = boardLocation;
        this.outcomeService = new QuestOutcomeService(farmer, new QuestWorldContext(farm));
        this.contracts = List.copyOf(contracts);
    }

    /**
     * Returns the contracts offered by the quest board.
     *
     * @return unmodifiable list of offered quest definitions
     */
    public List<QuestDefinition> contracts() { return contracts; }

    /**
     * Checks whether a contract session exists.
     *
     * @return true when a quest session exists
     */
    public boolean hasActiveQuest() { return session != null; }

    /**
     * Checks whether the supplied actor is the Farmer in the active quest arena.
     *
     * @param actor actor whose participation is being checked
     * @param map current map
     * @return true if this Farmer has an active quest on the supplied arena map
     */
    public boolean isActiveFor(Actor actor, GameMap map) {
        return session != null && actor == farmer && map == arena.map() && arena.map().contains(farmer);
    }

    /**
     * Checks whether an objective is still part of the active contract and remains in place.
     *
     * @param actor actor requesting the interaction
     * @param objective objective being accessed
     * @return true if the Farmer can interact with the active objective
     */
    public boolean canInteract(Actor actor, QuestObjectiveGround objective) {
        return session != null && !awaitingReturn && !session.hasExpired() && actor == farmer && session.contains(objective) && objective.location().getGround() == objective;
    }

    /**
     * Allows only the participating Farmer to interact beside the quest board
     * on the inherited farm. Also revalidates old menu actions when executed.
     */
    public boolean canInteractAtBoard(Actor actor, GameMap map) {
        return actor == farmer && map != null && boardLocation.map() == map && map.contains(actor) && adjacent(map.locationOf(actor), boardLocation);
    }

    /**
     * Attempts to buy a contract, charging only after arena preparation succeeds.
     *
     * @param actor the actor signing the contract
     * @param map the current farm map
     * @param contract the desired contract
     * @return a message reporting acceptance or rejection
     */
    public String accept(Actor actor, GameMap map, QuestDefinition contract) {
        return beginContract(actor, map, contract, false);
    }

    /**
     * Starts the selected contract without an entry fee after a won dice wager.
     * All normal location, ownership, and arena validations still apply.
     *
     * @param actor the winner of the wager
     * @param map the current farm map
     * @param contract the randomly assigned contract
     * @return a message reporting acceptance or rejection
     */
    public String acceptDicePrize(Actor actor, GameMap map, QuestDefinition contract) {
        return beginContract(actor, map, contract, true);
    }

    /**
     * Validates and prepares either a paid contract or a free dice prize.
     * The entry fee is charged only after the scenario has prepared successfully.
     *
     * @param actor actor accepting the contract
     * @param map current farm map
     * @param contract selected offered contract
     * @param freeEntry whether payment is waived
     * @return message describing acceptance or rejection
     */
    private String beginContract(Actor actor, GameMap map, QuestDefinition contract, boolean freeEntry) {
        if (!canInteractAtBoard(actor, map)) {
            return "You must stand beside the quest board to accept a contract.";
        }
        if (session != null) {
            return "Finish your current contract before taking another.";
        }
        if (!contracts.contains(contract)) {
            return "The quest board does not offer that contract.";
        }
        if (!freeEntry && actor.getStatistic(FinanceStatistics.COINS) < contract.entryFee()) {
            return "Not enough coins for " + contract.title() + " (fee " + contract.entryFee() + ").";
        }
        Location entrance = arena.entrance();
        if (entrance.containsAnActor() || !entrance.canActorEnter(actor)) {
            return "The Quest Arena entrance is blocked.";
        }
        try {
            arena.reset();
            session = new QuestSession(contract);
            QuestSetupContext setup = new QuestSetupContext(arena, (location, name) -> addObjective(location.x(), location.y(), name));
            contract.scenario().prepare(setup);
            if (session.totalObjectives() == 0) {
                throw new IllegalStateException("Quest has no objectives");
            }
            if (!entrance.canActorEnter(actor) || entrance.containsAnActor()) {
                throw new IllegalStateException("Quest entrance changed during setup");
            }
            if (!arena.placeClock(new QuestClockGround(this))) {
                throw new IllegalStateException("Cannot prepare arena clock");
            }
        } catch (RuntimeException failure) {
            session = null;
            arena.reset();
            return "Cannot prepare contract: " + failure.getMessage();
        }
        if (!freeEntry) {
            actor.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.DECREASE,
                    contract.entryFee());
        }
        map.moveActor(actor, entrance);
        awaitingReturn = false;
        String entry = freeEntry ? " won free dice-challenge entry to " : " paid " + contract.entryFee() + " coins for ";
        return actor + entry + contract.title() + ". Objectives: " + session.totalObjectives() + ". Time limit: " + contract.turnLimit() + " turns. Use the Quest Journal to abandon.";
    }

    /**
     * Adds a named objective at the requested coordinates while preparing a quest.
     * A failed placement rejects the entire contract before payment.
     *
     * @param x horizontal arena coordinate
     * @param y vertical arena coordinate
     * @param name text identifying the objective
     * @throws IllegalStateException if no contract is being prepared or placement fails
     */
    public void addObjective(int x, int y, String name) {
        if (session == null || !arena.inside(x, y)) {
            throw new IllegalStateException("Objective cannot be created here");
        }
        Location location = arena.map().at(x, y);
        QuestObjectiveGround objective = new QuestObjectiveGround(this, location, name);
        requireGround(x, y, objective);
        session.addObjective(objective);
    }

    /**
     * Makes a terrain change required for the scenario, rejecting failed setups.
     *
     * @param x horizontal arena coordinate
     * @param y vertical arena coordinate
     * @param ground ground to place
     * @throws IllegalStateException if the tile cannot be changed
     */
    public void requireGround(int x, int y, Ground ground) {
        if (!arena.placeGround(x, y, ground)) {
            throw new IllegalStateException("Cannot prepare arena tile " + x + "," + y);
        }
    }

    /**
     * Activates a nearby objective and finishes the quest when all goals are complete.
     * Ordered contracts require the objectives to be visited in their listed order.
     * The final objective is retained if the farm has no safe return tile.
     *
     * @param actor actor attempting to activate the objective
     * @param map current map
     * @param objective objective being activated
     * @return message describing progress, rejection, or the quest outcome
     */
    public String activate(Actor actor, GameMap map, QuestObjectiveGround objective) {
        if (!isActiveFor(actor, map) || !canInteract(actor, objective)) {
            return "The objective is no longer available.";
        }
        if (awaitingReturn || session.hasExpired()) {
            return "The contract has ended. Waiting to return to the farm.";
        }
        Location current = map.locationOf(actor);
        Location target = objective.location();
        if (!adjacent(current, target)) {
            return "Move beside the objective to activate it.";
        }
        if (!session.isNextObjective(objective)) {
            return "The relic sigils must be activated in sequence: " + (session.completedObjectives() + 1) + " comes next.";
        }
        if (session.isFinalObjective() && safeReturnLocation() == null) {
            return "Cannot complete the final objective yet because every farm return tile is blocked. " + "The final objective remains available; clear a safe tile and try again.";
        }
        target.setGround(new Soil());
        session.completeObjective();
        if (session.isComplete()) {
            return "All objectives completed! " + finish(true);
        }
        return "Objective completed (" + session.completedObjectives() + "/" + session.totalObjectives() + "). " + session.turnsRemaining() + " turns remaining.";
    }

    /**
     * Advances the mission deadline once per game-world turn.
     * Only time spent actively inside the arena counts. Expiry invokes the failure path.
     */
    public void tick() {
        if (session == null || !arena.map().contains(farmer)) {
            return;
        }
        if (awaitingReturn) {
            if (safeReturnLocation() != null) {
                System.out.println("Quest board: " + finish(false));
            }
            return;
        }
        session.tick();
        if (session.hasExpired()) {
            System.out.println("Quest board: Time's up! " + finish(false));
        }
    }

    /**
     * Abandons the active quest and applies its failure consequence.
     *
     * @param actor actor requesting abandonment
     * @param map current map
     * @return message describing the result
     */
    public String abandon(Actor actor, GameMap map) {
        if (!isActiveFor(actor, map)) {
            return "There is no active quest to abandon.";
        }

        return "Contract abandoned. " + finish(false);
    }

    /**
     * Returns the Farmer to a safe farm tile, applies the quest outcome, and resets the arena.
     *
     * @param successful whether the Farmer completed every objective in time
     * @return message describing the completed or failed quest, or a blocked return
     */
    private String finish(boolean successful) {
        QuestDefinition finished = session.definition();
        // Try the normal return location, then neighbouring and other farm tiles.
        Location returnTile = safeReturnLocation();
        if (returnTile == null) {
            awaitingReturn = true;
            return "All farm return tiles are blocked. The contract has ended; "
                    + "return will be retried automatically when a tile becomes free.";
        }
        arena.map().moveActor(farmer, returnTile);
        String outcome = outcomeService.apply(finished, successful);
        session = null;
        awaitingReturn = false;
        arena.reset();
        return finished.title() + (successful ? " succeeded." : " failed.")
                + outcome + " Returned to the farm.";
    }

    /**
     * Finds a safe return tile without moving another farm occupant.
     * The usual arrival and neighbouring tiles are preferred; the full farm is
     * searched as a fallback so quests can complete even if the entrance is blocked.
     *
     * @return a free enterable farm tile, or null if none exists
     */
    private Location safeReturnLocation() {
        if (isSafeFarmTile(farmReturn)) {
            return farmReturn;
        }
        for (Exit exit : farmReturn.getExits()) {
            Location tile = exit.getDestination();
            if (isSafeFarmTile(tile)) {
                return tile;
            }
        }
        GameMap farm = farmReturn.map();
        for (int y : farm.getYRange()) {
            for (int x : farm.getXRange()) {
                Location tile = farm.at(x, y);
                if (isSafeFarmTile(tile)) {
                    return tile;
                }
            }
        }
        return null;
    }

    /**
     * Checks whether a farm tile is a valid return destination for the Farmer.
     *
     * @param tile candidate farm tile
     * @return true if it is empty and enterable by the Farmer
     */
    private boolean isSafeFarmTile(Location tile) {
        return tile != null && !tile.containsAnActor() && tile.canActorEnter(farmer);
    }

    /**
     * Uses eight-direction adjacency for locations on the same map.
     *
     * @param left first location
     * @param right second location
     * @return true when the locations are distinct neighbours on the same map
     */
    private static boolean adjacent(Location left, Location right) {
        return left.map() == right.map() &&
                Math.max(Math.abs(left.x() - right.x()), Math.abs(left.y() - right.y())) == 1;
    }

    /**
     * Returns the remaining turns for the active quest.
     *
     * @return remaining turns, or zero if no quest is active
     */
    public int turnsRemaining() {
        return session == null ? 0 : session.turnsRemaining();
    }

    /**
     * Returns the number of completed quest objectives.
     *
     * @return completed objectives, or zero if no quest is active
     */
    public int completedObjectives() {
        return session == null ? 0 : session.completedObjectives();
    }

    /**
     * Returns the total number of objectives in the active quest.
     *
     * @return total objectives, or zero if no quest is active
     */
    public int totalObjectives() {
        return session == null ? 0 : session.totalObjectives();
    }
}



