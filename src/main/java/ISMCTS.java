import java.util.*;
import java.util.stream.Collectors;

public class ISMCTS {
    static double DEFAULT_EXPLORATION = 0.7;
    private int itermax;
    private boolean verbose;
    private double exploration;

    public ISMCTS(int itermax, boolean verbose, double exploration) {
        this.itermax = itermax;
        this.verbose = verbose;
        this.exploration = exploration;
    }

    public ISMCTS(int itermax, boolean verbose) {
        this(itermax, verbose, DEFAULT_EXPLORATION);
    }

    private Set<Move> getMoves(Game game) {
        return game.getState().possibleMoves(game);
    }

//    private Set<String> getMoves(Game game) {
//         return game.getState().possibleMoves(game).stream()
//                .map(Game::getLastMove).collect(Collectors.toSet());
//    }

    public Move selectMove(Game rootstate) {
        TreeNode root = new TreeNode(null, null, null, this.getExploration());
        Random rand = new Random();
        // If only one possible state, return that state
        List<Move> moves = new ArrayList<>(rootstate.getState().possibleMoves(rootstate));
        if(moves.size() == 1) {
            return moves.get(0);
        }

        for (int i = 0; i < this.itermax; i++) {
            TreeNode node = root;
            // Randomise game state
            PlayerType toMove = rootstate.getActivePlayerType();
            Game state = rootstate.cloneAndRandomise(toMove);

            List<Game> stateHistory = new ArrayList<>();
            List<Move> moveHistory = new ArrayList<>();
            stateHistory.add(new Game(state));
            moveHistory.add(null);

            // Select
            Set<Move> legalMoves = this.getMoves(state);
            Set<Move> untriedMoves = node.getUntriedMoves(this.getMoves(state));
            while(legalMoves.size() != 0 && untriedMoves.size() == 0) {
                node = node.selectChild(legalMoves);
                Move nodeMove = node.getMove();
                state = nodeMove.makeMove(state);
//                moveHistory.add(nodeMove);
//                stateHistory.add(new Game(state));
                legalMoves = this.getMoves(state);
                untriedMoves = node.getUntriedMoves(this.getMoves(state));
            }

            // Expand
            if (untriedMoves.size() != 0) {
                // Determine player to move
                PlayerType player = state.getState().playerToMove(state);
                // Select a random move
                int randomIdx = rand.nextInt(untriedMoves.size());
                Move selectedMove = new ArrayList<>(untriedMoves).get(randomIdx);
                state = selectedMove.makeMove(state);
//                moveHistory.add(selectedMove);
//                stateHistory.add(new Game(state));
                node = node.addChild(selectedMove, player);
                }

            // Simulate
            // Play randomly until a terminal state is reached
            while (!(state.getState() == GameState.CREATURE_WIN || state.getState() == GameState.SCIENTIST_WIN)) {
                List<Move> legalMoveList = new ArrayList<>(this.getMoves(state));
                int randomIdx = rand.nextInt(legalMoveList.size());
                Move selectedMove = legalMoveList.get(randomIdx);
                state = selectedMove.makeMove(state);
//                stateHistory.add(new Game(state));
//                moveHistory.add(selectedMove);
            }

            // Backpropagate
            while (!Objects.isNull(node)) {
                node.update(state.getState());
                node = node.getParent();
            }
            if(verbose) {
                System.out.println("Iteration " + i + " of " + itermax);
            }
        }
        Move bestMove = root.getChildren().stream()
                .max((a, b) -> Integer.compare(a.getVisits(), b.getVisits())).get().getMove();
        return bestMove;
    }

    public static void main(String[] args) {
        ISMCTS creature_iscmts = new ISMCTS(10000, false);
        ISMCTS scientist_iscmts = new ISMCTS(2000, false);
        int sciWin = 0;
        int creWin = 0;
        List<Game> endState = new ArrayList<>();
        for(int i= 0; i < 10; i++) {
            Game game = new Game();
            while (!(game.getState() == GameState.SCIENTIST_WIN || game.getState() == GameState.CREATURE_WIN)) {
                System.out.println(game);
                PlayerType move = game.getState().playerToMove(game);
                ISMCTS this_turn = move == PlayerType.SCIENTIST ? scientist_iscmts : creature_iscmts;
                game = this_turn.selectMove(game).makeMove(game);
            }
            System.out.println(game);
            if(game.getState() == GameState.SCIENTIST_WIN) {
                sciWin++;
            } else {
                creWin++;
            }
            System.out.println("Scientists: " + sciWin + ", Creature: " + creWin);
            endState.add(game);
        }
    }

    public double getExploration() {
        return exploration;
    }

    public void setExploration(double exploration) {
        this.exploration = exploration;
    }
}