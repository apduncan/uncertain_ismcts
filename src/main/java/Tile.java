import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Tile {
    private boolean token;
    private boolean tokenPresent;
    private boolean creaturePresent;

    public Tile(boolean token) {
        // Initialise a tile to go in the deck - no adjacent tiles / spaces etc
        this.token = token;
        // If this is a token tile, mark that it has a token on it
        this.tokenPresent = token;
        this.creaturePresent = false;
    }

    public Tile(boolean token, boolean tokenPresent, boolean creaturePresent) {
        this.token = token;
        this.tokenPresent = tokenPresent;
        this.creaturePresent = creaturePresent;
    }

    public Tile(Tile tile) {
        // Copies a tile but not it's neighbours, those must be set separately
        this(tile.isToken(), tile.isTokenPresent(), tile.isCreaturePresent());
    }

    @Override
    public String toString() {
        return "["
                + (this.isCreaturePresent() ? "O" : "")
                + (this.isTokenPresent() ? "*": "")
                + (this.isToken() && !this.isTokenPresent() ? "^" : "")
                + "]";
    }

    public boolean isToken() {
        return token;
    }

    public void setToken(boolean token) {
        this.token = token;
    }

    public boolean isTokenPresent() {
        return tokenPresent;
    }

    public void setTokenPresent(boolean tokenPresent) {
        this.tokenPresent = tokenPresent;
    }

    public boolean isCreaturePresent() {
        return creaturePresent;
    }

    public void setCreaturePresent(boolean creaturePresent) {
        this.creaturePresent = creaturePresent;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(token)
                .append(tokenPresent)
                .append(creaturePresent)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        return new EqualsBuilder()
                .append(token, tile.token)
                .append(tokenPresent, tile.tokenPresent)
                .append(creaturePresent, tile.creaturePresent)
                .isEquals();
    }
}
