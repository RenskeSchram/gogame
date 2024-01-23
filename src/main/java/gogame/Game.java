package gogame;

import java.util.*;
import server.SocketConnection;

public class Game {
    private final List<Player> players = new ArrayList<>();
    public Board board;
    private Board previousBoard;
    private Player turn;
    protected boolean active = false;
    private boolean passed = false;

    /**
     * Constructor for new Game object with players and a new Board.
     */
    public Game(Player firstPlayer, Player secondPlayer) {
        board = new Board();
        previousBoard = new Board();
        players.add(firstPlayer);
        players.add(secondPlayer);

        start();
    }

    /**
     * Initialize start of game by assigning colors and first turn to the players.
     */
    private void start() {
        // activate game
        active = true;
        players.get(0).setColor(Color.BLACK);
        players.get(1).setColor(Color.WHITE);
        turn = players.get(0);

        //sendStartedGame() over network
        for (Player player : players) {
            // TODO: player.getConnection().sendGameStarted(players.get(0).userName, players.get(1).userName);
        }
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
     *
     * @return true if the move is valid.
     */
    protected boolean isValid(int location, Color color) {
        return isCorrectTurn(color) &&
                active &&
                turn != null &&
                board.isValid(getCoordinate(location)[0], getCoordinate(location)[1]) &&
                !isKoFight(location, color);
    }

    /**
     * Check and return true if the move results in a Ko-fight.
     *
     * @param location index of intersection of move
     * @param color color of move
     *
     * @return true if the move results in a Ko-fight
     */
    protected boolean isKoFight(int location, Color color) {
        Board copyBoard = board.deepCopy();
        // make test move
        copyBoard.setField(getCoordinate(location)[0], getCoordinate(location)[1], color);
        // check if this moves results in a similar board as one move ago, if so: Ko-fight
        copyBoard.removeCaptured(copyBoard.getCaptured(getCoordinate(location)[0], getCoordinate(location)[1]));
        return Arrays.deepEquals(copyBoard.fields, previousBoard.fields);
    }

    /**
     * Do the move if this is valid.
     *
     * @param location index of intersection of move
     * @param color color of move
     */
    protected void doMove(int location, Color color) {
        if (isValid(location, color)) {
            // set new previous board for ko-fight check
            previousBoard = board.deepCopy();
            // put the stone on the field
            board.setField(getCoordinate(location)[0], getCoordinate(location)[1], color);
            //check for and remove captured stones
            //TODO: check for suicide
            board.removeCaptured(
                    board.getCaptured(getCoordinate(location)[0], getCoordinate(location)[1])
            );

            // sendMove() over network
            for (Player player : players) {
                //player.getConnection().sendOutput(location, color);
            }

        } else {
            // ERROR incorrect player making turn
        }

        // sendMakeMove() over network
        for (Player player : players) {
            //TODO: player.serverConnection.sendMakeMove(turn.userName);
        }

    }

    /**
     * Do a passing move.
     *
     * @param color color of ServerPlayer who is passing
     */
    protected void doPass(Color color) {
        if (isCorrectTurn(color) && active) {
            if (passed) {
                end();
            } else {
                // switch turn and set passed to true.
                turn = otherTurn();
                passed = true;

                // update over network
                for (Player player : players) {
                    //TODO: player.serverConnection.sendPass();
                    // player.serverConnection.sendMakeMove(turn.userName);
                }
            }
        }
    }

    /**
     * Return ServerPlayer who is not at turn.
     *
     * @return ServerPlayer who is not at turn
     */
    private Player otherTurn() {
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
    protected Player getTurn() {
        return turn;
    }

    /**
     * End game. Stop possibility to make a move, calculate scores and call out winner.
     */
    private void end() {
        // Inactivate the game
        active = false;
        //TODO: update players with winner
    }

    public void doResign(Color color) {

    }


}
