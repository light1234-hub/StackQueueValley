
package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.tool.Hoe;
import game.tool.WateringCan;
import game.farmeraction.CoatingActionProvider;
import game.weather.WeatherCoordinator;
import game.weather.WeatherCraftingProvider;

/**
 * The player-controlled farmer who manages the farm and performs actions.
 */
public class Farmer extends Actor {

    private final CoatingActionProvider coatingActionProvider = new CoatingActionProvider();
    private final WeatherCoordinator weatherCoordinator;
    private final WeatherCraftingProvider weatherCraftingProvider = new WeatherCraftingProvider();

    /**
     * Creates a farmer with starting stamina and farming tools.
     *
     * @param name farmer's name
     * @param weatherCoordinator coordinator for weather events
     */
    public Farmer(String name, WeatherCoordinator weatherCoordinator) {
        super("Farmer " + name, '@', 100);

        this.weatherCoordinator = weatherCoordinator;

        this.addNewStatistic(
                ActorStatistics.STAMINA, new BaseStatistic(20));

        this.addNewStatistic(
                FinanceStatistics.COINS, new BaseStatistic(10000));

        this.getInventory().add(new Hoe());
        this.getInventory().add(new WateringCan());

        this.enableAbility(ZombieTarget.ZOMBIE_TARGET);
    }

    /**
     * Handles the farmer's turn, including weather updates,
     * stamina regeneration, crafting actions and menu display.
     *
     * @param actions available actions
     * @param lastAction farmer's previous action
     * @param map current game map
     * @param display current display
     * @return the action selected for the turn
     */
    @Override
    public Action playTurn(
            ActionList actions,
            Action lastAction,
            GameMap map,
            Display display) {

        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Teammate's REQ5 weather event updates
        weatherCoordinator.updateOnTurn(map.locationOf(this));

        // Handle multi-turn actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Regenerate stamina
        this.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);

        // Existing coating actions
        coatingActionProvider.addAvailableActions(this, map, actions);

        // Your REQ5 weather crafting actions
        weatherCraftingProvider.addAvailableActions(this, actions);

        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}

