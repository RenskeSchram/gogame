package gogame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Board for the Go Game.
 */
public class Board {

  /**
   * Initialisation parameters of the board.
   */
  private final int DIM;
  private final Color[][] intersections;


  /**
   * Constructor for an empty Board.
   * For all intersections, set to Color.EMPTY
   */
  public Board(int DIM) {
    this.DIM = DIM;
    intersections = new Color[DIM][DIM];
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        setStone(new int[]{col, row}, Color.EMPTY);
      }
    }
  }


  public int getDIM(){
    return DIM;
  }

  public Color[][] getIntersections() {
    return intersections;
  }

  /**
   * Creates a deep copy of this board's intersections.
   */
  public Board deepCopy() {
    Board copy = new Board(DIM);
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        copy.setStone(new int[]{col, row}, this.getStone(new int[]{col, row}));
      }
    }
    return copy;
  }

  /**
   * Returns a String representation of the intersections array.
   *
   * @return a String representation of the intersections array.
   */
  public String toString() {
    StringBuilder stringBoard = new StringBuilder();
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {
        stringBoard.append(intersections[col][row].toSymbol()).append("  ");
      }
      stringBoard.append("\n");
    }
    stringBoard.append("\n");
    return stringBoard.toString();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                  Intersections & Stones                                    ///
  //////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * Check if intersection is a valid intersection on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return true if intersection is a valid field on the Board.
   */
  protected boolean isIntersection(int[] intersection) {
    return intersection[0] >= 0 && intersection[0] < DIM && intersection[1] >= 0
        && intersection[1] < DIM;
  }

  /**
   * Check if intersection is a valid field on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return true if intersection is a valid field on the Board.
   */
  protected boolean isEmptyIntersection(int[] intersection) {
    if (isIntersection(intersection)) {
      return getStone(intersection) == Color.EMPTY;
    } else {
      return false;
    }
  }

  /**
   * Returns adjacent intersection coordinates on the board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return adjacent field coordinates on the board.
   */
  protected List<int[]> getAdjacentIntersections(int[] intersection) {
    List<int[]> adjacentIntersections = new ArrayList<>();
    int[] left = new int[]{intersection[0], intersection[1] - 1};
    if (isIntersection(left)) {
      adjacentIntersections.add(left);
    }
    int[] right = new int[]{intersection[0], intersection[1] + 1};
    if (isIntersection(right)) {
      adjacentIntersections.add(right);
    }
    int[] up = new int[]{intersection[0] + 1, intersection[1]};
    if (isIntersection(up)) {
      adjacentIntersections.add(up);
    }
    int[] down = new int[]{intersection[0] - 1, intersection[1]};
    if (isIntersection(down)) {
      adjacentIntersections.add(down);
    }
    return adjacentIntersections;
  }

  /**
   * Place the color on the given intersection (row, col) on the Board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @param color    color of placed move.
   *                 //@requires isIntersection(row, col)
   */
  protected void setStone(int[] intersection, Color color) {
    if (isIntersection(intersection)) {
      this.intersections[intersection[0]][intersection[1]] = color;
    }
  }

  /**
   * Returns the Color of the field (row, col).
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return Color of the field with row and col
   * // requires: isIntersection(row, col)
   */
  public Color getStone(int[] intersection) {
    if (isIntersection(intersection)) {
      return intersections[intersection[0]][intersection[1]];
    } else {
      return null;
    }
  }

  public List<int[]> getStonesWithThisColor(Color color) {
    List<int[]> stoneWithThisColor = new ArrayList<>();
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {

        if (getStone(new int[]{col, row}) == color) {
          stoneWithThisColor.add(new int[]{col, row});
        }
      }
    }
    return stoneWithThisColor;
  }

  /**
   * Get a List of intersections with liberties for the stone.
   *
   * @param stone to be checked.
   * @return a List of intersections with liberties for the group.
   */
  public List<int[]> getLibertiesOfStone(int[] stone) {
    List<int[]> stoneLiberties = new ArrayList<>();
    for (int[] adjacent : getAdjacentIntersections(stone)) {
      if (isEmptyIntersection(adjacent)) {
        stoneLiberties.add(adjacent);
      }
    }
    return stoneLiberties;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                      Groups                                                ///
  //////////////////////////////////////////////////////////////////////////////////////////////////

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
      if (getStone(intersection) != Color.EMPTY) {
        group.add(intersection);
      }
    } else {
      if (getStone(intersection) == Color.EMPTY) {
        group.add(intersection);
      }
    }

    for (int[] adjacent : getAdjacentIntersections(intersection)) {
      addStonesToGroup(group, adjacent, getStone(intersection));
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
  private void addStonesToGroup(List<int[]> group, int[] current, Color color) {
    // check if the intersection is not already on the list and is the same color
    if (!containsIntersection(group, current) && (getStone(current) == color)) {
      group.add(current);

      // recursive check for all adjacent intersections of new to the group added intersections
      for (int[] adjacent : getAdjacentIntersections(current)) {
        if (getStone(adjacent) == getStone(current)) {
          addStonesToGroup(group, adjacent, color);
        }
      }
    }
  }

  /**
   * Get a List of intersections with liberties for the group.
   *
   * @param group group to be checked.
   * @return a List of intersections with liberties for the group.
   */
  public List<int[]> getLibertiesOfGroup(List<int[]> group) {
    Set<List<Integer>> groupLibertiesSet = new HashSet<>();

    for (int[] stone : group) {
      List<int[]> libertiesOfStone = getLibertiesOfStone(stone);
      for (int[] liberty : libertiesOfStone) {
        groupLibertiesSet.add(Arrays.asList(liberty[0], liberty[1]));
      }
    }

    List<int[]> groupLibertiesList = new ArrayList<>();
    for (List<Integer> liberty : groupLibertiesSet) {
      groupLibertiesList.add(new int[]{liberty.get(0), liberty.get(1)});
    }

    return groupLibertiesList;
  }

  /**
   * Returns if coordinate is already in the group.
   *
   * @param group          list to check.
   * @param intersection  intersection saved as int[] {col, row}
   * @return true if coordinate is already in the list.
   */
  static boolean containsIntersection(List<int[]> group, int[] intersection) {
    for (int[] element : group) {
      if (Arrays.equals(element, intersection)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the assigned color of the territory.
   *
   * @param group list with intersection coordinates
   * @return the assigned color of the territory
   */
  protected Color getTerritoryColor(List<int[]> group) {
    // for all stones in group get adjacent stone color
    List<Color> colors = new ArrayList<>();
    for (int[] stone : group) {
      for (int[] adjacentStone : getAdjacentIntersections(stone)) {
        if (getStone(adjacentStone) != Color.EMPTY) {
          colors.add(getStone(adjacentStone));
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

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                      Board Rules                                           ///
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns if it is a valid move to make on the board.
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return true if it is a valid move to make on the board.
   */
  public boolean isValid(int[] intersection) {
    return isIntersection(intersection) && isEmptyIntersection(intersection);
  }


  /**
   * Returns if the stone has a liberty (empty adjacent stone).
   *
   * @param intersection intersection saved as int[] {col, row}
   * @return if the stone has a liberty (empty adjacent stone).
   */
  public boolean isSuicide(int[] intersection) {
    return getLibertiesOfGroup(getGroup(intersection, true)).isEmpty();
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

      if (getLibertiesOfGroup(group).isEmpty()) {
        captured.addAll(group);
      }
    }
    return captured;
  }

  /**
   * Set intersections of capturedGroup stones to empty.
   *
   * @param capturedGroup list of coordinates of capturedGroup stones.
   */
  public void removeCaptured(List<int[]> capturedGroup) {
    for (int[] capture : capturedGroup) {
      setStone(capture, Color.EMPTY);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                         Scoring                                            ///
  //////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * Determining the final territories and assign a Color to all intersections on the board.
   */
  public void getFilledBoard() {
    for (int row = 0; row < DIM; row++) {
      for (int col = 0; col < DIM; col++) {

        // find next empty intersection on the board and create new group
        if (getStone(new int[]{col, row}) == Color.EMPTY) {
          List<int[]> group = new ArrayList<>();

          // find all adjacent empty intersections and add to group
          addStonesToGroup(group, new int[]{col, row}, Color.EMPTY);

          // get and set territory color for group
          if (!group.isEmpty()) {
            Color territoryColor = getTerritoryColor(group);
            for (int[] emptySpot : group) {
              setStone(emptySpot, territoryColor);
            }
          }
        }
      }
    }
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

}