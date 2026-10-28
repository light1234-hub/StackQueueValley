package game.quest;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.arena.ArenaQuest;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Provides quest scenarios with only the operations needed to prepare an arena.
 * Objective registration is delegated to the quest manager, without exposing the
 * manager itself to scenario implementations.
 */
public class QuestSetupContext {
    private final ArenaQuest arena;
    private final BiConsumer<Location, String> objectiveRegistrar;

    /**
     * Creates a setup context for one prepared quest arena.
     *
     * @param arena reusable quest arena being configured
     * @param objectiveRegistrar callback that registers an objective with the manager
     * @throws NullPointerException if either dependency is null
     */
    public QuestSetupContext(ArenaQuest arena,
                             BiConsumer<Location, String> objectiveRegistrar) {
        this.arena = Objects.requireNonNull(arena, "arena must not be null");
        this.objectiveRegistrar = Objects.requireNonNull(
                objectiveRegistrar, "objective registrar must not be null");
    }

    /**
     * Replaces a free arena tile with required scenario ground.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param ground ground to place
     * @throws IllegalStateException if the tile cannot be changed
     */
    public void requireGround(int x, int y, Ground ground) {
        if (!arena.placeGround(x, y, ground)) {
            throw new IllegalStateException("Cannot prepare arena tile " + x + "," + y);
        }
    }

    /**
     * Registers an objective at a valid arena coordinate.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param name objective label shown to the player
     * @throws IllegalStateException if the coordinate is invalid or the name is blank
     */
    public void addObjective(int x, int y, String name) {
        if (!arena.inside(x, y) || name == null || name.isBlank()) {
            throw new IllegalStateException("Objective cannot be created here");
        }
        objectiveRegistrar.accept(arena.map().at(x, y), name);
    }

    /**
     * Attempts to spawn an actor at a valid, enterable arena location.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param actor actor to spawn
     * @return true if the actor was spawned
     */
    public boolean spawnActor(int x, int y, Actor actor) {
        return arena.spawnActor(x, y, actor);
    }

    /**
     * Attempts to place an item within the arena.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param item item to place
     * @return true if the item was placed
     */
    public boolean placeItem(int x, int y, Item item) {
        return arena.placeItem(x, y, item);
    }
    /**
     * Places a required actor or rejects the whole quest setup.
     * Scenario classes do not need to repeat placement failure handling.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param actor actor that must be spawned
     * @throws IllegalStateException when the actor cannot be placed
     */
    public void requireActor(int x, int y, Actor actor) {
        if (!spawnActor(x, y, actor)) {
            throw new IllegalStateException("Cannot spawn required actor at " + x + "," + y);
        }
    }

    /**
     * Places a required item or rejects the whole quest setup.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param item item that must be present
     * @throws IllegalStateException when the item cannot be placed
     */
    public void requireItem(int x, int y, Item item) {
        if (!placeItem(x, y, item)) {
            throw new IllegalStateException("Cannot place required item at " + x + "," + y);
        }
    }

}
