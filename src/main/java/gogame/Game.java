package gogame;

import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    public Board board;
    public int gameCode;
    private Board previousBoard;
    private Player turn;
    protected boolean active = false;
    private boolean passed = false;
    Timer timer;
    TimerTask timeOutPass;
    private TimerTask timeOutReminder;



    /**
     * Constructor for new Game object with players and a new Board.
     */
    public Game(Player firstPlayer, Player secondPlayer, int DIM) {
        board = new Board(DIM);
        previousBoard = new Board(DIM);
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
        players.get(0).game = this;
        players.get(1).setColor(Color.WHITE);
        players.get(0).game = this;
        turn = players.get(0);

        passGameUpdateToAll(Protocol.GAMESTARTED + Protocol.SEPARATOR + players.get(0)
                .getUsername() + "," + players.get(1)
                .getUsername() + Protocol.SEPARATOR + board.DIM);
        turn.passGameUpdate(Protocol.MAKEMOVE);

    }

    /**
     * Check if the move is valid: valid coordinate on board and ko-rule.
     *
     * @return true if the move is valid.
     */
    protected boolean isValidMove(int[] location, Color color) {
        return isValidTurn(color) && active && board.isValid(location) && !isKoFight(location,
                                                                                     color);
    }

    /**
     * Returns if the placed color is of the correct ServerPlayer at turn.
     *
     * @param color color of placed stone.
     * @return true if the placed color is of the correct ServerPlayer at turn.
     */
    private boolean isValidTurn(Color color) {
        return turn != null && color == turn.getColor();
    }


    /**
     * Check and return true if the move results in a Ko-fight.
     *
     * @param location index of intersection of move
     * @param color    color of move
     * @return true if the move results in a Ko-fight
     */
    protected boolean isKoFight(int[] location, Color color) {
        Board copyBoard = board.deepCopy();

        // do to-be-tested move
        copyBoard.setField(location, color);

        // check if this moves results in a similar board as one move ago, if so: Ko-fight
        copyBoard.removeCaptured(copyBoard.getCaptured(location));
        return Arrays.deepEquals(copyBoard.fields, previousBoard.fields);
    }

    /**
     * Do the move if this is valid.
     *
     * @param location index of intersection of move
     * @param color    color of move
     */
    public void doMove(int[] location, Color color) {
        if (isValidTurn(color)) {
            resetTimer();

            if (isValidMove(location, color)) {
                // send move over network
                passGameUpdateToAll(
                        Protocol.MOVE + Protocol.SEPARATOR + location[0] + "," + location[1] + Protocol.SEPARATOR + getTurn().getColor());

                // set new previous board for ko-fight check
                previousBoard = board.deepCopy();
                // put the stone on the field
                board.setField(location, color);
                //check for and remove captured stones
                board.removeCaptured(board.getCaptured(location));
                //single suicide (nog verplaatsen in code)
                if (board.isSingleSuicide(location)) {
                    board.setField(location, Color.EMPTY);
                }

                // ask for new move to correct player
                startTimer();
                turn = otherTurn();
                getTurn().passGameUpdate(Protocol.MAKEMOVE);

            } else {
                getTurn().passGameUpdate(
                        Protocol.ERROR + Protocol.SEPARATOR + "invalid move, try again");
                getTurn().passGameUpdate(Protocol.MAKEMOVE);
            }
        } else {
            getPlayer(color).passGameUpdate(
                    Protocol.ERROR + Protocol.SEPARATOR + "wait for your turn to make a move");
        }
    }

    /**
     * Do a passing move.
     *
     * @param color color of ServerPlayer who is passing
     */
    public void doPass(Color color) {
        if (isValidTurn(color) && active) {
            resetTimer();
            if (passed) {
                end();
            } else {
                // send move to players
                passGameUpdateToAll(Protocol.PASS + Protocol.SEPARATOR + getTurn().getColor());
                // switch turn and set passed to true.
                turn = otherTurn();
                passed = true;
                startTimer();
            }
        }

        // ask for new move to correct player
        getTurn().passGameUpdate(Protocol.MAKEMOVE);
    }

    public void doResign(Color color) {
        board.doResign(color);
        end();
    }

    /**
     * Return ServerPlayer who is not at turn.
     *
     * @return ServerPlayer who is not at turn
     */
    private Player otherTurn() {
        if (turn == players.get(0)) {
            return players.get(1);
        } else {
            return players.get(0);
        }
    }


    /**
     * Returns the players whose turn it is.
     *
     * @return the players whose turn it is.
     */
    protected Player getTurn() {
        return turn;
    }

    /**
     * Returns the color of the player.
     *
     * @return the color of the player.
     */
    protected Player getPlayer(Color color) {
        if (color.equals(Color.BLACK)) {
            return players.get(0);
        } else if (color.equals(Color.WHITE)) {
            return players.get(0);
        } else {
            System.out.println(
                    Protocol.ERROR + Protocol.SEPARATOR + "color does not belong to a player");
            return null;
        }
    }

    /**
     * End game. Stop possibility to make a move, calculate scores and call out winner.
     */
    private void end() {
        // Inactivate the game
        active = false;
        for (Player player : players) {
            player.quitGame();
            player.passGameUpdate(Protocol.GAMEOVER + Protocol.SEPARATOR + board.getWinner());
            player.passGameUpdate(Protocol.PRINT);
        }
    }

    public void passGameUpdateToAll(String update) {
        for (Player player : players) {
            player.passGameUpdate(update);
        }
    }

    public List<int[]> getValidMoves() {
        List<int []> validMoves = new ArrayList<>();
        for (int col = 0; col < board.DIM; col++) {
            for (int row = 0; row < board.DIM; row++) {
                if (isValidMove(new int[]{col, row}, getTurn().getColor())) {
                    validMoves.add(new int[]{col, row});
                }
            }
        }
        return validMoves;
    }



    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timeOutPass = new TimerTask() {
            @Override
            public void run() {
                doPass(getTurn().getColor());
                getTurn().passGameUpdate(Protocol.ERROR + Protocol.SEPARATOR + "you automatically passed as time run out");
            }
        };

        timeOutReminder = new TimerTask() {
            @Override
            public void run() {
                getTurn().passGameUpdate(Protocol.ERROR + Protocol.SEPARATOR + "time is running out, you have 20 seconds left to make a move");
            }
        };

        timer.schedule(timeOutReminder, 40000);
        timer.schedule(timeOutPass, 10000);
    }

    void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timeOutPass != null) {
            timeOutPass.cancel();
            timeOutPass = null;
        }
    }

    private void resetTimer() {
        stopTimer();
        startTimer();
    }


    @Override
    public String toString() {
        return "00" + gameCode;
    }
}
