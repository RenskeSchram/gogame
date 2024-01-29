package gogame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Board for the Go Game.
 */
public class Board {

  /**
   * Initialisation parameters of the board.
   */
  public int DIM;
  public final Color[][] fields;

  /**
   * Constructor for an empty Board.
   * For all fields, set field to Color.EMPTY
   */
  public Board(int DIM) {
    this.DIM = DIM;
    fields = new Color[DIM][DIM];
    for (int row = 0; (row >= 0) && (row < DIM); row++) {
      for (int col = 0; (col >= 0) && (col < DIM); col++) {
        setField(new int[]{col, row}, Color.EMPTY);
      }
    }
  }

  /**
   * Check if intersection is a valid intersection on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return true if intersection is a valid field on the Board.
   */


  protected boolean isField(int[] intersection) {
    return intersection[0] >= 0 && intersection[0] < DIM && intersection[1] >= 0 && intersection[1] < DIM;
  }

  /**
   * Check if intersection is a valid field on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return true if intersection is a valid field on the Board.
   */


  protected boolean isEmpty(int[] intersection) {
    if (isField(intersection)) {
      return getField(intersection) == Color.EMPTY;
    } else {
      return false;
    }
  }

  /**
   * Place the color on the given intersection (row, col) on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @param color    color of placed move.
   *                 //@requires isField(row, col)
   */

  public void setField(int[] intersection, Color color) {
    if (isField(intersection)) {
      this.fields[intersection[0]][intersection[1]] = color;
    }
  }

  /**
   * Returns the Color of the field (row, col).
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return Color of the field with row and col
   * // requires: isField(row, col)
   */
  protected Color getField(int[] intersection) {
    if (isField(intersection)) {
      return fields[intersection[0]][intersection[1]];
    } else {
      return null;
    }
  }

  /**
   * Returns if it is a valid move to make on the board.
   * // requires: isField(row, col) && isEmpty(row, col)
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return
   */
  public boolean isValid(int[] intersection) {
    return isField(intersection) && isEmpty(intersection);
  }


  /**
   * Returns if the stone has a liberty (empty adjacent stone).
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return if the stone has a liberty (empty adjacent stone).
   */

//  protected boolean hasLiberty(int[] intersection) {
//    boolean liberty = false;
//    // get the color for all adjacent intersections
//    for (int[] adjacent : getAdjacentIntersections(intersection)) {
//      // check if color is EMPTY, meaning there is a liberty
//      if (isEmpty(adjacent)) { liberty = true; }
//    }
//    return liberty;
//  }
  public boolean isSingleSuicide(int[] intersection) {
    return getLibertiesGroup(getGroup(intersection, true)).isEmpty();
  }

  /**
   * Get the group of stones (or empty intersections). If the game is active, groups are formed by
   * colored stones (for captures), if the game is ended, (territory) groups are formed by the
   * empty stones.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @param active   activity status of game
   * @return List of coordinates of intersections in group
   */

  public List<int[]> getGroup(int[] intersection, boolean active) {
    List<int[]> group = new ArrayList<>();

    if (active) {
      if (getField(intersection) != Color.EMPTY) {
        group.add(intersection);
      }
    } else {
      if (getField(intersection) == Color.EMPTY) {
        group.add(intersection);
      }
    }

    for (int[] adjacent : getAdjacentIntersections(intersection)) {
      exploreAdjacentStones(group, adjacent, getField(intersection));
    }

    return group;
  }

  /**
   * Explore adjacent intersections of current and add to list if these belong to the same group.
   *
   * @param group   group of intersections
   * @param current current intersection to be checked
   * @param color   color of the group
   */
  private void exploreAdjacentStones(List<int[]> group, int[] current, Color color) {
    // check if the intersection is not already on the list and is the same color
    if (!containsIntersection(group, current) && (getField(current) == color)) {
      group.add(current);

      // recursive check for all adjacent intersections of new to the group added intersections
      for (int[] adjacent : getAdjacentIntersections(current)) {
        if (getField(adjacent) == getField(current)) {
          exploreAdjacentStones(group, adjacent, color);
        }
      }
    }
  }


  /**
   * Returns if coordinate is already in the list.
   *
   * @param list     list to check.
   * @param intersection intersection saved as int[] {col, row}
   * @return if coordinate is already in the list.
   */
  static boolean containsIntersection(List<int[]> list, int[] intersection) {
    for (int[] element : list) {
      if (Arrays.equals(element, intersection)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns adjacent field coordinates on the board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return adjacent field coordinates on the board.
   */

  protected List<int[]> getAdjacentIntersections(int[] intersection) {
    List<int[]> adjacentStones = new ArrayList<>();
    int[] left = new int[]{intersection[0], intersection[1] - 1};
    if (isField(left)) {
      adjacentStones.add(left);
    }
    int[] right = new int[]{intersection[0], intersection[1] + 1};
    if (isField(right)) {
      adjacentStones.add(right);
    }
    int[] up = new int[]{intersection[0] + 1, intersection[1]};
    if (isField(up)) {
      adjacentStones.add(up);
    }
    int[] down = new int[]{intersection[0] - 1, intersection[1]};
    if (isField(down)) {
      adjacentStones.add(down);
    }
    return adjacentStones;
  }


  /**
   * Return the captured stones. All adjacent stones of the placed stone are checked for being
   * part of a group and checking if any of the stones in the group has liberties. If all stones
   * in the group are out of liberties, the group is captured. All captured stones are returned.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return list of int[]{row, col} of captured stones
   */

  public List<int[]> getCaptured(int[] intersection) {
    List<int[]> captured = new ArrayList<>();

    for (int[] adjacent : getAdjacentIntersections(intersection)) {
      List<int[]> group = getGroup(adjacent, true);

      if (getLibertiesGroup(group).isEmpty()) {
        captured.addAll(group);
      }
    }
    return captured;
  }

  public List<int[]> getLibertiesGroup(List<int[]> group) {
    List<int[]> groupLiberties = new ArrayList<>();
    for (int[] stone : group) {
      if (!getLibertiesStone(stone).isEmpty()) {
        groupLiberties.addAll(getLibertiesStone(stone));
      }
    }
    return groupLiberties;
  }

  public List<int[]> getLibertiesStone(int[] stone) {
    List<int[]> stoneLiberties = new ArrayList<>();
    for (int[] adjacent : getAdjacentIntersections(stone)) {
      if (isEmpty(adjacent)) {
        stoneLiberties.add(adjacent);
      }
    }
    return stoneLiberties;
  }

  /**
   * Set fields of captured stones to empty.
   *
   * @param captured list of coordinates of captured stones.
   */
  public void removeCaptured(List<int[]> captured) {
    for (int[] capture : captured) {
      setField(capture, Color.EMPTY);
    }
  }


  /**
   * Creates a deep copy of this field.
   */
    /*@ ensures \result != this;
     ensures (\forall int i; (i >= 0 && i < DIM*DIM); \result.fields[i] == this.fields[i]);
     @*/
  public Board deepCopy() {
    Board copy = new Board(DIM);
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        copy.setField(new int[]{col, row}, this.getField(new int[]{col, row}));
      }
    }
    return copy;
  }


  /**
   * Returns a String representation of the fields array.
   *
   * @return
   */
  public String toString() {
    StringBuilder stringBoard = new StringBuilder();
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        stringBoard.append(fields[col][row].toSymbol()).append("  ");
      }
      stringBoard.append("\n");
    }
    stringBoard.append("\n");
    return stringBoard.toString();
  }

  /**
   * Determine winner of the board, by determining the final territories and counting the stones.
   *
   * @return Color of winning ServerPlayer or Color.NEUTRAL in case of a draw
   */
  public Color getWinner() {
    // get final board with all assigned territories
    getFilledBoard();

    // return winner
    if (getStonesWithThisColor(Color.WHITE).size() > getStonesWithThisColor(Color.BLACK).size()) {
      return Color.WHITE;
    } else if (getStonesWithThisColor(Color.WHITE).size() < getStonesWithThisColor(Color.BLACK).size()) {
      return Color.BLACK;
    } else {
      return Color.NEUTRAL;
    }
  }

  public List<int[]> getStonesWithThisColor(Color color) {
    List<int[]> stoneWithThisColor = new ArrayList<>();
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        if (getField(new int[]{col, row}) == color) {
          stoneWithThisColor.add(new int[]{col, row});
        }
      }
    }
    return stoneWithThisColor;
  }


  /**
   * Determining the final territories and assign a Color to all intersections on the board.
   */
  public void getFilledBoard() {
    // loop over the board
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {

        // find next empty intersection on the board and create new group
        if (getField(new int[]{col, row}) == Color.EMPTY) {
          List<int[]> group = new ArrayList<>();

          // find all adjacent empty intersections and add to group
          exploreAdjacentStones(group, new int[]{col, row}, Color.EMPTY);

          // get and set territory color for group
          if (!group.isEmpty()) {
            Color territoryColor = getTerritoryColor(group);
            for (int[] emptySpot : group) {
              setField(emptySpot, territoryColor);
            }
          }
        }
      }
    }
  }

  /**
   * Get the assigned color of the territory.
   *
   * @param group list with intersection coordinates
   * @return the assigned color of the territory
   */
  public Color getTerritoryColor(List<int[]> group) {
    // for all stones in group get adjacent stone color
    List<Color> colors = new ArrayList<>();
    for (int[] stone : group) {
      for (int[] adjacentStone : getAdjacentIntersections(stone)) {
        if (getField(adjacentStone) != Color.EMPTY) {
          colors.add(getField(adjacentStone));
        }
      }
    }
    // check if the colors surrounding the group are all the same
    // if true, the territory belongs to that color, otherwise the territory is neutral.
    boolean correctTerritory = true;
    for (Color color : colors) {
      if (color != colors.get(0)) {
        correctTerritory = false;
        break;
      }
    }
    if (correctTerritory && !colors.isEmpty()) {
      return colors.get(0);
    } else {
      return Color.NEUTRAL;
    }
  }

  public void doResign(Color color) {
    for (int row = 0; (row >= 0) && (row < DIM); row++) {
      for (int col = 0; (col >= 0) && (col < DIM); col++) {
        setField(new int[]{col, row}, color.other());
      }
    }
  }
}