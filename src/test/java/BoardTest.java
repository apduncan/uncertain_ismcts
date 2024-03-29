import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BoardTest {
    private Board board;
    private Deck<Tile> deck;

    @Before
    public void setUp() throws Exception {
        // Make a deck of tiles
        List<Tile> tiles = IntStream.range(0, 17).mapToObj((i) -> {
            return new Tile(false);
        }).collect(Collectors.toList());
        deck = new Deck(tiles);
        board = new Board(deck, Arrays.asList(new Cube(Color.BLUE), new Cube(Color.ORANGE)),
                Arrays.asList(new Cube(Color.ORANGE)));
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testToString() {
        System.out.println(this.board);
        board.addTiles(deck.draw(), deck.draw());
        System.out.println(this.board);
        Board b2 = new Board(board);
        System.out.println(b2);
    }

    @Test
    public void testHash() {
        Board clone = new Board(this.board);
        assertEquals(this.board.hashCode(),clone.hashCode());
        // Change something and check it is not equal
        clone.dropEdge(Board.Side.LEFT);
        assertNotEquals(this.board.hashCode(), clone.hashCode());
        clone = new Board(this.board);
        // Move a cube
        Cube green = new Cube(Color.GREEN);
        clone.getActiveRowSpaces().get(0).addCube(green);
        System.out.println(clone);
        assertNotEquals(this.board.hashCode(), clone.hashCode());
    }
}