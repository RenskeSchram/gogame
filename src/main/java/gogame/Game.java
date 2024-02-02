package gogame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Game {

  private final List<Player> players = new ArrayList<>();
  private Player turn;
  public Board board;
  private boolean active = false;
  private boolean passed = false;
  private int gameCode;
  private HashMap<Integer, Color[][]> koRuleBoards;
  int counter = 0;
  /**
   * Constructor for new Game object with players and a new Board.
   */
  public Game(Player firstPlayer, Player secondPlayer, int DIM) {
    board = new Board(DIM);
    koRuleBoards = new HashMap<>();
    players.add(firstPlayer);
    players.add(secondPlayer);

    start();
  }

  /**
   * Set number of game to identify game.
   * @param gameCode number of game
   */
  public void setGameCode(int gameCode) {
    this.gameCode = gameCode;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
  public boolean getActive() {
    return active;
  }

  @Override
  public String toString() {
    return "00" + gameCode;
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
   * Returns if the placed color is of the correct ServerPlayer at turn.
   *
   * @param color color of placed stone.
   * @return true if the placed color is of the correct ServerPlayer at turn.
   */
  protected boolean isValidTurn(Color color) {
    return turn != null && color == turn.getColor();
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
   * Initialize start of game by assigning colors and first turn to the players.
   */
  private void start() {
    active = true;

    players.get(0).setColor(Color.BLACK);
    players.get(0).game = this;
    players.get(1).setColor(Color.WHITE);
    players.get(0).game = this;

    turn = players.get(0);

    passGameUpdateToAll(Protocol.GAMESTARTED + Protocol.SEPARATOR + players.get(0)
        .getUsername() + "," + players.get(1)
        .getUsername() + Protocol.SEPARATOR + board.getDIM());
    turn.passGameUpdate(Protocol.MAKEMOVE);
  }

  /**
   * End game. Stop possibility to make a move, calculate scores and call out winner.
   */
  public void end(Color color) {
    active = false;
    if (color == Color.WHITE || color == Color.BLACK) {
      for (Player player : players) {
        player.quitGame();
        player.passGameUpdate( Protocol.GAMEOVER + Protocol.SEPARATOR + "WINNER" + Protocol.SEPARATOR + color);
      }
    } else {
      for (Player player : players) {
        player.quitGame();
        player.passGameUpdate(Protocol.GAMEOVER + Protocol.SEPARATOR + "DRAW");
      }
    }
    board.clear();
  }


  protected void passGameUpdateToAll(String update) {
    for (Player player : players) {
      player.passGameUpdate(update);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                         GamePlay/Moves                                     ///
  //////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * Do the move if this is valid.
   *
   * @param location index of intersection of move
   * @param color    color of move
   */
  public void doMove(int[] location, Color color) {

    if (isValidTurn(color) && active) {

      if (isValidMove(location, color)) {
        passGameUpdateToAll(Protocol.MOVE + Protocol.SEPARATOR + intersectionArrayToSingleInt(location)
            + Protocol.SEPARATOR + getTurn().getColor());

        passed = false;

        handleValidMove(board, location, color);

        turn = otherTurn();
        getTurn().passGameUpdate(Protocol.MAKEMOVE);
        koRuleBoards.put(counter, board.deepCopy().getIntersections());
        counter++;
        //System.out.println(board.toString());

      } else {
        getTurn().passGameUpdate(Protocol.ERROR + Protocol.SEPARATOR + "invalid move, try again");
        getTurn().passGameUpdate(Protocol.MAKEMOVE);
      }

    } else {
      getPlayer(color).passGameUpdate( Protocol.ERROR + Protocol.SEPARATOR + "wait for your turn to make a move");
    }

  }

  private int intersectionArrayToSingleInt(int[] intersectionArray) {
    return intersectionArray[1]*board.getDIM() + intersectionArray[0];
  }

  public void handleValidMove(Board board, int[] location, Color color) {
    // put the stone on the field
    board.setStone(location, color);
    //check for and remove captured stones
    board.removeCaptured(board.getCaptured(location));
    //single suicide (nog verplaatsen in code)
    if (board.isSuicide(location)) {
      board.setStone(location, Color.EMPTY);
    }
  }

  /**
   * Do a passing move.
   *
   * @param color color of ServerPlayer who is passing
   */
  public void doPass(Color color) {
    if (isValidTurn(color) && active) {
      if (passed) {
        end(board.getWinner());
      } else {
        passGameUpdateToAll(Protocol.PASS + Protocol.SEPARATOR + getTurn().getColor());
        turn = otherTurn();
        passed = true;
        getTurn().passGameUpdate(Protocol.MAKEMOVE);
        koRuleBoards.put(counter, board.deepCopy().getIntersections());
        counter++;
      }

    }
  }

  public void doResign(Color color) {
    end(color.other());
  }

  /**
   * Check if the move is valid: valid coordinate on board and ko-rule.
   *
   * @return true if the move is valid.
   */
  protected boolean isValidMove(int[] location, Color color) {
    return isValidTurn(color) && active && board.isValid(location) && !isKoFight(location, color);
  }

  /**
   * Returns list of possible valid moves.
   *
   * @return list of possible valid moves
   */
  public List<int[]> getValidMoves() {
    List<int []> validMoves = new ArrayList<>();
    for (int col = 0; col < board.getDIM(); col++) {
      for (int row = 0; row < board.getDIM(); row++) {
        if (isValidMove(new int[]{col, row}, getTurn().getColor())) {
          validMoves.add(new int[]{col, row});
        }
      }
    }
    return validMoves;
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
    handleValidMove(copyBoard, location, color);

    boolean isKoFight = false;

    for (Color[][] oldBoardArray : koRuleBoards.values()) {
      if (Arrays.deepEquals(oldBoardArray, copyBoard.getIntersections())) {
        isKoFight = true;
        break;
      }
    }

    return isKoFight;
  }

}
