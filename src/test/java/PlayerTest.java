import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;
    @Before
    public void setUp() throws Exception {
        this.player = new Player(3, 3, 3, 4, false, 0, 3);
    }

    @Test
    public void testToString() {
        System.out.println(this.player.toString());
    }

    @Test
    public void drawCards() {

    }

    @Test
    public void uniqueCards() {
        this.player.setHand(new Deck<>());
        this.player.getHand().addToDeck(new MoveAllCard());
        this.player.getHand().addToDeck(new MoveAllCard());
        this.player.getHand().addToDeck(new MoveOneCard());
        System.out.println(this.player.uniqueCards());
    }
}