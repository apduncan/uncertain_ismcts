import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    // CONSTANTS
    public static final int DEFAULT_DECK_SIZE = 17;
    public static final int DEFAULT_TOKEN_TILES = 5;
    public static final PlayerType DEFAULT_SCIENTIST = PlayerType.SCIENTIST;
    public static final PlayerType DEFAULT_CREATURE = PlayerType.CREATURE;
    public static final List<Color> DEFAULT_LEFTCUBES = new ArrayList<>(Arrays.asList(Color.BLUE, Color.BLUE,
            Color.ORANGE, Color.ORANGE, Color.GREEN));
    public static final List<Color> DEFUALT_RIGHTCUBES = new ArrayList<>(Arrays.asList(Color.BLUE, Color.BLUE,
            Color.ORANGE, Color.GREEN, Color.GREEN));
    public static final List<Card> WILDCARDS = new ArrayList<>(Arrays.asList(new MoveOneCard(), new MoveAllCard(),
            new SplitCard()));

    // PROPERTIES
    private Player scientist;
    private Player creature;
    private Board board;
    private Deck<Tile> tiles;
    private Player activePlayer;
    private GameState state;
    private List<Cube> droppedCubes;
    private Player firstPlayer;
    private String lastMove;

    public Game(PlayerType scientist, PlayerType creature, int boardWidth, int deckSize, int tokenTiles,
                List<Color> leftCubes, List<Color> rightCubes) {
        // Create a new game with some specific settings
        this.scientist = new Player(scientist);
        this.creature = new Player(creature);
        this.tiles = this.makeDeck(deckSize, boardWidth, tokenTiles);
        List<Cube> leftCube = leftCubes.stream().map(Cube::new).collect(Collectors.toList());
        List<Cube> rightCube = rightCubes.stream().map(Cube::new).collect(Collectors.toList());
        this.board = new Board(boardWidth, tiles, leftCube, rightCube);
        this.state = GameState.MOVE_CUBES;
        this.droppedCubes = new ArrayList<>();
        this.firstPlayer = this.creature;
        this.lastMove = "";
    }

    public Game() {
        // Make a default new game
        this.scientist = new Player(Game.DEFAULT_SCIENTIST);
        this.creature = new Player(Game.DEFAULT_CREATURE);
        this.tiles = this.makeDeck(Game.DEFAULT_DECK_SIZE, Board.DEFAULT_MAX_WIDTH, Game.DEFAULT_TOKEN_TILES);
        List<Cube> leftCube = Game.DEFAULT_LEFTCUBES.stream().map(Cube::new).collect(Collectors.toList());
        List<Cube> rightCube = Game.DEFUALT_RIGHTCUBES.stream().map(Cube::new).collect(Collectors.toList());
        this.board = new Board(tiles, leftCube, rightCube);
        this.activePlayer = this.creature;
        this.state = GameState.MOVE_CUBES;
        this.droppedCubes = new ArrayList<>();
        this.firstPlayer = this.creature;
        this.lastMove = "";
    }

    public Game(Game game) {
        this.scientist = new Player(game.scientist);
        this.creature = new Player(game.creature);
        this.board = new Board(game.board);
        this.tiles = new Deck<>(game.tiles);
        this.activePlayer = game.activePlayer == game.scientist ? this.scientist : this.creature;
        this.state = game.state;
        this.droppedCubes = game.droppedCubes.stream().map(Cube::new).collect(Collectors.toList());
        this.firstPlayer = game.getFirstPlayer() == game.getCreature() ? this.getCreature() : this.getScientist();
        this.lastMove = game.getLastMove();
    }

    public List<Cube> getDroppedCubes() {
        return droppedCubes;
    }

    public void setDroppedCubes(List<Cube> droppedCubes) {
        this.droppedCubes = droppedCubes;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getScientist() {
        return scientist;
    }

    public void setScientist(Player scientist) {
        if(this.scientist == this.getActivePlayer()) {
            this.setActivePlayer(scientist);
        }
        this.scientist = scientist;
    }

    public Player getCreature() {
        return creature;
    }

    public void setCreature(Player creature) {
        if(this.creature == this.getActivePlayer()) {
            this.activePlayer = this.creature;
        }
        this.creature = creature;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Deck<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Deck<Tile> tiles) {
        this.tiles = tiles;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void setActivePlayer(PlayerType player) {
        this.activePlayer = player == PlayerType.SCIENTIST ? this.getScientist() : this.getCreature();
    }

    public PlayerType getActivePlayerType() {
        return this.activePlayer == this.scientist ? PlayerType.SCIENTIST : PlayerType.CREATURE;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public Player toggleActivePlayer() {
        this.activePlayer = this.activePlayer == this.scientist ? this.creature : this.scientist;
        return this.activePlayer;
    }

    public Player toggleFirstPlayer() {
        this.firstPlayer = this.firstPlayer == this.scientist ? this.creature : this.scientist;
        return this.firstPlayer;
    }

    public Game cloneAndRandomise(PlayerType player) {
        // Randomise players
        Game clone = new Game(this);
        Player scientist = clone.getScientist().cloneAndRandomise(player == PlayerType.CREATURE);
        Player creature = clone.getCreature().cloneAndRandomise(player == PlayerType.SCIENTIST);
        Player firstPlayer = this.getFirstPlayer() == this.getScientist() ? scientist : creature;
        if(clone.getActivePlayerType() == PlayerType.SCIENTIST) {
            clone.setActivePlayer(scientist);
        } else {
            clone.setActivePlayer(creature);
        }
        clone.setScientist(scientist);
        clone.setCreature(creature);
        clone.setFirstPlayer(firstPlayer);
        // Randomise the deck of tiles
        // Token tiles have to be in the bottom 10
        Deck<Tile> tiles = clone.getTiles();
        int nonTokenCount = tiles.getItems().size() - 10;
        List<Tile> nonTokenTiles = IntStream.range(0, nonTokenCount).mapToObj(i -> tiles.getItems().get(i))
                .collect(Collectors.toList());
        List<Tile> tokenTiles = IntStream.range(Math.max(nonTokenCount, 0), tiles.getItems().size())
                .mapToObj(i -> tiles.getItems().get(i))
                .collect(Collectors.toList());
        Collections.shuffle(tokenTiles);
        tiles.setItems(nonTokenTiles);
        tiles.getItems().addAll(tokenTiles);
        return clone;
    }

    private Deck<Tile> makeDeck(int deckSize, int boardWidth, int tokenTiles) {
        // Construct a deck of n tiles, t of which have tokens.
        // Place the token tiles in the the bottom  n - b, where b is board width
        // topTiles hold the top tiles (which don't need shuffling), shufTiles holds the bottom tiles with the tokens,
        // which need shuffling before putting on the bottom of the deck.
        List<Tile> topTiles = new ArrayList<>();
        List<Tile> shufTiles = new ArrayList<>();
        int baseTile = deckSize - boardWidth - tokenTiles;
        IntStream.range(0, boardWidth).forEach(i -> topTiles.add(new Tile(false)));
        IntStream.range(0, baseTile).forEach(i -> shufTiles.add(new Tile(false)));
        IntStream.range(0, tokenTiles).forEach(i -> shufTiles.add(new Tile(true)));
        // Shuffle the bottom bit of the deck
        Collections.shuffle(shufTiles);
        // Combine the two - first index is the top of the deck
        topTiles.addAll(shufTiles);
        // Make into a deck
        return new Deck<>(topTiles);
    }

    public Set<Game> getActivePlayerMoves() {
        // Return a set of all possible states the game could move into when the active player takes a move.
        Set<Game> games = new HashSet<>();
        // First possibility is that the active player draws a card.
        if(this.getActivePlayer().getHand().size() < this.getActivePlayer().getHandLimit()) {
            Game draw = new Game(this);
            draw.activePlayer.drawCards(1);
            draw.toggleActivePlayer();
            draw.setLastMove((this.activePlayer == this.creature ?
                    PlayerType.CREATURE.name() : PlayerType.SCIENTIST.name()) + "|DRAW");
            games.add(draw);
        }
        // Play each distinct card
        for(Card card : this.getActivePlayer().uniqueCards()) {
            // For each distinct card in this players hand, make all possible future states if that card is played
            for(Board board : card.getPossibleMoves(this.board)) {
                Game cardPlay = new Game(this);
                cardPlay.setBoard(board);
                cardPlay.activePlayer.getHand().getItems().remove(card);
                cardPlay.activePlayer.getDeck().getDiscard().add(card);
                cardPlay.toggleActivePlayer();
                // Make a string so we can uniquely identify the move just made (card played + board state moved into)
                String moveDesc = (this.activePlayer == this.creature ? PlayerType.CREATURE.name() :
                        PlayerType.SCIENTIST.name()) + "|" + card.getClass().getName() + "|" + board.hashCode();
                cardPlay.setLastMove(moveDesc);
                games.add(cardPlay);
            }
        }
        // Play wildcard
        if(this.getActivePlayer().isWildcardReady()) {
            for (Card card : Game.WILDCARDS) {
                // For each distinct card in this players hand, make all possible future states if that card is played
                for (Board board : card.getPossibleMoves(this.board)) {
                    Game cardPlay = new Game(this);
                    cardPlay.getActivePlayer().setWildcardReady(false);
                    cardPlay.setBoard(board);
                    cardPlay.toggleActivePlayer();
                    // Make a string so we can uniquely identify the move just made (card played + board state moved into)
                    String moveDesc = (this.activePlayer == this.creature ? PlayerType.CREATURE.name() :
                            PlayerType.SCIENTIST.name()) + "|WILDCARD|" + board.hashCode();
                    games.add(cardPlay);
                }
            }
        }
        return games;
    }

    public Set<Game> getCreatureUpdates() {
        // Return possible game states when creature updates their position
        Set<Game> games = new HashSet<>();
        // Deal with numbers of cubes first
        // Determine the numbers the creature could say
        Set<Integer> numbers = IntStream.range(0, this.getBoard().getInactiveRowTiles().size())
                .map(i -> this.getBoard().getNumberAdjacent(false, i))
                .boxed()
                .collect(Collectors.toSet());
        for(Integer num : numbers) {
            Game copy = new Game(this);
            // Move to number, modifies in place
            copy.getBoard().moveToNumber(num);
            copy.setLastMove("CREATUREUPDATE|" + board.hashCode());
            games.add(copy);
        }

        // Deal with colors second
        Set<Set<Color>> colors = IntStream.range(0, this.getBoard().getInactiveRowTiles().size())
                .mapToObj(i -> this.getBoard().getColorAdjacent(false, i))
                .collect(Collectors.toSet());
        // For each color set the creature could move to, make a new game
        for(Set<Color> c : colors) {
            Game copy = new Game(this);
            copy.getBoard().moveToColor(c);
            copy.setLastMove("CREATUREUPDATE|" + board.hashCode());
            games.add(copy);
        }

        // Select games where the creature does not lose (present in more than one place)
        Set<Game> nonLoss = games.stream().filter(g -> {
            return g.getBoard().getInactiveRowTiles().stream().filter(Tile::isCreaturePresent).mapToInt(t -> 1).sum() > 1;
        }).collect(Collectors.toSet());
        // Select games where the creature wins
        Set<Game> win = games.stream().filter(g -> {
            return g.getBoard().getInactiveRowTiles().get(0).isCreaturePresent() &&
                    g.getBoard().getInactiveRowTiles().get(g.getBoard().getInactiveRowTiles().size()-1).isCreaturePresent() &&
                    g.getBoard().size() == g.getBoard().getMaxWidth();
        }).collect(Collectors.toSet());
        // If winning moves exist, only take the winning moves
        if(win.size() > 0) {
            return win;
        }
        // If it is possible, opt to not lose if we can't win
        if(nonLoss.size() > 0) {
            return nonLoss;
        }
        // Otherwise I guess we should just lose :(
        return games;
    }

    public Set<Game> getScientistDrop() {
        // Get state if scientist drops either edge.
        // If edge shouldn't be dropped, just return current game state
        Set<Game> games = new HashSet<>();
        if(this.getBoard().size() == this.getBoard().getMaxWidth()) {
            // Board is as at max width, so do left and right drops
            Game dropLeft = new Game(this);
            if(!dropLeft.getBoard().getInactiveRowTiles().get(0).isCreaturePresent()) {
                List<Tile> tokenTiles = dropLeft.getBoard().getSideTiles(Board.Side.LEFT).stream()
                        .filter(Tile::isTokenPresent).collect(Collectors.toList());
                tokenTiles.forEach(t -> {
                    t.setTokenPresent(false);
                    dropLeft.getScientist().addToken();
                });
                // If edge has token, add token to scientist
                dropLeft.droppedCubes = dropLeft.getBoard().dropEdge(Board.Side.LEFT);
                dropLeft.setLastMove("SCIENTISTDROP|LEFT|" + board.hashCode());
                games.add(dropLeft);
            }
            Game dropRight = new Game(this);
            if(!dropRight.getBoard().getInactiveRowTiles().get(dropRight.getBoard().getInactiveRowTiles().size()-1)
                    .isCreaturePresent()) {
                List<Tile> tokenTiles = dropRight.getBoard().getSideTiles(Board.Side.RIGHT).stream()
                        .filter(Tile::isTokenPresent).collect(Collectors.toList());
                tokenTiles.forEach(t -> {
                    t.setTokenPresent(false);
                    dropRight.getScientist().addToken();
                });
                dropRight.droppedCubes = dropRight.getBoard().dropEdge(Board.Side.RIGHT);
                games.add(dropRight);
                dropRight.setLastMove("SCIENTISTDROP|RIGHT|" + board.hashCode());
            }
            if(games.size() == 0) {
                // If it isn't possible to drop and edge, return current gamestate.
                // Will lead to creature win during state change
                Game copy = new Game(this);
                copy.setLastMove("SCIENTISTDROP|NONE");
                games.add(copy);
            }
        } else {
            games.add(new Game(this));
        }
        return games;
    }

    public Set<Game> getFreePlace() {
        // Return all the states in which the dropped cubes could be placed.
        // If no dropped cubes, just return current state
        Set<Game> games = new HashSet<>();
        if(this.getDroppedCubes().size() == 0) {
            games.add(new Game(this));
            return games;
        }
        for(Board board : this.placeCubes(new HashSet<>(Arrays.asList(new Board(this.getBoard()))),
                this.getDroppedCubes().stream().map(Cube::new).collect(Collectors.toList()),
                this.getBoard())) {
            Game copy = new Game(this);
            copy.setBoard(board);
            copy.setDroppedCubes(new ArrayList<>());
            copy.setLastMove("CREATURE|FREEPLACE|" + board.hashCode());
            games.add(copy);
        }
        return games;
    }

    public Set<Game> getDrawTiles() {
        // Draw the top two tiles, place them either left or right
        List<Tile> tiles;
        Set<Game> games = new HashSet<>();
        Game lr = new Game(this);
        tiles = lr.getTiles().draw(2);
        lr.getBoard().addTiles(new Tile(tiles.get(0)), new Tile(tiles.get(1)));
        lr.setLastMove((lr.getFirstPlayer() == lr.getCreature() ? PlayerType.CREATURE.name() : PlayerType.SCIENTIST.name()) +
                "|PLACETILES|" + board.hashCode());
        lr.toggleFirstPlayer();
        Game rl = new Game(this);
        tiles = rl.getTiles().draw(2);
        rl.getBoard().addTiles(new Tile(tiles.get(1)), new Tile(tiles.get(0)));
        rl.setLastMove((rl.getFirstPlayer() == rl.getCreature() ? PlayerType.CREATURE.name() : PlayerType.SCIENTIST.name()) +
                "|PLACETILES|" + board.hashCode());
        rl.toggleFirstPlayer();
        games.addAll(Arrays.asList(lr, rl));
        return games;
    }

    public Set<Game> getCreatureToken() {
        // Creature can take on of the possible tokens.
        List<Integer> tokenIdx = this.getBoard().getCreatureTokens();
        Set<Game> games = new HashSet<>();
        for(Integer idx: tokenIdx) {
            Game copy = new Game(this);
            Tile tile = copy.getBoard().getInactiveRowTiles().get(idx);
            tile.setTokenPresent(false);
            copy.getCreature().addToken();
            copy.setLastMove("CREATURE|TAKETOKEN|" + idx.toString());
            games.add(copy);
        }
        if(games.size() == 0) {
            // If no token which can be taken, just keep current state
            games.add(new Game(this));
        }
        return games;
    }

    public Set<Game> getTiebreaker() {
        // Count number of token on bottom row
        this.getBoard().getBottomRowTiles().stream()
                .filter(Tile::isTokenPresent)
                .forEach(t -> this.getCreature().addToken());
        this.getBoard().getTopRowTiles().stream()
                .filter(Tile::isTokenPresent)
                .forEach(t -> this.getScientist().addToken());
        return new HashSet<>(Arrays.asList(this));
    }

    private Set<Board> placeCubes(Set<Board> finalBoards, List<Cube> cubes, Board board) {
        // Get the topmost cube
        Cube cube = cubes.get(cubes.size() - 1);
        cubes.remove(cubes.size() - 1);
        // Recursively place cubes
        for(int i = 0; i < board.getInactiveRowSpaces().size(); i++) {
            // Create a board copy and place cube on space i
            Board copy = new Board(board);
            copy.getInactiveRowSpaces().get(i).addCube(cube);
            if(cubes.size() == 0) {
                finalBoards.add(copy);
            } else {
                // Recursively placed remaining cubes. Use a copy of cube list.
                placeCubes(finalBoards, cubes.stream().map(Cube::new).collect(Collectors.toList()), copy);
            }
        }
        return finalBoards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return new EqualsBuilder()
                .append(scientist, game.scientist)
                .append(creature, game.creature)
                .append(board, game.board)
                .append(tiles, game.tiles)
                .append(activePlayer, game.activePlayer)
                .append(state, game.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(scientist)
                .append(creature)
                .append(board)
                .append(tiles)
                .append(activePlayer)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "===Game=== \n" +
                "+scientist\n" + scientist +
                "\n+creature\n" + creature +
                "\n+board\n" + board +
                "\n+tiles\n" + tiles +
                "\n+activePlayer\n" + (this.activePlayer == this.scientist ? "SCIENTIST" : "CREATURE") +
                "\n+state=" + state;
    }

}
