package game.item;

import edu.monash.fit2099.engine.items.Item;

public class Sword extends Item
{
    public enum Coating { NONE, FIRE, CHARGED, MELON }
    private Coating coating = Coating.NONE;

    public Sword()
    {
        super("Sword",'!');
        this.makePortable();
    }

    public Coating getCoating() { return coating; }
    public void setCoating(Coating coating) { this.coating = coating; }
}
