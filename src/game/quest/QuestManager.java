package game.quest;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.TavernAbility;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.arena.ArenaQuest;
import game.terrain.Soil;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates paid quest acceptance, objective progress, deadlines, rewards,
 * failure consequences and safe return to the Tavern.
 * The manager depends on QuestScenario and QuestConsequence abstractions.
 */
public class QuestManager {
    private final Farmer farmer;
    private final ArenaQuest arena;
    private final Location tavernArrival;
    private final Location bartenderLocation;
    private final QuestOutcomeService outcomeService;
    private final List<QuestDefinition> contracts;
    private final List<QuestObjectiveGround> objectives = new ArrayList<>();
    private QuestDefinition active;
    private int completed;
    private int turnsRemaining;

    /**
     * Creates one quest service for the Farmer and a single reusable arena.
     *
     * @param farmer the player who can sign contracts
     * @param arena reusable arena to configure for each contract
     * @param tavernArrival safe return location
     * @param bartenderLocation interaction position of the bartender
     * @param farm persistent overworld changed by quest outcomes
     * @param contracts immutable list of offered contracts
     */
    public QuestManager(Farmer farmer, ArenaQuest arena, Location tavernArrival,
                        Location bartenderLocation, GameMap farm, List<QuestDefinition> contracts) {
        this.farmer = farmer;
        this.arena = arena;
        this.tavernArrival = tavernArrival;
        this.bartenderLocation = bartenderLocation;
        this.outcomeService = new QuestOutcomeService(farmer, new QuestWorldContext(farm));
        this.contracts = List.copyOf(contracts);
    }

    /** @return the available contract definitions */
    public List<QuestDefinition> contracts() { return contracts; }

    /** @return true when the Farmer has an unfinished contract */
    public boolean hasActiveQuest() { return active != null; }

    /** @return title of the active quest, or a suitable fallback */
    public String activeTitle() { return active == null ? "No active contract" : active.title(); }

    /** @return time remaining on the active contract */
    public int turnsRemaining() { return turnsRemaining; }

    /** @return number of finished objectives in the active quest */
    public int completedObjectives() { return completed; }

    /** @return total number of objectives in the active quest */
    public int totalObjectives() { return objectives.size(); }

    /**
     * Checks whether the supplied actor is the Farmer in the active quest arena.
     *
     * @param actor actor whose participation is being checked
     * @param map current map
     * @return true if this Farmer has an active quest on the supplied arena map
     */
    public boolean isActiveFor(Actor actor, GameMap map) {
        return active != null && actor == farmer && map == arena.map() && arena.map().contains(farmer);
    }

    /**
     * Checks whether an objective is still part of the active contract and remains in place.
     *
     * @param actor actor requesting the interaction
     * @param objective objective being accessed
     * @return true if the Farmer can interact with the active objective
     */
    public boolean canInteract(Actor actor, QuestObjectiveGround objective) {
        return active != null && actor == farmer && objectives.contains(objective)
                && objective.location().getGround() == objective;
    }

    /**
     * Checks that this actor is the Farmer, has Tavern permission and is next to
     * the bartender on the correct map. Stale menu actions must also pass this.
     *
     * @param actor the actor attempting to interact
     * @param map the map where the interaction is attempted
     * @return true only for the authorised adjacent Farmer
     */
    public boolean canInteractAtBar(Actor actor, GameMap map) {
        return actor == farmer && actor.hasAbility(TavernAbility.CAN_USE_TAVERN)
                && map != null && map.contains(actor) && nearBartender(map, actor);
    }

    /**
     * Attempts to buy a contract, charging only after arena preparation succeeds.
     *
     * @param actor the actor signing the contract
     * @param map the current Tavern map
     * @param contract the desired contract
     * @return a message reporting acceptance or rejection
     */
    public String accept(Actor actor, GameMap map, QuestDefinition contract) {
        return beginContract(actor, map, contract, false);
    }

    /**
     * Starts a contract without charging an entry fee. Only the drinking contest
     * should call this method after winning the wager; all normal validations
     * (location, ownership, available arena) still apply.
     *
     * @param actor the winner of the wager
     * @param map the current Tavern map
     * @param contract the randomly assigned contract
     * @return a message reporting acceptance or rejection
     */
    public String acceptWagerPrize(Actor actor, GameMap map, QuestDefinition contract) {
        return beginContract(actor, map, contract, true);
    }

    /**
     * Validates and prepares either a paid contract or a free wager prize.
     * The entry fee is charged only after the scenario has prepared successfully.
     *
     * @param actor actor accepting the contract
     * @param map current Tavern map
     * @param contract selected offered contract
     * @param freeEntry whether payment is waived
     * @return message describing acceptance or rejection
     */
    private String beginContract(Actor actor, GameMap map, QuestDefinition contract,
                                 boolean freeEntry) {
        if (!canInteractAtBar(actor, map)) {
            return "You must stand beside the bartender to accept a contract.";
        }
        if (active != null) return "Finish your current contract before taking another.";
        if (!contracts.contains(contract)) return "The bartender does not offer that contract.";
        if (!freeEntry && actor.getStatistic(FinanceStatistics.COINS) < contract.entryFee()) {
            return "Not enough coins for " + contract.title() + " (fee " + contract.entryFee() + ").";
        }
        Location entrance = arena.map().at(3, 3);
        if (entrance.containsAnActor() || !entrance.canActorEnter(actor)) {
            return "The Quest Arena entrance is blocked.";
        }
        try {
            arena.reset();
            active = contract;
            completed = 0;
            turnsRemaining = contract.turnLimit();
            objectives.clear();
            QuestSetupContext setup = new QuestSetupContext(arena,
                    (location, name) -> addObjective(location.x(), location.y(), name));
            contract.scenario().prepare(setup);
            if (objectives.isEmpty()) throw new IllegalStateException("Quest has no objectives");
            if (!entrance.canActorEnter(actor) || entrance.containsAnActor()) {
                throw new IllegalStateException("Quest entrance changed during setup");
            }
            requireGround(13, 1, new QuestClockGround(this));
        } catch (RuntimeException failure) {
            active = null;
            objectives.clear();
            arena.reset();
            return "Cannot prepare contract: " + failure.getMessage();
        }
        if (!freeEntry) {
            actor.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.DECREASE,
                    contract.entryFee());
        }
        map.moveActor(actor, entrance);
        String entry = freeEntry ? " won a free wager entry to "
                : " paid " + contract.entryFee() + " coins for ";
        return actor + entry + contract.title()
                + ". Objectives: " + objectives.size() + ". Time limit: "
                + contract.turnLimit() + " turns. Use the Quest Journal to abandon.";
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
        if (active == null || !arena.inside(x, y)) {
            throw new IllegalStateException("Objective cannot be created here");
        }
        Location location = arena.map().at(x, y);
        QuestObjectiveGround objective = new QuestObjectiveGround(this, location, name);
        requireGround(x, y, objective);
        objectives.add(objective);
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
     * The final objective is retained if the Tavern has no safe return tile.
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
        Location current = map.locationOf(actor);
        Location target = objective.location();
        if (!adjacent(current, target)) return "Move beside the objective to activate it.";
        int index = objectives.indexOf(objective);
        if (active.orderedObjectives() && index != completed) {
            return "The relic sigils must be activated in sequence: " + (completed + 1)
                    + " comes next.";
        }
        if (completed + 1 == objectives.size() && safeReturnLocation() == null) {
            return "Cannot complete the final objective yet because every Tavern return tile is blocked. "
                    + "The final objective remains available; clear a safe tile and try again.";
        }
        target.setGround(new Soil());
        completed++;
        if (completed == objectives.size()) {
            return "All objectives completed! " + finish(true);
        }
        return "Objective completed (" + completed + "/" + objectives.size() + "). "
                + turnsRemaining + " turns remaining.";
    }

    /**
     * Advances the mission deadline once per game-world turn.
     * Only time spent actively inside the arena counts. Expiry invokes the failure path.
     */
    public void tick() {
        if (active == null || !arena.map().contains(farmer)) return;
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            System.out.println("Bartender: Time's up! " + finish(false));
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
        if (!isActiveFor(actor, map)) return "There is no active quest to abandon.";
        return "Contract abandoned. " + finish(false);
    }

    /**
     * Returns the Farmer to a safe Tavern tile, applies the quest outcome, and resets the arena.
     *
     * @param successful whether the Farmer completed every objective in time
     * @return message describing the completed or failed quest, or a blocked return
     */
    private String finish(boolean successful) {
        QuestDefinition finished = active;
        // Try the normal arrival first, then neighbouring and other Tavern tiles.
        Location returnTile = safeReturnLocation();
        if (returnTile == null) {
            return "Cannot return to Tavern: arrival is blocked. Your contract remains active.";
        }
        arena.map().moveActor(farmer, returnTile);
        String outcome = outcomeService.apply(finished, successful);
        active = null;
        completed = 0;
        turnsRemaining = 0;
        objectives.clear();
        arena.reset();
        return finished.title() + (successful ? " succeeded." : " failed.")
                + outcome + " Returned to the Tavern.";
    }

    /**
     * Finds a safe return tile without moving another Tavern occupant.
     * The usual arrival and neighbouring tiles are preferred; the full Tavern is
     * searched as a fallback so quests can complete even if the entrance is blocked.
     *
     * @return a free enterable Tavern tile, or null if none exists
     */
    private Location safeReturnLocation() {
        if (isSafeTavernTile(tavernArrival)) {
            return tavernArrival;
        }
        for (Exit exit : tavernArrival.getExits()) {
            Location tile = exit.getDestination();
            if (isSafeTavernTile(tile)) {
                return tile;
            }
        }
        GameMap tavern = tavernArrival.map();
        for (int y : tavern.getYRange()) {
            for (int x : tavern.getXRange()) {
                Location tile = tavern.at(x, y);
                if (isSafeTavernTile(tile)) {
                    return tile;
                }
            }
        }
        return null;
    }

    /**
     * Checks whether a Tavern tile is a valid return destination for the Farmer.
     *
     * @param tile candidate Tavern tile
     * @return true if it is empty and enterable by the Farmer
     */
    private boolean isSafeTavernTile(Location tile) {
        return tile != null && !tile.containsAnActor() && tile.canActorEnter(farmer);
    }

    /**
     * Checks whether an actor is adjacent to the bartender on the supplied map.
     *
     * @param map map where the interaction occurs
     * @param actor actor interacting with the bartender
     * @return true if the actor is adjacent on the bartender's map
     */
    private boolean nearBartender(GameMap map, Actor actor) {
        if (bartenderLocation.map() != map) return false;
        return adjacent(map.locationOf(actor), bartenderLocation);
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
}



