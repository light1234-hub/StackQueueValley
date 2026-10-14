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
import game.ability.TavernAbility;
import game.progression.ExperienceTracker;
import game.tool.Hoe;
import game.tool.WateringCan;
import game.farmeraction.CoatingActionProvider;

/**
 * The player-controlled farmer who manages the farm and performs actions.
 */
public class Farmer extends Actor {
    private final CoatingActionProvider coatingActionProvider = new CoatingActionProvider();
    private final ExperienceTracker experience = new ExperienceTracker();

    /**
     * Gives this the farmer quest experience
     * @param amount amount of experience gain
     */
    public void gainExp(int amount){
        experience.award(amount);
    }

    /**
     * Allow to spend experience without knowing the quest
     * @param amount amount of exp to spend
     */
    public void spendExp(int amount){
        experience.spend(amount);
    }

    /**
     * Getter method for levels and exps
     */

    public int getLevel(){
        return experience.level();
    }

    public int getTotalExperience(){
        return experience.totalEarned();
    }

    public int getAvailableExperience(){
        return experience.available();
    }

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
        this.enableAbility(TavernAbility.CAN_USE_TAVERN);


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

        coatingActionProvider.addAvailableActions(this, map, actions);

        //Display menu
        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());
        display.println(("Coins: " + this.getStatistic(FinanceStatistics.COINS)));
        display.println("Level: " + getLevel() + " | Earned XP: " + getTotalExperience() + " | Available XP: " + getAvailableExperience());
        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

}
