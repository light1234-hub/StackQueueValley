package game.req4tests;

import game.item.AegisElixir;
import game.item.CopperItem;
import game.item.DiamondItem;
import game.item.GaleDraught;
import game.item.ProspectorElixir;
import game.item.Potion;
import game.item.StoneItem;
import game.item.StrawberryItem;
import game.item.TomatoItem;
import org.junit.jupiter.api.Test;
import static game.req4tests.TestSupport.*;

import static org.junit.jupiter.api.Assertions.*;

/** The recipe table from the spec. */
class PotionRecipeTest {
    private static void check(Potion p, int xp, int turns, int total) throws Exception {
        assertEquals(xp, p.getXpRequired());
        assertEquals(turns, p.getBrewTurns());
        assertEquals(total, p.getIngredients().values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void galeDraught() throws Exception {
        Potion p = new GaleDraught();
        check(p, 50, 3, 5);
        assertEquals(3, p.getIngredients().get(StrawberryItem.class));
        assertEquals(1, p.getIngredients().get(TomatoItem.class));
        assertEquals(1, p.getIngredients().get(CopperItem.class));
    }

    @Test
    void aegisElixir() throws Exception {
        Potion p = new AegisElixir();
        check(p, 100, 4, 6);
        assertEquals(3, p.getIngredients().get(StoneItem.class));
        assertEquals(2, p.getIngredients().get(StrawberryItem.class));
        assertEquals(1, p.getIngredients().get(DiamondItem.class));
    }

    @Test
    void prospectorElixir() throws Exception {
        Potion p = new ProspectorElixir();
        check(p, 150, 5, 5);
        assertEquals(2, p.getIngredients().get(CopperItem.class));
        assertEquals(2, p.getIngredients().get(TomatoItem.class));
        assertEquals(1, p.getIngredients().get(StoneItem.class));
    }

    @Test
    void potionsAreConsumableOnceCarried() throws Exception {
        assertNotNull(new GaleDraught().getItem());
    }
}