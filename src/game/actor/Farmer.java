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

/**
 * The player-controlled farmer who manages the farm and performs actions.
 */
public class Farmer extends Actor
{
    private int coins;
    private static final int MAX_COINS = 10000;

    /**
     * Creates a farmer with starting stamina and farming tools.
     *
     * @param name farmer's name
     */
    public Farmer(String name)
    {
        super("Farmer " + name, '@', 100);
        this.coins = MAX_COINS;

        //init the the Stamina attribute
        this.addNewStatistic(ActorStatistics.STAMINA, new BaseStatistic(20));

        // give farmer the Item
        this.getInventory().add(new Hoe());
        this.getInventory().add(new WateringCan());

    }

    public int getCoins()
    {
        return coins;
    }

    public void addCoins(int amount)
    {
        this.coins = this.coins + amount;

        if (this.coins > MAX_COINS)
        {
            this.coins = MAX_COINS;
        }
    }

    public void deductCoins(int amount)
    {
        this.coins = this.coins - amount;

        if (this.coins < 0)
        {
            this.coins = 0;
        }
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

        //Display menu
        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
