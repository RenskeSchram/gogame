package gogame;

import java.util.*;

public class Game {
    public Board board;
    private ServerPlayer turn;
    private final List<ServerPlayer> players = new ArrayList<>();
    private Board previousBoard;
    protected boolean active = false;
    private boolean passed = false;

    /**
     * Constructor for new Game object with players and a new Board.
     */
    public Game(ServerPlayer firstPlayer, ServerPlayer secondPlayer) {
        this.board = new Board();
        this.players.add(firstPlayer);
        this.players.add(secondPlayer);

        start();
    }

    /**
     * Initialize start of game by assigning colors and first turn to the players.
     */
    private void start() {
        //TODO: sendStartedGame() over network
        active = true;
        players.get(0).setColor(Color.BLACK);
        players.get(1).setColor(Color.WHITE);
        previousBoard = new Board();

        turn = players.get(0);
    }

    /**
     * Convert location index to row and column index.
     * @param location index of location
     * @return int[] variable with variable[1] = row and variable[2] = col
     */
    protected int[] getCoordinate(int location) {
        int row = location / Board.DIM;
        int col = location % Board.DIM;
        return new int[]{row, col};
    }

    /**
     * Check if the move is valid: correct player, valid coordinate on board and ko-rule.
     */
    protected boolean isValid(int location, Color color) {
        return isCorrectTurn(color) && active &&
                board.isValid(getCoordinate(location)[0], getCoordinate(location)[1], color) &&
                !isKoFight(location, color);
    }

    protected boolean isKoFight(int location, Color color) {
        Board copyBoard = board.deepCopy();
        copyBoard.setField(getCoordinate(location)[0], getCoordinate(location)[1], color);
        copyBoard.removeCaptured(copyBoard.getCaptured(getCoordinate(location)[0], getCoordinate(location)[1]));
        return Arrays.deepEquals(copyBoard.fields, previousBoard.fields);
    }

    /**
     *
     * @param location
     * @param color
     */
    protected void doMove(int location, Color color) {
        if (isValid(location, color)) {
            previousBoard = board.deepCopy();
            board.setField(getCoordinate(location)[0], getCoordinate(location)[1], color);
            //TODO: sendMove() over network
            //check for suicide
            board.removeCaptured(board.getCaptured(getCoordinate(location)[0], getCoordinate(location)[1]));
            turn = otherTurn();
            passed = false;
            //TODO: sendTurn() over network
        } else {
            // ERROR incorrect player making turn
        }
    }

    protected void doPass(Color color) {
        if (isCorrectTurn(color) && active) {
            if (passed) {
                //TODO: activate ending of game
                end();
            } else {
                turn = otherTurn();
                //TODO: sendMove() over network
                //TODO: sendTurn() over network
                passed = true;
            }
        }
    }

    private ServerPlayer otherTurn() {
        if (turn == players.get(0)) {
            return players.get(1);
        } else if (turn == players.get(1)) {
            return players.get(0);
        } else {
            // ERROR incorrect other turn
            System.out.println("ERROR: incorrect other turn");
            return null;
        }
    }

    /**
     * Returns if the placed color is of the correct ServerPlayer at turn.
     * @param color color of placed stone.
     * @return true if the placed color is of the correct ServerPlayer at turn.
     */
    private boolean isCorrectTurn(Color color) {
        return color == turn.getColor();
    }

    /**
     * Returns the players whose turn it is.
     * @return the players whose turn it is.
     */
    protected ServerPlayer getTurn() {
        return turn;
    }


    /**
     * End game. Stop possibility to make a move, calculate scores and call out winner.
     */
    private void end() {
        // Inactivate the game
        active = false;

        // Calculate winner
        board.getFilledBoard();
        switch(board.getWinningColor()) {
            case Color.WHITE -> System.out.println("WHITE is winner");
            case Color.BLACK -> System.out.print("BLACK is winner");
            case Color.NEUTRAL -> System.out.println("DRAW");
        }
    }

}
