import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board {
    public static int DEFAULT_MAX_WIDTH = 7;
    private List<Tile> topRowTiles;
    private List<Tile> bottomRowTiles;
    private List<Space> topRowSpaces;
    private List<Space> bottomRowSpaces;
    private List<Tile> activeRowTiles;
    private List<Space> activeRowSpaces;
    private int maxWidth;
    public enum Side {LEFT, RIGHT};

    public Board(int maxWidth, Deck<Tile> deck, List<Cube> leftCubes, List<Cube> rightCubes) {
        // The board is made of two rows - top and bottom
        // The active row will always be one shorter than the other
        // The first tile / space in the list will be the leftmost
        this.maxWidth = maxWidth;
        this.initBoard(deck, leftCubes, rightCubes);
    }

    public Board(Deck<Tile> deck, List<Cube> leftCubes, List<Cube> rightCubes) {
        this(DEFAULT_MAX_WIDTH, deck, leftCubes, rightCubes);
    }

    public Board(Board board) {
        // Copy constructor for board
        this.maxWidth = board.getMaxWidth();
        this.topRowTiles = board.topRowTiles.stream()
                .map(Tile::new)
                .collect(Collectors.toList());
        this.bottomRowTiles = board.bottomRowTiles.stream()
                .map(Tile::new)
                .collect(Collectors.toList());
        this.topRowSpaces = board.topRowSpaces.stream()
                .map(Space::new)
                .collect(Collectors.toList());
        this.bottomRowSpaces = board.bottomRowSpaces.stream()
                .map(Space::new)
                .collect(Collectors.toList());
        this.activeRowSpaces = board.bottomRowSpaces == board.activeRowSpaces ? this.bottomRowSpaces : this.topRowSpaces;
        this.activeRowTiles = board.bottomRowTiles == board.activeRowTiles ? this.bottomRowTiles : this.topRowTiles;
    }

    private void initBoard(Deck<Tile> deck, List<Cube> leftCubes, List<Cube> rightCubes) {
        // Initialise tiles
        Tile startTile = deck.draw();
        Tile leftTopTile = deck.draw();
        Tile rightTopTile = deck.draw();
        startTile.setCreaturePresent(true);
        this.bottomRowTiles = new ArrayList<>(Arrays.asList(startTile));
        this.bottomRowSpaces = new ArrayList<>(Arrays.asList(new Space(leftCubes), new Space(rightCubes)));
        this.topRowTiles = new ArrayList<>(Arrays.asList(leftTopTile, rightTopTile));
        this.topRowSpaces = IntStream.range(0, 3).mapToObj(i -> new Space()).collect(Collectors.toList());
        this.activeRowSpaces = bottomRowSpaces;
        this.activeRowTiles = bottomRowTiles;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public List<Tile> getActiveRowTiles() {
        return this.activeRowTiles;
    }

    public List<Space> getActiveRowSpaces() {
        return this.activeRowSpaces;
    }

    public List<Tile> getInactiveRowTiles() {
        return this.activeRowTiles == this.bottomRowTiles ? this.topRowTiles : this.bottomRowTiles;
    }

    public List<Space> getInactiveRowSpaces() {
        return this.activeRowSpaces == this.bottomRowSpaces ? this.topRowSpaces : this.bottomRowSpaces;
    }

    public List<Tile> getRowTiles(boolean activeRow) {
        return activeRow ? this.getActiveRowTiles() : this.getInactiveRowTiles();
    }

    public List<Space> getRowSpaces(boolean activeRow) {
        return activeRow ? this.getActiveRowSpaces() : this.getInactiveRowSpaces();
    }

    public boolean isTileInActiveRow(Tile tile) {
        return this.activeRowTiles.contains(tile);
    }

    public boolean isTileInInactiveRow(Tile tile) {
        return !this.isTileInActiveRow(tile);
    }

    public boolean isSpaceInActiveRow(Space space) {
        return this.activeRowSpaces.contains(space);
    }

    public boolean isSpaceInInactiveRow(Space space) {
        return !this.isSpaceInActiveRow(space);
    }

    public List<Tile> getRow(Tile tile) {
        // Get the row which contains this tile
        return this.isTileInActiveRow(tile) ? this.getActiveRowTiles() : this.getInactiveRowTiles();
    }

    public List<Space> getRow(Space space) {
        return this.isSpaceInActiveRow(space) ? this.getActiveRowSpaces() : this.getInactiveRowSpaces();
    }

    public Tile getTile(boolean activeRow, int tile) throws IndexOutOfBoundsException {
        List<Tile> row = this.getRowTiles(activeRow);
        if(tile < 0 || tile > row.size()-1) {
            throw new IndexOutOfBoundsException("Tile index out of bounds");
        }
        return row.get(tile);
    }

    public Space getSpace(boolean activeRow, int space) {
        List<Space> row = this.getRowSpaces(activeRow);
        if(space < 0 || space > row.size()-1) {
            throw new IndexOutOfBoundsException("Space index out of bounds");
        }
        return row.get(space);
    }

    public List<Space> getSpacesAdjacent(Tile tile) {
        boolean activeRow = this.isTileInActiveRow(tile);
        int idx = (activeRow ? this.getActiveRowTiles() : this.getInactiveRowTiles()).indexOf(tile);
        return this.getSpacesAdjacent(activeRow, idx);
    }

    public List<Space> getSpacesAdjacent(boolean activeRow, int tile) {
        return Arrays.asList(this.getRowSpaces(activeRow).get(tile), this.getRowSpaces(activeRow).get(tile+1));
    }

    public Set<Color> getColorAdjacent(Tile tile) {
        return this.colorUnion(this.getSpacesAdjacent(tile));
    }

    public Set<Color> getColorAdjacent(boolean activeRow, int tile) {
        return this.getColorAdjacent(this.getTile(activeRow, tile));
    }

    public int getNumberAdjacent(Tile tile) {
        return this.getSpacesAdjacent(tile).stream().mapToInt(Space::getCubeCount).sum();
    }

    public int getNumberAdjacent(boolean activeRow, int tile) {
        return this.getNumberAdjacent(this.getTile(activeRow, tile));
    }

    public List<Tile> getTopRowTiles() {
        return topRowTiles;
    }

    public void setTopRowTiles(List<Tile> topRowTiles) {
        this.topRowTiles = topRowTiles;
    }

    public List<Tile> getBottomRowTiles() {
        return bottomRowTiles;
    }

    public void setBottomRowTiles(List<Tile> bottomRowTiles) {
        this.bottomRowTiles = bottomRowTiles;
    }

    public List<Space> getTopRowSpaces() {
        return topRowSpaces;
    }

    public void setTopRowSpaces(List<Space> topRowSpaces) {
        this.topRowSpaces = topRowSpaces;
    }

    public List<Space> getBottomRowSpaces() {
        return bottomRowSpaces;
    }

    public void setBottomRowSpaces(List<Space> bottomRowSpaces) {
        this.bottomRowSpaces = bottomRowSpaces;
    }

    public int size() {
        return this.getActiveRowTiles().size() + this.getInactiveRowTiles().size();
    }

    private Set<Color> colorUnion(List<Space> spaces) {
        return spaces.stream().map(Space::getColors).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private void toggleRows() {
        this.activeRowTiles = this.getInactiveRowTiles();
        this.activeRowSpaces = this.getInactiveRowSpaces();
    }

    public List<Cube> dropEdge(Board.Side side) {
        List<Space> spaces = null;
        List<Tile> tiles = null;
        if(side == Side.LEFT) {
            spaces = Arrays.asList(this.topRowSpaces.get(0), this.bottomRowSpaces.get(0));
            tiles = Arrays.asList(this.topRowTiles.get(0), this.bottomRowTiles.get(0));
        } else {
            spaces = Stream.of(this.topRowSpaces, this.bottomRowSpaces)
                    .map(r -> r.get(r.size()-1))
                    .collect(Collectors.toList());
            tiles = Stream.of(this.topRowTiles, this.bottomRowTiles)
                    .map(r -> r.get(r.size()-1))
                    .collect(Collectors.toList());
        }
        this.topRowSpaces.removeAll(spaces);
        this.bottomRowSpaces.removeAll(spaces);
        this.topRowTiles.removeAll(tiles);
        this.bottomRowTiles.removeAll(tiles);
        return spaces.stream()
                .flatMap(s -> s.getCubes().stream())
                .collect(Collectors.toList());
    }

    public void addTiles(Tile left, Tile right) {
        this.activeRowTiles.add(0, left);
        this.activeRowTiles.add(right);
        this.activeRowSpaces.add(0, new Space());
        this.activeRowSpaces.add(new Space());
        this.toggleRows();
    }

    public Set<Integer> getTilesAdjacentToCreature() {
        Set<Integer> adjacent = new HashSet<>(this.getTilesWithCreature());
        adjacent.addAll(adjacent.stream().map(i -> i + 1).collect(Collectors.toList()));
        return adjacent;
    }

    public List<Integer> getTilesWithCreature() {
        // Work out which tiles have creature presence
        List<Integer> presence = IntStream.range(0, this.getActiveRowTiles().size())
                .mapToObj(Integer::new)
                .filter(i -> this.getActiveRowTiles().get(i).isCreaturePresent())
                .collect(Collectors.toList());
        return presence;
    }

    public void moveToNumber(final int number) {
        // Move creature presence to inactive row tile meeting certain criteria
        // Work out which tiles have creature presence
        List<Integer> presence = this.getTilesWithCreature();
        // Inactive spaces which are equal to or +1 are adjacent
        Set<Integer> adjacent = this.getTilesAdjacentToCreature();
        // Filter adjacent spaces to those meeting the number requirements, and set present there
        adjacent.stream().filter(i -> this.getNumberAdjacent(false, i) == number)
                .forEach(i -> this.getInactiveRowTiles().get(i).setCreaturePresent(true));
        // Remove creature presence from the active row
        presence.forEach(i -> this.getActiveRowTiles().get(i).setCreaturePresent(false));
    }

    public void moveToColor(final Set<Color> colors) {
        List<Integer> presence = this.getTilesWithCreature();
        Set<Integer> adjacent = this.getTilesAdjacentToCreature();
        adjacent.stream().filter(i -> this.getColorAdjacent(false, i).equals(colors))
                .forEach(i -> this.getInactiveRowTiles().get(i).setCreaturePresent(true));
        presence.forEach(i -> this.getActiveRowTiles().get(i).setCreaturePresent(false));
    }

    public List<Tile> getSideTiles(final Board.Side side) {
        List<Tile> tiles = new ArrayList<>();
        if(side == Side.LEFT) {
            tiles.add(this.getInactiveRowTiles().get(0));
            tiles.add(this.getActiveRowTiles().get(0));
        } else {
            tiles.add(this.getInactiveRowTiles().get(this.getInactiveRowTiles().size()-1));
            tiles.add(this.getActiveRowTiles().get(this.getActiveRowTiles().size() - 1));
        }
        return tiles;
    }

    public List<Integer> getCreatureTokens() {
        // Return index of tiles which have both creature presence and tokens
        return IntStream.range(0, this.getInactiveRowTiles().size())
                .filter(i -> {
                    Tile tile = this.getInactiveRowTiles().get(i);
                    return tile.isCreaturePresent() && tile.isTokenPresent();
                }).mapToObj(Integer::new).collect(Collectors.toList());
    }

    private String tileString(Tile tile) {
        return this.getColorAdjacent(tile).stream()
                        .map(c -> String.valueOf(c.toString().charAt(0)))
                        .sorted(Comparator.comparing(c -> c.toString()))
                        .collect(Collectors.joining()) +
                "," +
                this.getNumberAdjacent(tile) +
                (tile.isToken() ? (tile.isTokenPresent() ? " ★" : " ☆") : "") +
                (tile.isCreaturePresent() ? " ☻" : "");
    }

    private List<String> rowToStrings(List<Tile> tiles, List<Space> spaces) {
        List<String> rowStrings = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            rowStrings.add(spaces.get(i).toString());
            rowStrings.add(this.tileString(tiles.get(i)));
        }
        rowStrings.add(spaces.get(spaces.size() - 1).toString());
        return rowStrings;
    }

    @Override
    public String toString() {
        List<String> boardTop = this.rowToStrings(this.topRowTiles, this.topRowSpaces);
        List<String> boardBottom = this.rowToStrings(this.bottomRowTiles, this.bottomRowSpaces);
        // Determine maximum width of tile or spaces
        int maxLen = Stream.of(boardTop, boardBottom)
                .flatMap(Collection::stream)
                .mapToInt(String::length)
                .max()
                .getAsInt();
        // Build board string
        String board = "";
        boolean padTop = boardTop.size() < boardBottom.size();
        board += this.padRow(boardTop, maxLen, padTop);
        board += "\n";
        board += this.padRow(boardBottom, maxLen, !padTop);
        return board;
    }

    private String padRow(List<String> strings, int width, boolean padEdge) {
        String row = "";
        row += padEdge ? StringUtils.leftPad("", width + 2) : "";
        for(int i = 0; i < strings.size(); i++) {
            String start = "[";
            String end = "]";
            if(Math.floorMod(i, 2) == 0) {
                start = "(";
                end = ")";
            }
            // Determine the index of the string to use
            row += start + StringUtils.leftPad(strings.get(i), width) + end;
        }
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return new EqualsBuilder()
                .append(this.toString(), board.toString())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.toString())
                .toHashCode();
    }
}
