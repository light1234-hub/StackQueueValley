package game.actor;

import game.behaviour.WanderBehaviour;
import game.behaviour.SeekShelterBehaviour;

/**
 * An abstract class for all passive NPCS to extend
 */
public abstract class PassiveCreature extends NPC
{
    /**
     *  A constructor that creates an object of the Passive Creature Class
     * @param name
     * @param displayChar
     * @param health
     */
    public PassiveCreature(String name, char displayChar, int health)
    {
        super(name,displayChar,health);

        this.addBehaviour(new SeekShelterBehaviour());
        this.addBehaviour(new WanderBehaviour());
    }
}
