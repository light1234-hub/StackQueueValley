package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents an interaction with the Tavern bartender.
 * Allows an actor to initiate dialogue with the bartender.
 */
public class TalkBartenderAction extends Action {

    /**
     * Executes the dialogue interaction with the bartender.
     *
     * @param actor the actor initiating the conversation
     * @param map the game map where the action is performed
     * @return the bartender's dialogue message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return "Bartender: Welcome to the Drunken Tavern! Contracts, wagers and "
                + "the occasional terrible decision are coming soon.";
    }

    /**
     * Returns the description of the dialogue action displayed in the menu.
     *
     * @param actor the actor performing the action
     * @return the menu description for talking to the bartender
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Talk to the bartender";
    }
}
