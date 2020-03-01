import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Card {
    protected abstract Set<Board> getPossibleMoves(Board currentBoard);

    protected abstract Card makeCopy();

    protected Set<Board> getUniqueMoves(Board currentBoard) {
        // Eliminate duplicated moves (different blue cubes being moved but ending up in same configuration)
        Set<Board> possBoards = this.getPossibleMoves(currentBoard);
        Set<Board> distinctBoards = new HashSet<>(possBoards.stream()
                .collect(Collectors.toMap(b -> b.toString(), b -> b))
                .values());
        return distinctBoards;
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
}
