public enum PlayerType {
    SCIENTIST (4, 3, 3, 2, 5, 0, false),
    CREATURE (4, 3, 2, 3, 5, 0, false);

    private final int handLimit;
    private final int startingHand;
    private final int moveAll;
    private final int moveOne;
    private final int split;
    private final int place;
    private final boolean wildcardReady;

    PlayerType(int handLimit, int startingHand, int moveAll, int moveOne, int split, int place, boolean wildcardReady) {
        this.handLimit = handLimit;
        this.startingHand = startingHand;
        this.moveAll = moveAll;
        this.moveOne = moveOne;
        this.split = split;
        this.wildcardReady = wildcardReady;
        this.place = place;
    }

    public int getHandLimit() {
        return handLimit;
    }

    public int getStartingHand() {
        return startingHand;
    }

    public int getMoveAll() {
        return moveAll;
    }

    public int getMoveOne() {
        return moveOne;
    }

    public int getSplit() {
        return split;
    }

    public boolean isWildcardReady() {
        return wildcardReady;
    }

    public int getPlace() {
        return place;
    }
}
