import org.junit.Before;
import org.junit.Test;

public class GameTest {
    private Game game;

    @Test
    public void getActivePlayerMoves() {
        System.out.println(game.getActivePlayerMoves());
    }
     @Test
     public void getCreatureUpdate() {
         System.out.println(((Game)this.game.getActivePlayerMoves().toArray()[0]).getCreatureUpdates());
     }

    @Test
    public void testToString() {
        System.out.println(this.game.toString());
    }

    @Before
    public void setUp() throws Exception {
        this.game = new Game();
    }

    @Test
    public void cloneAndRandomise() {
        System.out.println(game.toString());
        Game randomGame = game.cloneAndRandomise(PlayerType.CREATURE);
        System.out.println(randomGame.toString());
    }
}