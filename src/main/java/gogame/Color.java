package gogame;

public enum Color {

  EMPTY("\u001B[37m" + "◌" + "\u001B[0m"), BLACK("\u001B[30m" + "●" + "\u001B[0m"), WHITE("●"),
  NEUTRAL("\u001B[37m" + "○" + "\u001B[0m");
  private final String symbol;

  Color(String symbol) {
    this.symbol = symbol;
  }

  /**
   * Returns the other Color.
   *
   * @return the other color is this mark is not EMPTY
   */
  public Color other() {
    if (this == BLACK) {
      return WHITE;
    } else if (this == WHITE) {
      return BLACK;
    } else {
      return EMPTY;
    }
  }

  public String toSymbol() {
    return symbol;
  }
}


