package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.tool.Hoe;
import game.tool.WateringCan;
import game.farmeraction.ApplyCoatingAction;
import game.blueprint.Coatable;
import game.blueprint.CoatingSource;

import java.util.ArrayList;
import java.util.List;
/**
 * The player-controlled farmer who manages the farm and performs actions.
 */
public class Farmer extends Actor {
    /**
     * Creates a farmer with starting stamina and farming tools.
     *
     * @param name farmer's name
     */
    public Farmer(String name) {
        super("Farmer " + name, '@', 100);

        //init the the Stamina attribute
        this.addNewStatistic(ActorStatistics.STAMINA, new BaseStatistic(20));
        this.addNewStatistic(FinanceStatistics.COINS, new BaseStatistic(10000));

        // give farmer the Item
        this.getInventory().add(new Hoe());
        this.getInventory().add(new WateringCan());

        this.enableAbility(ZombieTarget.ZOMBIE_TARGET);

    }

    @Override
    /**
     * Handles the farmer's turn, including stamina regeneration and menu display.
     *
     * @param actions available actions
     * @param lastAction farmer's previous action
     * @param map current game map
     * @param display current display
     * @return the action selected for the turn
     */
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // this regen stamina
        this.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);

        addCoatingActions(actions, map);

        //Display menu
        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /** Adds coating actions for carried coatable items and carried or adjacent sources. */
    private void addCoatingActions(ActionList actions, GameMap map) {
        List<CoatingSourceReference> sources = new ArrayList<>();
        for (Item item : getInventory().getItems()) {
            if (item instanceof CoatingSource) {
                sources.add(new CoatingSourceReference((CoatingSource) item, item, null));
            }
        }

        Location here = map.locationOf(this);
        for (Exit exit : here.getExits()) {
            Location adjacent = exit.getDestination();
            for (Item item : adjacent.getItems()) {
                if (item instanceof CoatingSource) {
                    sources.add(new CoatingSourceReference((CoatingSource) item, item, adjacent));
                }
            }
            if (adjacent.getGround() instanceof CoatingSource) {
                sources.add(new CoatingSourceReference((CoatingSource) adjacent.getGround(), null, adjacent));
            }
            for (Status status : adjacent.getGround().statuses()) {
                if (status instanceof CoatingSource && status.isStatusActive()) {
                    sources.add(new CoatingSourceReference((CoatingSource) status, null, adjacent));
                }
            }
        }

        for (Item item : getInventory().getItems()) {
            if (!(item instanceof Coatable)) continue;
            Coatable target = (Coatable) item;
            for (CoatingSourceReference reference : sources) {
                if (reference.item == item) continue;
                actions.add(new ApplyCoatingAction(item, target, reference.source,
                        reference.item, reference.location));
            }
        }
    }

    /** A coating source and where it is currently available. */
    private static class CoatingSourceReference {
        private final CoatingSource source;
        private final Item item;
        private final Location location;

        private CoatingSourceReference(CoatingSource source, Item item, Location location) {
            this.source = source;
            this.item = item;
            this.location = location;
        }
    }
}
