import java.util.*;
import java.util.stream.Collectors;

public class TreeNode {
    /***
     * Node in ISMCTS game tree.
     */
    /***
     * TODO: Maybe consider a transposition handling scheme?
     */
    private Move move;
    private TreeNode parent;
    private List<TreeNode> children;
    private int wins;
    private int visits;
    private int avails;
    private double exploration;
    private PlayerType justMoved;

    public TreeNode(Move move, TreeNode parent, PlayerType justMoved, double exploration) {
        this.move = move;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.wins = 0;
        this.visits = 0;
        this.avails = 1;
        this.justMoved = justMoved;
        this.exploration = exploration;
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

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Set<Move> triedMoves() {
        return this.children.stream().map(TreeNode::getMove).collect(Collectors.toSet());
    }

    public Set<Move> getUntriedMoves(Set<Move> legalMoves) {
        // Get a list of moves which have not been tried which are among the legal moves
        Set<Move> tried = this.triedMoves();
        Set<Move> untried = new HashSet<>(legalMoves);
        untried.removeAll(tried);
        return untried;
    }

    public TreeNode selectChild(Set<Move> legalMoves) {
        // Select a child node for which the move is legal, based on UCB1 formula
        List<TreeNode> legalChildren = this.children.stream().filter(c -> legalMoves.contains(c.getMove()))
                .collect(Collectors.toList());
        // Get child with highest UCB score
        TreeNode selected =  legalChildren.stream().reduce(null, (TreeNode a, TreeNode b) -> {
            if(Objects.isNull(a)) {
                return b;
            }
            if(b.ucbScore() > a.ucbScore()) {
                return b;
            } else {
                return a;
            }
        });
        // Updated availability counts
        for(TreeNode child : legalChildren) {
            child.setAvails(child.getAvails() + 1);
        }
        return selected;
    }

    public Double ucbScore() {
        return Double.valueOf(this.getWins()) / Double.valueOf(this.getVisits()) + (this.getExploration() *
                Math.sqrt(Math.log(Double.valueOf(this.getAvails())) / Double.valueOf(this.getVisits())));
    }

    public TreeNode addChild(Move move, PlayerType justMoved) {
        TreeNode child = new TreeNode(move, this, justMoved, this.exploration);
        this.getChildren().add(child);
        return child;
    }

    public void update(GameState terminalState) {
        // Update node - increment visit count, increase win count if terminal state matches win state
        this.visits = this.visits + 1;
        if(this.getJustMoved() == PlayerType.SCIENTIST) {
            this.wins = this.wins + (terminalState == GameState.SCIENTIST_WIN ? 1 : 0);
        }
        if(this.getJustMoved() == PlayerType.CREATURE) {
            this.wins = this.wins + (terminalState == GameState.CREATURE_WIN ? 1 : 0);
        }
    }

    public PlayerType getJustMoved() {
        return justMoved;
    }

    public void setJustMoved(PlayerType justMoved) {
        this.justMoved = justMoved;
    }
}
