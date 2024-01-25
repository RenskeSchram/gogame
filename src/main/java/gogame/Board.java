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
     * Check if location is a valid field on the Board.
     *
     * @param location location saved as int[] {col, row}
     * @return true if location is a valid field on the Board.
     */
//    protected boolean isField(int col, int row) {
//        return row >= 0 && row < DIM && col >= 0 && col < DIM;
//    }

    protected boolean isField(int[] location) {
        return location[0] >= 0 && location[0] < DIM && location[1] >= 0 && location[1] < DIM;
    }

    /**
     * Check if location is a valid field on the Board.
     *
     * @param location location saved as int[] {col, row}

     * @return true if location is a valid field on the Board.
     */
//    protected boolean isEmpty(int col, int row) {
//        if (isField(col, row)) {
//            return getField(col, row) == Color.EMPTY;
//        } else {
//            return false;
//        }
//    }

    protected boolean isEmpty(int[] location) {
        if (isField(location)) {
            return getField(location) == Color.EMPTY;
        } else {
            return false;
        }
    }

    /**
     * Place the color on the given location (row, col) on the Board.
     *
     * @param location location saved as int[] {col, row}
     * @param color color of placed move.
     * //@requires isField(row, col)
     */
//    public void setField(int col, int row, Color color) {
//        if (isField(col, row)) {
//            this.fields[col][row] = color;
//        }
//    }

    public void setField(int[] location, Color color) {
        if (isField(location)) {
            this.fields[location[0]][location[1]] = color;
        }
    }

    /**
     * Returns the Color of the field (row, col).
     *
     * @param location location saved as int[] {col, row}
     * @return Color of the field with row and col
     * // requires: isField(row, col)
     */
//    protected Color getField(int col, int row) {
//        if (isField(col, row)) {
//            return fields[col][row];
//        } else {
//            return null;
//        }
//    }

    protected Color getField(int[] location) {
        if (isField(location)) {
            return fields[location[0]][location[1]];
        } else {
            return null;
        }
    }

    /**
     * Returns if it is a valid move to make on the board.
     * // requires: isField(row, col) && isEmpty(row, col)
     *
     * @param location location saved as int[] {col, row}
     * @return
     */
//    public boolean isValid(int col, int row) {
//        return isField(col, row) && isEmpty(col, row);
//    }

    public boolean isValid(int[] location) {
        return isField(location) && isEmpty(location);
    }


    /**
     * Returns if the stone has a liberty (empty adjacent stone).
     *
     * @param location location saved as int[] {col, row}
     * @return if the stone has a liberty (empty adjacent stone).
     */
//    protected boolean hasLiberty(int col, int row) {
//        boolean liberty = false;
//        // get the color for all adjacent intersections
//        for (int[] adjacent : getAdjacentStones(col, row)) {
//            // check if color is EMPTY, meaning there is a liberty
//            if (isEmpty(adjacent)) { liberty = true; }
//        }
//        return liberty;
//    }

    protected boolean hasLiberty(int[] location) {
        boolean liberty = false;
        // get the color for all adjacent intersections
        for (int[] adjacent : getAdjacentStones(location)) {
            // check if color is EMPTY, meaning there is a liberty
            if (isEmpty(adjacent)) { liberty = true; }
        }
        return liberty;
    }

//
//    protected boolean isSingleSuicide(int[] location){
//        return !hasLiberty(location);
//    }

    protected boolean isSingleSuicide(int[] location){
        return !hasLiberty(location);
    }

    /**
     * Get the group of stones (or empty intersections). If the game is active, groups are formed by
     * colored stones (for captures), if the game is ended, (territory) groups are formed by the
     * empty stones.
     *
     * @param location location saved as int[] {col, row}
     * @param active activity status of game
     *
     * @return List of coordinates of intersections in group
     */
//    protected List<int[]> getGroup(int col, int row, boolean active) {
//        List<int[]> group = new ArrayList<>();
//
//        if (active) {
//            if (getField(col, row) != Color.EMPTY) {
//                group.add(new int[]{col, row});
//            }
//        } else {
//            if (getField(col, row) == Color.EMPTY) {
//                group.add(new int[]{col, row});
//            }
//        }
//
//        for (int[] adjacent : getAdjacentStones(col, row)) {
//            exploreAdjacentStones(group, adjacent, getField(col, row));
//        }
//
//        return group;
//    }

    protected List<int[]> getGroup(int[] location, boolean active) {
        List<int[]> group = new ArrayList<>();

        if (active) {
            if (getField(location) != Color.EMPTY) {
                group.add(location);
            }
        } else {
            if (getField(location) == Color.EMPTY) {
                group.add(location);
            }
        }

        for (int[] adjacent : getAdjacentStones(location)) {
            exploreAdjacentStones(group, adjacent, getField(location));
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
        if (!containsLocation(group, current) && (getField(current) == color)) {
            group.add(current);

            // recursive check for all adjacent intersections of new to the group added intersections
            for (int[] adjacent : getAdjacentStones(current)) {
                if (getField(adjacent) == getField(current)) {
                    exploreAdjacentStones(group, adjacent, color);
                }
            }
        }
    }


    /**
     * Returns if coordinate is already in the list.
     *
     * @param list list to check.
     * @param location location saved as int[] {col, row}
     * @return if coordinate is already in the list.
     */
    private boolean containsLocation(List<int[]> list, int[] location) {
        for (int[] element : list) {
            if (Arrays.equals(element, location)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns adjacent field coordinates on the board.
     *
     * @param location location saved as int[] {col, row}
     * @return adjacent field coordinates on the board.
     */
//    protected List<int[]> getAdjacentStones(int col, int row) {
//        List<int[]> adjacentStones = new ArrayList<>();
//        int[] left = new int[]{col, row - 1};
//        if (isField(left[0], left[1])) {
//            adjacentStones.add(left);
//        }
//        int[] right = new int[]{col, row + 1};
//        if (isField(right[0], right[1])) {
//            adjacentStones.add(right);
//        }
//        int[] up = new int[]{col + 1, row};
//        if (isField(up[0], up[1])) {
//            adjacentStones.add(up);
//        }
//        int[] down = new int[]{col - 1, row};
//        if (isField(down[0], down[1])) {
//            adjacentStones.add(down);
//        }
//        return adjacentStones;
//    }

    protected List<int[]> getAdjacentStones(int[] location) {
        List<int[]> adjacentStones = new ArrayList<>();
        int[] left = new int[]{location[0], location[1] - 1};
        if (isField(left)) {
            adjacentStones.add(left);
        }
        int[] right = new int[]{location[0], location[1] + 1};
        if (isField(right)) {
            adjacentStones.add(right);
        }
        int[] up = new int[]{location[0] + 1, location[1]};
        if (isField(up)) {
            adjacentStones.add(up);
        }
        int[] down = new int[]{location[0] - 1, location[1]};
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
     * @param location location saved as int[] {col, row}
     * @return list of int[]{row, col} of captured stones
     */
//    public List<int[]> getCaptured(int col, int row) {
//        List<int[]> captured = new ArrayList<>();
//
//        for (int[] adjacent : getAdjacentStones(col, row)) {
//            List<int[]> group = getGroup(adjacent[0], adjacent[1], true);
//            boolean noliberty = true;
//
//            for (int[] stone : group) {
//                if (hasLiberty(stone[0], stone[1])) {
//                    noliberty = false;
//                    break;
//                }
//            }
//
//            if (noliberty) {
//                captured.addAll(group);
//            }
//        }
//        return captured;
//    }

    public List<int[]> getCaptured(int[] location) {
        List<int[]> captured = new ArrayList<>();

        for (int[] adjacent : getAdjacentStones(location)) {
            List<int[]> group = getGroup(adjacent, true);
            boolean noliberty = true;

            for (int[] stone : group) {
                if (hasLiberty(stone)) {
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

        // count score
        int whiteCount = 0;
        int blackCount = 0;
        for (int row = 0; row < DIM; row++) {
            for (int col = 0; col < DIM; col++) {
                if (getField(new int[]{col, row}) == Color.WHITE) { whiteCount++; }
                if (getField(new int[]{col, row}) == Color.BLACK) { blackCount++; }
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
                if (getField(new int[] {col, row})== Color.EMPTY) {
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
            for (int [] adjacentStone : getAdjacentStones(stone)) {
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