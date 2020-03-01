import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class MoveAllCardTest {
    private Board board;
    @Before
    public void setUp() throws Exception {
        // Make a board to perform the splits on
        List<Tile> tiles = IntStream.range(0, 17).mapToObj((i) -> {
            return new Tile(false);
        }).collect(Collectors.toList());
        Deck<Tile> deck = new Deck(tiles);
        board = new Board(deck,
                Arrays.asList(new Cube(Color.BLUE), new Cube(Color.BLUE), new Cube(Color.ORANGE), new Cube(Color.ORANGE),
                        new Cube(Color.GREEN)),
                Arrays.asList(new Cube(Color.BLUE), new Cube(Color.BLUE), new Cube(Color.GREEN), new Cube(Color.GREEN),
                        new Cube(Color.ORANGE))
        );
    }

    @Test
    public void getPossibleMoves() {
        Card card = new MoveAllCard();
        Set<Board> mvs = card.getPossibleMoves(this.board);
        for(Board b : mvs) {
            System.out.println(b.toString());
            System.out.println("");
        }
    }
}