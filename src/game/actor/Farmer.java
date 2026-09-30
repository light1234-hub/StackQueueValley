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
import game.effect.ChargeEffect;
import game.effect.FireEffect;
import game.farmeraction.ApplySwordCoatingAction;
import game.item.MelonItem;
import game.item.Sword;

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

        addSwordCoatingActions(actions, map);

        //Display menu
        display.println(this.name);
        display.println("HP: " + this.getStatistic(ActorStatistics.HEALTH));
        display.println("SP: " + this.getStatistic(ActorStatistics.STAMINA));
        display.println("Inventory: " + this.getInventory().getItems());

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    private void addSwordCoatingActions(ActionList actions, GameMap map) {
        boolean carryingMelon = this.getInventory().getItems().stream().anyMatch(MelonItem.class::isInstance);
        java.util.List<edu.monash.fit2099.engine.positions.Location> nearby = new java.util.ArrayList<>();
        edu.monash.fit2099.engine.positions.Location here = map.locationOf(this);
        nearby.add(here);
        for (edu.monash.fit2099.engine.positions.Exit exit : here.getExits()) nearby.add(exit.getDestination());

        for (edu.monash.fit2099.engine.items.Item item : this.getInventory().getItems()) {
            if (!(item instanceof Sword)) continue;
            Sword sword = (Sword) item;
            if (carryingMelon) actions.add(new ApplySwordCoatingAction(sword, Sword.Coating.MELON, "Melon"));
            for (edu.monash.fit2099.engine.positions.Location source : nearby) {
                if (source.getGround().statusesOf(FireEffect.class).stream().anyMatch(FireEffect::isActive))
                    actions.add(new ApplySwordCoatingAction(sword, Sword.Coating.FIRE, "Fire"));
                if (source.getGround().statusesOf(ChargeEffect.class).stream().anyMatch(ChargeEffect::isActive))
                    actions.add(new ApplySwordCoatingAction(sword, Sword.Coating.CHARGED, "charged ground"));
            }
        }
    }
}
