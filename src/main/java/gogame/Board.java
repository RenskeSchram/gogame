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
    public static final int DIM = 9;
    protected final Color[][] fields;

    /**
     * Constructor for an empty Board.
     * For all fields, set field to Color.EMPTY
     */
    public Board() {
        fields = new Color[DIM][DIM];
        for (int row = 0; (row >= 0) && (row < DIM); row++) {
            for (int col = 0; (col >= 0) && (col < DIM); col++) {
                setField(row, col, Color.EMPTY);
            }
        }
    }

    /**
     * Check if location is a valid field on the Board.
     *
     * @param row index of row of location
     * @param col index of col of location
     * @return true if location is a valid field on the Board.
     */
    protected boolean isField(int row, int col) {
        return row >= 0 && row < DIM && col >= 0 && col < DIM;
    }

    /**
     * Check if location is a valid field on the Board.
     *
     * @param row index of row of location
     * @param col index of col of location
     * @return true if location is a valid field on the Board.
     */
    protected boolean isEmpty(int row, int col) {
        if (isField(row, col)) {
            return getField(row, col) == Color.EMPTY;
        } else {
            return false;
        }
    }

    /**
     * Place the color on the given location (row, col) on the Board.
     *
     * @param row   index of row of location
     * @param col   index of col of location
     * @param color color of placed move.
     * //@requires isField(row, col)
     */
    protected void setField(int row, int col, Color color) {
        if (isField(row, col)) {
            this.fields[row][col] = color;
        }
    }

    /**
     * Returns the Color of the field (row, col).
     *
     * @param row index of row of location
     * @param col index of col of location
     * @return Color of the field with row and col
     * // requires: isField(row, col)
     */
    protected Color getField(int row, int col) {
        if (isField(row, col)) {
            return fields[row][col];
        } else {
            return null;
        }
    }

    /**
     * Returns if it is a valid move to make on the board.
     * // requires: isField(row, col) && isEmpty(row, col)
     *
     * @param row index of row of location
     * @param col index of col of location
     * @return
     */
    protected boolean isValid(int row, int col) {
        return isField(row, col) && isEmpty(row, col);
    }

    /**
     * Returns if the stone has a liberty (empty adjacent stone).
     *
     * @param row
     * @param col
     * @return if the stone has a liberty (empty adjacent stone).
     */
    protected boolean hasLiberty(int row, int col) {
        boolean liberty = false;
        // get the color for all adjacent intersections
        for (int[] adjacent : getAdjacentStones(row, col)) {
            // check if color is EMPTY, meaning there is a liberty
            if (isEmpty(adjacent[0], adjacent[1])) { liberty = true; }
        }
        return liberty;
    }

    /**
     * Get the group of stones (or empty intersections). If the game is active, groups are formed by
     * colored stones (for captures), if the game is ended, (territory) groups are formed by the
     * empty stones.
     *
     * @param row
     * @param col
     * @param active activity status of game
     *
     * @return List of coordinates of intersections in group
     */
    protected List<int[]> getGroup(int row, int col, boolean active) {
        List<int[]> group = new ArrayList<>();
        if (active) {
            if (getField(row, col) != Color.EMPTY) {
                group.add(new int[]{row, col});
            }
        } else {
            if (getField(row, col) == Color.EMPTY) {
                group.add(new int[]{row, col});
            }
        }

        for (int[] adjacent : getAdjacentStones(row, col)) {
            exploreAdjacentStones(group, adjacent, getField(row, col));
        }

        return group;
    }

    /**
     * Explore adjacent intersections of current and add to list if these belong to the same group.
     *
     * @param group group of intersections
     * @param current current intersection to be checked
     * @param color color of the group
     */
    private void exploreAdjacentStones(List<int[]> group, int[] current, Color color) {
        // check if the intersection is not already on the list and is the same color
        if (!containsCoordinate(group, current) && (getField(current[0], current[1]) == color)) {
            group.add(current);

            // recursive check for all adjacent intersections of new to the group added intersections
            for (int[] adjacent : getAdjacentStones(current[0], current[1])) {
                if (getField(adjacent[0], adjacent[1]) == getField(current[0], current[1])) {
                    exploreAdjacentStones(group, adjacent, color);
                }
            }
        }
    }

    /**
     * Returns if coordinate is already in the list.
     *
     * @param list list to check.
     * @param rowCol coordinate to check.
     * @return if coordinate is already in the list.
     */
    private boolean containsCoordinate(List<int[]> list, int[] rowCol) {
        for (int[] element : list) {
            if (Arrays.equals(element, rowCol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns adjacent field coordinates on the board.
     *
     * @param row index of row of location
     * @param col index of col of location
     * @return adjacent field coordinates on the board.
     */
    protected List<int[]> getAdjacentStones(int row, int col) {
        List<int[]> adjacentStones = new ArrayList<>();
        int[] left = new int[]{row, col - 1};
        if (isField(left[0], left[1])) {
            adjacentStones.add(left);
        }
        int[] right = new int[]{row, col + 1};
        if (isField(right[0], right[1])) {
            adjacentStones.add(right);
        }
        int[] up = new int[]{row + 1, col};
        if (isField(up[0], up[1])) {
            adjacentStones.add(up);
        }
        int[] down = new int[]{row - 1, col};
        if (isField(down[0], down[1])) {
            adjacentStones.add(down);
        }
        return adjacentStones;
    }


    /**
     * Return the captured stones. All adjacent stones of the placed stone are checked for being
     * part of a group and checking if any of the stones in the group has liberties. If all stones
     * in the group are out of liberties, the group is captured. All captured stones are returned.
     *
     * @param row index of row of placed stone
     * @param col index of col of placed stone
     * @return list of int[]{row, col} of captured stones
     */
    protected List<int[]> getCaptured(int row, int col) {
        List<int[]> captured = new ArrayList<>();

        for (int[] adjacent : getAdjacentStones(row, col)) {
            List<int[]> group = getGroup(adjacent[0], adjacent[1], true);
            boolean noliberty = true;

            for (int[] stone : group) {
                if (hasLiberty(stone[0], stone[1])) {
                    noliberty = false;
                    break;
                }
            }

            if (noliberty) {
                captured.addAll(group);
            }
        }
        return captured;
    }

    /**
     * Set fields of captured stones to empty.
     * @param captured list of coordinates of captured stones.
     */
    protected void removeCaptured(List<int[]> captured) {
        for (int[] capture : captured) {
            setField(capture[0], capture[1], Color.EMPTY);
        }
    }


    /**
     * Creates a deep copy of this field.
     */
    /*@ ensures \result != this;
     ensures (\forall int i; (i >= 0 && i < DIM*DIM); \result.fields[i] == this.fields[i]);
     @*/
    public Board deepCopy() {
        Board copy = new Board();
        for (int row = 0; row < DIM; row++) {
            for (int col = 0; col < DIM; col++) {
                copy.setField(row, col, this.getField(row, col));
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
                stringBoard.append(fields[row][col]).append("  ");
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

        // count score
        int whiteCount = 0;
        int blackCount = 0;
        for (int row = 0; row < DIM; row++) {
            for (int col = 0; col < DIM; col++) {
                if (getField(row, col) == Color.WHITE) { whiteCount++; }
                if (getField(row, col) == Color.BLACK) { blackCount++; }
            }
        }

        // return winner
        if (whiteCount > blackCount) {
            return Color.WHITE;
        } else if (whiteCount < blackCount) {
            return Color.BLACK;
        } else {
            return Color.NEUTRAL;
        }
    }

    /**
     * Determining the final territories and assign a Color to all intersections on the board.
     */
    public void getFilledBoard() {
        // loop over the board
        for (int row = 0; row < DIM; row++) {
            for (int col = 0; col < DIM; col++) {

                // find next empty intersection on the board and create new group
                if (getField(row, col)== Color.EMPTY) {
                    List<int[]> group = new ArrayList<>();

                    // find all adjacent empty intersections and add to group
                    exploreAdjacentStones(group, new int[]{row, col}, Color.EMPTY);

                    // get and set territory color for group
                    if (!group.isEmpty()) {
                        Color territoryColor = getTerritoryColor(group);
                        for (int[] emptySpot : group) {
                            setField(emptySpot[0], emptySpot[1], territoryColor);
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
            for (int [] adjacentStone : getAdjacentStones(stone[0], stone[1])) {
                if (getField(adjacentStone[0],adjacentStone[1]) != Color.EMPTY) {
                    colors.add(getField(adjacentStone[0],adjacentStone[1]));
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
        if (correctTerritory) {
            return colors.get(0);
        } else {
            return Color.NEUTRAL;
        }
}

}