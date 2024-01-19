package gogame;

public enum Color {

    EMPTY("\u001B[37m" + "◌" + "\u001B[0m"), BLACK("○"), WHITE("●");
    private final String symbol;

    Color(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the other Color.
     * @return the other color is this mark is not EMPTY
     */
    //@ ensures this == BLACK ==> \result == WHITE && this == WHITE ==> \result == BLACK;
    public Color other() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }

    @Override
    public String toString() {
        return symbol;
    }
}


