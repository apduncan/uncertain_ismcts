import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.scene.input.KeyCode.T;

public class Player {
    // Represents things one of the player has
    private int handLimit;
    // Deck objects include a discard pile, so not represented
    private Deck<Card> deck;
    private Deck<Card> hand;
    private int tokens;
    private boolean wildcardReady;

    public Player(int cardMoveOne, int cardMoveAll, int cardSplit, int handLimit, boolean wildcardReady, int tokens,
                  int startingHandsize) {
        this.handLimit = handLimit;
        this.tokens = tokens;
        this.wildcardReady = wildcardReady;
        // Set up deck and draw starting cards
        this.deck = new Deck<>();
        this.deck.addToDeck(IntStream.range(0, cardMoveAll).mapToObj(i -> new MoveAllCard()).collect(Collectors.toList()));
        this.deck.addToDeck(IntStream.range(0, cardMoveOne).mapToObj(i -> new MoveOneCard()).collect(Collectors.toList()));
        this.deck.addToDeck(IntStream.range(0, cardSplit).mapToObj(i -> new SplitCard()).collect(Collectors.toList()));
        // A new player should shuffle their deck - don't do this with a copy constructor
        this.deck.shuffleDeck();
        this.hand = new Deck<>();
        // Draw starting hand
        this.drawCards(startingHandsize);
    }

    public Player(Player player) {
        // Copy constructor
        this.handLimit = player.handLimit;
        this.wildcardReady = player.wildcardReady;
        this.tokens = player.tokens;
        this.deck = new Deck<>();
        this.hand = new Deck<>();
        player.deck.getItems().forEach(c -> this.deck.addToDeck(c.makeCopy()));
        player.deck.getDiscard().forEach(c -> this.deck.getDiscard().add(c.makeCopy()));
        player.hand.getItems().forEach(c -> this.hand.addToDeck(c.makeCopy()));
    }

    public Player(PlayerType type) {
        this(type.getMoveOne(), type.getMoveAll(), type.getSplit(), type.getHandLimit(), type.isWildcardReady(),
                0, type.getStartingHand());
    }

    public int getHandLimit() {
        return handLimit;
    }

    public void setHandLimit(int handLimit) {
        this.handLimit = handLimit;
    }

    public Deck<Card> getDeck() {
        return deck;
    }

    public void setDeck(Deck<Card> deck) {
        this.deck = deck;
    }

    public Deck<Card> getHand() {
        return hand;
    }

    public void setHand(Deck<Card> hand) {
        this.hand = hand;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void addToken() {
        this.tokens++;
        this.setWildcardReady(true);
    }

    public boolean isWildcardReady() {
        return wildcardReady;
    }

    public void setWildcardReady(boolean wildcardReady) {
        this.wildcardReady = wildcardReady;
    }

    public List<Card> drawCards(int n) {
        // Determine how many required to draw, if would draw above hand limit, draw to instead
        int n_draw = Math.min(n, this.handLimit - this.hand.size());
        // Draw n cards. If deck has less than n, draw, shuffle, draw
        // Places cards into hand and returns them as a list
        int firstDraw = Math.min(this.deck.size(), n_draw);
        List<Card> drawn = this.deck.draw(firstDraw);
        if (firstDraw < n_draw) {
            this.deck.shuffleDeckAndDiscards();
            drawn.addAll(this.deck.draw(n_draw - firstDraw));
        }
        this.hand.addToDeck(drawn);
        return drawn;
    }

    public List<Card> uniqueCards() {
        return new ArrayList(
                this.hand.getItems().stream().collect(Collectors.toMap(Card::toString, p -> p, (p, q) -> p)).values()
        );
    }

    public Player cloneAndRandomise(boolean incldueHand) {
        // Pool all the card which are not visible to the player being considered
        // includeHand indicates whether to shuffle hand and deck together
        int handSize = this.getHand().size();
        int deckSize = this.getDeck().size();
        // Copy cards into a list to be redealt
        List<Card> cards = this.getDeck().getItems().stream().map(Card::makeCopy).collect(Collectors.toList());
        if(incldueHand) {
            cards.addAll(this.getHand().getItems().stream().map(Card::makeCopy).collect(Collectors.toList()));
        }
        Player clone = new Player(this);
        // Shuffle the pooled unseen cards
        Collections.shuffle(cards);
        // Make a new list of card for the deck
        List<Card> newDeck = IntStream.range(0, deckSize).mapToObj(cards::get).collect(Collectors.toList());
        clone.getDeck().setItems(newDeck);
        if(incldueHand) {
            // Make a list of new cards for the hand
            List<Card> newHand = IntStream.range(deckSize, deckSize + handSize).mapToObj(cards::get)
                    .collect(Collectors.toList());
            clone.getHand().setItems(newHand);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "Player\t" +
                "handLimit=" + handLimit +
                "\tdeck=" + deck +
                "\thand=" + hand +
                "\ttokens=" + tokens +
                "\twildcardReady=" + wildcardReady;
    }
}
