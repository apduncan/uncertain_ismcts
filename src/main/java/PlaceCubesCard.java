import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlaceCubesCard extends Card {
    @Override
    protected Set<Move> getPossibleMoves(Game currentGame, boolean wildcard) {
        /* This was originally a step rather than an action triggered by a card.
        Decided to try making this a card that only the creature has access to, but can also be selected as a
        wildcard action by the scientist (makes timing a bit trickier)
        As such, we'll reuse the code for generating possible positions from the Game class.
        Also, try letting this card draw 2 more cards, see if that balances things out.
         */
        // Return all the states in which the dropped cubes could be placed.
        // If no dropped cubes, just return current state
        Set<Move> moves = new HashSet<>();
        if(currentGame.getDroppedCubes().size() == 0) {
            Function<Game, Game> move = g -> {
                Player player = g.getActivePlayer();
                this.playCard(g, wildcard);
                player.setWildcardReady(true);
                return g;
            };
            moves.add(new Move(move, "PLACECUBE|JUSTDRAW"));
            return moves;
        }
        // If lots of cubes to place (more than 3) us a heuristic method rather than exhaustive search for
        // possible placements.
        Set<Board> possibleBoards = currentGame.getDroppedCubes().size() < 4 ?
                currentGame.placeCubes(new HashSet<>(Arrays.asList(new Board(currentGame.getBoard()))),
                    currentGame.getDroppedCubes().stream().map(Cube::new).collect(Collectors.toList()),
                    new Board(currentGame.getBoard())) :
                currentGame.heuristicPlaceCubes(new HashSet<>(Arrays.asList(new Board(currentGame.getBoard()))),
                    currentGame.getDroppedCubes().stream().map(Cube::new).collect(Collectors.toList()),
                    new Board(currentGame.getBoard()));
        for(Board board : possibleBoards) {
            final Board copy = new Board(board);
            if(copy.size() > copy.getMaxWidth()) {
                int foo = 1;
            }
            Function<Game, Game> move = g -> {
                if(copy.size() > copy.getMaxWidth()) {
                    int foo = 1;
                }
                g.setBoard(new Board(copy));
                g.setDroppedCubes(new ArrayList<>());
                Player active = g.getActivePlayer();
                this.playCard(g, wildcard);
                active.setWildcardReady(true);
                return g;
            };
            moves.add(new Move(move, "PLACECUBE|" + copy.hashCode()));
        }
        return moves;
    }

    @Override
    protected Card makeCopy() {
        return new PlaceCubesCard();
    }

    @Override
    public String toString() { return "P"; }
}
