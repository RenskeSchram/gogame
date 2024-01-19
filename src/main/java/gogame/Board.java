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
     * @param row index of row of location
     * @param col index of col of location
     * @return true if location is a valid field on the Board.
     */
    protected boolean isField(int row, int col) {
        return row >= 0 && row < DIM && col >= 0 && col < DIM;
    }

    /**
     * Check if location is a valid field on the Board.
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
     * @param row index of row of location
     * @param col index of col of location
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
     * @param row index of row of location
     * @param col index of col of location
     * @return Color of the field with row and col
     */
    protected Color getField(int row, int col) {
        if (isField(row, col)) {
            return fields[row][col];
        } else {
            return null;
        }
    }

    protected boolean isGameOver() {
        return false;
    }

    protected boolean isValid(int row, int col, Color color) {
        return isField(row, col) && isEmpty(row, col);
    }

    protected boolean hasLiberty(int row, int col) {
        boolean liberty = false;
        for (int[] adjacent : getAdjacentStones(row, col)) {
            if (isEmpty(adjacent[0], adjacent[1])) { liberty = true; }
        }
        return liberty;
    }

    protected List<int[]> getGroup(int row, int col) {
        List<int[]> group = new ArrayList<>();
        if (getField(row, col) != Color.EMPTY) {
            group.add(new int[]{row, col});
        }

        for (int[] adjacent : getAdjacentStones(row, col)) {
                exploreAdjacentStones(group, adjacent, getField(row, col));
        }

        return group;
    }

    private void exploreAdjacentStones(List<int[]> group, int[] current, Color color) {
        if (!group.isEmpty() && !containsRowCol(group, current) && (getField(current[0], current[1]) == color)) {
            group.add(current);

            for (int[] adjacent : getAdjacentStones(current[0], current[1])) {
                if (getField(adjacent[0], adjacent[1]) == getField(current[0], current[1])) {
                    exploreAdjacentStones(group, adjacent, color);
                }
            }
        }
    }

    private boolean containsRowCol(List<int[]> list, int[] rowCol) {
        for (int[] element : list) {
            if (Arrays.equals(element,rowCol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns adjacent field coordinates on the board.
     * @param row index of row of location
     * @param col index of col of location
     * @return adjacent field coordinates on the board.
     */
    protected List<int[]> getAdjacentStones(int row, int col) {
        List<int[]> adjacentStones = new ArrayList<>();
        int[] left = new int[]{row , col - 1};
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
     *  Return the captured stones. All adjacent stones of the placed stone are checked for being
     *  part of a group and checking if any of the stones in the group has liberties. If all stones
     *  in the group are out of liberties, the group is captured. All captured stones are returned.
     * @param row index of row of placed stone
     * @param col index of col of placed stone
     * @return list of int[]{row, col} of captured stones
     */
    protected List<int[]> getCaptured(int row, int col) {
        List<int[]> captured = new ArrayList<>();

        for (int[] adjacent : getAdjacentStones(row, col)) {
            List<int[]> group = getGroup(adjacent[0], adjacent[1]);
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
            for (int col = 0; col < DIM ; col++) {
            copy.setField(row, col, this.getField(row, col));
            }
        }
        return copy;
    }



    /**
     * Returns a String representation of the fields array.
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

}
