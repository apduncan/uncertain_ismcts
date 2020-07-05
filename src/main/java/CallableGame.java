import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableGame implements Callable<CallableGame> {
    private Game gameState;
    private ISMCTS scientistBrain;
    private ISMCTS creatureBrain;
    private PlayerType winner;
    private List<Game> gameStateHistory;
    private List<Move> gameMoveHistory;

    static int DEF_SCIENTIST_ITERMAX = 5000;
    static int DEF_CREATURE_ITERMAX = 5000;

    public CallableGame() {
        this.scientistBrain = new ISMCTS(DEF_SCIENTIST_ITERMAX, false);
        this.creatureBrain = new ISMCTS(DEF_CREATURE_ITERMAX, false);
        this.gameState = new Game();
        this.gameStateHistory = new ArrayList<>();
        this.gameMoveHistory = new ArrayList<>();
    }

    public CallableGame(ISMCTS scientistBrain, ISMCTS creatureBrain) {
        this();
        this.scientistBrain = scientistBrain;
        this.creatureBrain = creatureBrain;
    }

    @Override
    public CallableGame call() {
        // Store a history of the game state and moves in case we want to analyse these later
        this.addHistory(this.gameState);
        while (!(this.gameState.getState() == GameState.SCIENTIST_WIN ||
                this.gameState.getState() == GameState.CREATURE_WIN)) {
            PlayerType move = this.gameState.getState().playerToMove(this.gameState);
            ISMCTS this_turn = move == PlayerType.SCIENTIST ? this.scientistBrain : this.creatureBrain;
            Move selectedMove = this_turn.selectMove(this.gameState);
            selectedMove.makeMove(this.gameState);
            this.addHistory(this.gameState, selectedMove);
        }
        // Set the winning player
        this.winner = this.gameState.getState() == GameState.SCIENTIST_WIN ? PlayerType.SCIENTIST : PlayerType.CREATURE;
        System.out.println("END: " + this.winner.name());
        System.out.println("### TURN END-1 ###");
        System.out.println(this.getGameStateHistory().get(this.getGameStateHistory().size()-2));
        System.out.println("### TURN END ###");
        System.out.println(this.gameState);
        return this;
    }

    private void addHistory(Game game) {
        this.addHistory(game, null);
    }

    private void addHistory(Game game, Move move) {
        this.gameStateHistory.add(new Game(game));
        this.gameMoveHistory.add(move);
    }

    public Game getGameState() {
        return gameState;
    }

    public void setGameState(Game gameState) {
        this.gameState = gameState;
    }

    public ISMCTS getScientistBrain() {
        return scientistBrain;
    }

    public void setScientistBrain(ISMCTS scientistBrain) {
        this.scientistBrain = scientistBrain;
    }

    public ISMCTS getCreatureBrain() {
        return creatureBrain;
    }

    public void setCreatureBrain(ISMCTS creatureBrain) {
        this.creatureBrain = creatureBrain;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public void setWinner(PlayerType winner) {
        this.winner = winner;
    }

    public List<Game> getGameStateHistory() {
        return gameStateHistory;
    }

    public void setGameStateHistory(List<Game> gameStateHistory) {
        this.gameStateHistory = gameStateHistory;
    }

    public List<Move> getGameMoveHistory() {
        return gameMoveHistory;
    }

    public void setGameMoveHistory(List<Move> gameMoveHistory) {
        this.gameMoveHistory = gameMoveHistory;
    }
}
