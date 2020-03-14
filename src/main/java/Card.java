import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class Card {
    protected abstract Set<Move> getPossibleMoves(Game currentGame, boolean wildcard);

    protected abstract Card makeCopy();

    protected Set<Move> getUniqueMoves(Game currentGame, boolean wildcard) {
        // Eliminate duplicated moves (different blue cubes being moved but ending up in same configuration)
        Set<Move> possMoves = this.getPossibleMoves(currentGame, wildcard);
        Set<Move> distinctMoves = possMoves.stream()
                .collect(Collectors.toSet());
        return distinctMoves;
    }

    protected Game playWildcard(Game game) {
        game.getActivePlayer().setWildcardReady(false);
        return game;
    }

    protected Game playHandCard(Game game) {
        game.getActivePlayer().getHand().getItems().remove(this);
        game.getActivePlayer().getDeck().getDiscard().add(this);
        return game;
    }

    protected String moveString(Game game) {
        String moveDesc = (game.getActivePlayer() == game.getCreature() ? PlayerType.CREATURE.name() :
                PlayerType.SCIENTIST.name()) + "|" + this.getClass().getName() + "|" + game.getBoard().hashCode();
        return moveDesc;
    }

    protected Game playCard(Game game, boolean wildcard) {
        if(wildcard) {
            game = this.playWildcard(game);
        } else {
            game = this.playHandCard(game);
        }
        game.toggleActivePlayer();
        // Make a string so we can uniquely identify the move just made (card played + board state moved into)
        game.setLastMove(this.moveString(game));
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return new EqualsBuilder()
                .append(this.toString(), card.toString())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.toString())
                .toHashCode();
    }
}
