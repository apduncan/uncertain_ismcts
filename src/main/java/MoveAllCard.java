import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveAllCard extends Card {
    @Override
    protected Set<Board> getPossibleMoves(Board currentBoard) {
        return Arrays.stream(Color.values())
                .map(c -> this.moveColor(c, currentBoard))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    protected Card makeCopy() {
        return new MoveAllCard();
    }

    private Set<Board> moveColor(Color color, Board currentBoard) {
        Set<Board> moves = new HashSet<>();
        // An offset of 0 is equivalent to moving left, offset 1 to moving right
        for(int offset : new int[] {0, 1}) {
            Board moveBoard = new Board(currentBoard);
            for(int i = 0; i < moveBoard.getActiveRowSpaces().size(); i++) {
                Space space = moveBoard.getActiveRowSpaces().get(i);
                // Remove any color cubes from active space
                // First find them
                List<Cube> moveCubes = space.getCubes()
                        .stream()
                        .filter(c -> c.getColor() == color)
                        .collect(Collectors.toList());
                // Now remove them
                moveCubes.forEach(c -> space.removeCube(c));
                // Put them on tile i+offset on the inactive row
                Space moveTo = moveBoard.getInactiveRowSpaces().get(i+offset);
                moveTo.addCubes(moveCubes);
            }
            moves.add(moveBoard);
        }
        return moves;
    }

    @Override
    public String toString() {
        return "A";
    }

}
