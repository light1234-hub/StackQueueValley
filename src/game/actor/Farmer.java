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
import game.effect.ChargedGroundCoating;
import game.effect.FireCoating;
import game.effect.MelonCoating;
import game.farmeraction.ApplyCoatingAction;
import game.blueprint.Coatable;
import game.item.MelonItem;

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

    /** Adds coating actions for carried coatable items and available sources. */
    private void addCoatingActions(ActionList actions, GameMap map) {
        edu.monash.fit2099.engine.items.Item melon = this.getInventory().getItems().stream()
                .filter(MelonItem.class::isInstance).findFirst().orElse(null);
        java.util.List<edu.monash.fit2099.engine.positions.Location> nearby = new java.util.ArrayList<>();
        edu.monash.fit2099.engine.positions.Location here = map.locationOf(this);
        nearby.add(here);
        for (edu.monash.fit2099.engine.positions.Exit exit : here.getExits()) nearby.add(exit.getDestination());

        boolean fireAvailable = nearby.stream().anyMatch(location ->
                location.getGround().statusesOf(FireEffect.class).stream().anyMatch(FireEffect::isActive));
        boolean chargeAvailable = nearby.stream().anyMatch(location ->
                location.getGround().statusesOf(ChargeEffect.class).stream().anyMatch(ChargeEffect::isActive));

        for (edu.monash.fit2099.engine.items.Item item : this.getInventory().getItems()) {
            if (!(item instanceof Coatable)) continue;
            Coatable target = (Coatable) item;
            if (melon != null) actions.add(new ApplyCoatingAction(item, target, new MelonCoating(), melon));
            if (fireAvailable) actions.add(new ApplyCoatingAction(item, target, new FireCoating(), null));
            if (chargeAvailable) actions.add(new ApplyCoatingAction(item, target, new ChargedGroundCoating(), null));
        }
    }
}
