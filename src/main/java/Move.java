import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Function;

public class Move {
    private Function<Game, Game> move;
    private String desc;

    public Move(Function<Game, Game> move, String desc) {
        this.move = move;
        this.desc = desc;
    }

    public Game makeMove(Game game) {
        game = this.getMove().apply(game);
        game.setState(game.getState().nextState(game, game));
        return game;
    }

    public Function<Game, Game> getMove() {
        return move;
    }

    public void setMove(Function<Game, Game> move) {
        this.move = move;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        return new EqualsBuilder()
                .append(desc, move.desc)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(desc)
                .toHashCode();
    }
}
