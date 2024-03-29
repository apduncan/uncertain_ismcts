import org.apache.commons.math3.util.Combinations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SplitCard extends Card {
    @Override
    protected Set<Board> getPossibleMoves(Board currentBoard) {
        // Return all the board states which could be arrived at by splitting
        // Loop through spaces on the active row
        return IntStream.range(0, currentBoard.getActiveRowSpaces().size())
                .mapToObj(i -> this.splitSpace(i, currentBoard))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    protected Card makeCopy() {
        return new SplitCard();
    }

    private Set<Board> splitSpace(int spaceIdx, Board currentBoard) {
        Space space = currentBoard.getActiveRowSpaces().get(spaceIdx);
        // Determine all the ways to partition the cubes in two
        // For set of cubes C, n = |C|. Find combinations of k number of cubes which can be selected, from 1 to (k / 2)
        Set<Integer> nSet = IntStream.range(0, space.getCubeCount()).mapToObj(Integer::new).collect(Collectors.toSet());
        Set<Board> moves = new HashSet<>();
        Double limit = new Double(space.getCubeCount()) / 2;
        if(space.getCubeCount() == 1) {
            limit = Double.valueOf(1.0);
        }
        for(int k = 1; k <= limit; k++) {
            for (int[] combo: new Combinations(space.getCubeCount(), k)) {
                // Determine the cube indices to put into the k-set
                Set<Integer> kSet = IntStream.of(combo).mapToObj(Integer::new).collect(Collectors.toSet());
                // Any indices not going into k-set will go to nk-set
                Set<Integer> nkSet = new HashSet<>(nSet);
                nkSet.removeAll(kSet);
                // Convert these to cubes
                List<Cube> kCube = this.cubesFromIndices(space.getCubes(), kSet);
                List<Cube> nkCube = this.cubesFromIndices(space.getCubes(), nkSet);
                // Send k-left and nk-right
                moves.add(this.makeSplit(spaceIdx, currentBoard, kCube, nkCube));
                // Send k-right and nk-left
                moves.add(this.makeSplit(spaceIdx, currentBoard, nkCube, kCube));
            }
        }
        if(moves.size() == 0) {
            int foo = 1;
        }
        return moves;
    }

    private List<Cube> cubesFromIndices(List<Cube> cubes, Set<Integer> indices) {
        // Take a list of cubes, and return those specified in indices
        return indices.stream().map(cubes::get).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "S";
    }

    private Board makeSplit(int spaceIdx, Board currentBoard, List<Cube> left, List<Cube> right) {
        // Make a specific split move and return a new board state
        Board movedBoard = new Board(currentBoard);
        Space space = movedBoard.getActiveRowSpaces().get(spaceIdx);
        // Remove all cubes from active space and put copies in the spaces on inactive row.
        space.clearCubes();
        Space leftSpace = movedBoard.getInactiveRowSpaces().get(spaceIdx);
        Space rightSpace = movedBoard.getInactiveRowSpaces().get(spaceIdx+1);
        leftSpace.addCubes(left);
        rightSpace.addCubes(right);
        return movedBoard;
    }
}
