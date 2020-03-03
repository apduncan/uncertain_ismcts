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

    private Set<Game> getStates(Game game) {
        return game.getState().possibleGames(game);
    }

    private Set<String> getMoves(Game game) {
         return game.getState().possibleGames(game).stream()
                .map(Game::getLastMove).collect(Collectors.toSet());
    }

    public Game selectMove(Game rootstate) {
        TreeNode root = new TreeNode("", null, null, this.getExploration());
        Random rand = new Random();
        // If only one possible state, return that state
        List<Game> games = new ArrayList<>(rootstate.getState().possibleGames(rootstate));
        if(games.size() == 1) {
            return games.get(0);
        }

        // Memo legal states & moves from a given state
        for (int i = 0; i < this.itermax; i++) {
            TreeNode node = root;

            // Randomise game state
            PlayerType toMove = rootstate.getActivePlayerType();
            Game state = rootstate.cloneAndRandomise(toMove);

            // Select
            Set<Game> legalStates;
            Set<String> legalMoves;
            Set<String> untriedMoves;
            do {
                legalStates = this.getStates(state);
                // If there's only one legal state, return that one without thinking

                legalMoves = this.getMoves(state);
                untriedMoves = node.getUntriedMoves(legalMoves);
                if (!(legalMoves.size() != 0 && untriedMoves.size() == 0)) {
                    break;
                }
                node = node.selectChild(legalMoves);
                final String selectedMove = node.getMove();
                state = legalStates.stream().filter(s -> s.getLastMove().equals(selectedMove)).findFirst().get();
                state.setState(state.getState().nextState(state, state));
            } while (true);

            // Expand
            if (untriedMoves.size() != 0) {
                // Determine player to move
                PlayerType player = state.getState().playerToMove(state);
                // Select a random move
                final Set<String> copyUntried = new HashSet<>(untriedMoves);
                List<Game> untriedStateList = legalStates.stream()
                        .filter(s -> copyUntried.contains(s.getLastMove()))
                        .collect(Collectors.toList());
                int randomIdx = rand.nextInt(untriedStateList.size());
                state = untriedStateList.get(randomIdx);
                state.setState(state.getState().nextState(state, state));
                node = node.addChild(state.getLastMove(), player);
            }

            // Simulate
            // Play randomly until a terminal state is reached
            while (!(state.getState() == GameState.CREATURE_WIN || state.getState() == GameState.SCIENTIST_WIN)) {
                List<Game> legalStateList = new ArrayList<>(this.getStates(state));
                int randomIdx = rand.nextInt(legalStateList.size());
                state = legalStateList.get(randomIdx);
                state.setState(state.getState().nextState(state, state));
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
        String bestMoveString = root.getChildren().stream()
                .max((a, b) -> Integer.compare(a.getVisits(), b.getVisits())).get().getMove();
        Set<Game> nextStates = rootstate.getState().possibleGames(rootstate);
        return nextStates.stream().filter(g -> g.getLastMove().equals(bestMoveString)).findFirst().get();
    }

    public static void main(String[] args) {
        ISMCTS creature_iscmts = new ISMCTS(10000, false);
        ISMCTS scientist_iscmts = new ISMCTS(10000, false);
        int sciWin = 0;
        int creWin = 0;
        List<Game> endState = new ArrayList<>();
        for(int i= 0; i < 20; i++) {
            Game game = new Game();
            while (!(game.getState() == GameState.SCIENTIST_WIN || game.getState() == GameState.CREATURE_WIN)) {
                System.out.println(game);
                PlayerType move = game.getState().playerToMove(game);
                ISMCTS this_turn = move == PlayerType.SCIENTIST ? scientist_iscmts : creature_iscmts;
                game = this_turn.selectMove(game);
                game.setState(game.getState().nextState(game, game));
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