import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Deck<T> {
    private List<T> items;
    private List<T> discard;

    public Deck() {
        this.items = new ArrayList<>();
        this.discard = new ArrayList<>();
    }

    public Deck(Collection<T> add) {
        this();
        this.items.addAll(add);
    }

    public Deck(Deck deck) {
        this();
        this.items.addAll(deck.getItems());
        this.discard.addAll(deck.getDiscard());
    }

    public void shuffleDeck() {
        Collections.shuffle(this.items);
    }

    public void shuffleDeckAndDiscards() {
        this.items.addAll(this.discard);
        this.discard = new ArrayList<>();
        this.shuffleDeck();
    }

    public void addToDeck(Collection<T> add) {
        this.items.addAll(add);
    }

    public void addToDeck(T add) {
        this.addToDeck(Arrays.asList(add));
    }

    public List<T> draw(int n) {
        // Draw as many as possible
        List<T> drawn = this.items.stream().limit(n).collect(Collectors.toList());
        // Make a new list of items excluding those drawn. Cannot use removeAll, as cards / tiles of the same
        // type are set to be equal, meaning it would remove all cards / tiles of that type
        this.items = IntStream.range(n, this.items.size()).mapToObj(this.items::get).collect(Collectors.toList());
        return drawn;
    }

    public T draw() {
        return this.draw(1).get(0);
    }

    public void discard(Collection<T> discards) {
        this.discard.addAll(discards);
    }

    public void discard(T discard) {
        this.discard(Arrays.asList(discard));
    }

    public int size() {
        return this.items.size();
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public List<T> getDiscard() {
        return discard;
    }

    public void setDiscard(List<T> discard) {
        this.discard = discard;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "items=" + this.items.stream().map(Object::toString).collect(Collectors.joining("")) +  ", " +
                "discard=" + this.discard.stream().map(Object::toString).collect(Collectors.joining("")) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Deck<?> deck = (Deck<?>) o;

        return new EqualsBuilder()
                .append(items, deck.items)
                .append(discard, deck.discard)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(items)
                .append(discard)
                .toHashCode();
    }
}
