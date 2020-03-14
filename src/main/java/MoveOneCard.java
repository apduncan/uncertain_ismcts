import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MoveOneCard extends Card {
    @Override
    protected Set<Move> getPossibleMoves(Game currentGame, boolean wildcard) {
        Set<Move> moves = new HashSet<>();
        // Attempt to move every cube both left and right one knight move
        // Loop through all spaces
        Board currentBoard = currentGame.getBoard();
        for(int i = 0; i < currentBoard.getActiveRowSpaces().size(); i++) {
            // Loop through all cubes on this space
            for(Cube cube : currentBoard.getActiveRowSpaces().get(i).getCubes()) {
                // Attempt a left and right move
                for(int offset : new int[] {-1, 2}) {
                    final int moveIdx = i;
                    final int moveOffset = offset;
                    // Skip if the target space does not exist
                    final int iTarget = moveIdx + moveOffset;
                    if(!(iTarget > -1 && iTarget < currentGame.getBoard().getInactiveRowSpaces().size())) {
                        continue;
                    }
                    // Make a function to complete the move
                    Function<Game, Game> move = g -> {
                        // Determine the target space in inactive row
                        Board gameBoard = g.getBoard();
                        // If target space exists, make move
                        if (iTarget > -1 && iTarget < gameBoard.getInactiveRowSpaces().size()) {
                            Space space = gameBoard.getActiveRowSpaces().get(moveIdx);
                            // Remove the target cube
                            space.removeCube(cube);
                            // Place on target space
                            gameBoard.getInactiveRowSpaces().get(iTarget).addCube(cube);
                        }
                        this.playCard(g, wildcard);
                        return g;
                    };
                    Move moveOne = new Move(move, "MOVEONE|" + moveIdx + "|" + cube.getColor() + "|"
                            + moveOffset + "|" + currentGame.getBoard().hashCode());
                    moves.add(moveOne);
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
