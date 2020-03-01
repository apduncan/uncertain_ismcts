import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeNode {
    /***
     * Node in ISMCTS game tree.
     */
    private Game state;
    private TreeNode parent;
    private List<TreeNode> children;
    private int wins;
    private int visits;
    private int avails;
    private double exploration;
    private GameState winState;
    private Set<Game> legalMoves;

    public TreeNode(Game state, TreeNode parent, GameState winState) {
        this.state = state;
        this.parent = parent;
        this.winState = winState;
        this.legalMoves = state.getState().possibleGames(state);
        this.children = new ArrayList<>();
        this.wins = 0;
        this.visits = 0;
        this.avails = 1;
    }

    public Game getState() {
        return state;
    }

    public void setState(Game state) {
        this.state = state;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getAvails() {
        return avails;
    }

    public void setAvails(int avails) {
        this.avails = avails;
    }

    public double getExploration() {
        return exploration;
    }

    public void setExploration(double exploration) {
        this.exploration = exploration;
    }

    public GameState getWinState() {
        return winState;
    }

    public void setWinState(GameState winState) {
        this.winState = winState;
    }

    public Set<Game> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(Set<Game> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public Set<Game> getUntriedMoves() {
        Set<Game> legal = new HashSet<>(this.getLegalMoves());
        Set<Game> tried = this.getChildren().stream().map(TreeNode::getState).collect(Collectors.toSet());
        legal.removeAll(tried);
        return legal;
    }

    public TreeNode selectChild() {
        return null;
    }
}
