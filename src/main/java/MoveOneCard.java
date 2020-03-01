import java.util.HashSet;
import java.util.Set;

public class MoveOneCard extends Card {
    @Override
    protected Set<Board> getPossibleMoves(Board currentBoard) {
        Set<Board> moves = new HashSet<>();
        // Attempt to move every cube both left and right one knight move
        // Loop through all spaces
        for(int i = 0; i < currentBoard.getActiveRowSpaces().size(); i++) {
            // Loop through all cubes on this space
            for(Cube cube : currentBoard.getActiveRowSpaces().get(i).getCubes()) {
                // Attempt a left and right move
                for(int offset : new int[] {-1, 2}) {
                    // Determine the target space in inactive row
                    int iTarget = i + offset;
                    // If target space exists, make move
                    if(iTarget > -1 && iTarget < currentBoard.getInactiveRowSpaces().size()) {
                        // Clone board
                        Board moveBoard = new Board(currentBoard);
                        Space space = moveBoard.getActiveRowSpaces().get(i);
                        // Remove the target cube
                        space.removeCube(cube);
                        // Place on target space
                        moveBoard.getInactiveRowSpaces().get(iTarget).addCube(cube);
                        moves.add(moveBoard);
                    }
                }
            }
        }
        return moves;
    }

    @Override
    protected Card makeCopy() {
        return new MoveOneCard();
    }

    @Override
    public String toString() {
        return "O";
    }
}
