package gogame.client.strategy;

import gogame.Board;
import gogame.Protocol;
import gogame.client.ClientPlayer;
import gogame.client.StrategyGame;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ComputerStrategy implements Strategy {

  protected StrategyGame strategyGame;
  private final ClientPlayer player;

  public ComputerStrategy(ClientPlayer player) {
    this.player = player;
  }

  /**
   * Automatically set username and send to Server.
   */
  @Override
  public void getUsername() {
    if (player.getUsername() == null) {
      player.setUsername("computer");
    } else {
      player.setUsername(player.getUsername() + "r");
    }
    player.getConnection().sendOutput(Protocol.LOGIN + Protocol.SEPARATOR + player.getUsername());
  }

  /**
   * Automatically respond that client can get queued.
   */
  public void sendQueue() {
    player.getConnection().sendOutput(Protocol.QUEUE);
  }

  /**
   * Set game to show current status for the client.
   *
   * @param game go game object.
   */
  @Override
  public void setGame(StrategyGame game) {
    this.strategyGame = game;
  }

  /**
   * Automatically determine the move. Check pos possible move to improve territory, otherwise do a rondom move (if losing) or pass (if winning).
   */
  @Override
  public void determineMove() {
    int[] move = getImprovingTerritoryMove();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    Board testBoard = strategyGame.board.deepCopy();

    if (move == null || getValidMoves().size() < 3) {
      if (getValidMoves().size() < 3 || getTerritoryDifference(testBoard) > 0) {
        player.doPass();
        System.out.println("PASS" + player.getColor());
      } else {
        int[] randomMove = getRandomMove(getValidMoves());
        player.doMove(randomMove);
        System.out.println(Arrays.toString(randomMove) + player.getColor());
      }
    } else {
      player.doMove(move);
      System.out.println(Arrays.toString(move) + player.getColor());
    }
    System.out.println(testBoard.toString());
  }

  /**
   * For all valid intersections on the baord, check for the move with the highest possible improvement of territory.
   * @return
   */
  public int[] getImprovingTerritoryMove() {
    int[] bestMove = null;
    int bestTerritoryDifference = 0;

    List<int[]> shuffledValidMoves = getValidMoves();
    Collections.shuffle(shuffledValidMoves);

    for (int[] move : shuffledValidMoves) {
      Board testBoard = strategyGame.board.deepCopy();
      strategyGame.handleValidMove(move, player.getColor());
      if (getTerritoryDifference(strategyGame.board) > bestTerritoryDifference) {
        bestMove = move;
        bestTerritoryDifference = getTerritoryDifference(strategyGame.board);
      }
      strategyGame.board = testBoard;
    }
    return bestMove;
  }

  /**
   * Calculate current territory difference.
   *
   * @param board current board status.
   * @return int of territory point difference.
   */
  protected int getTerritoryDifference(Board board) {
    board.getFilledBoard();
    return board.getStonesWithThisColor(player.getColor()).size() - board.getStonesWithThisColor(
        player.getColor().other()).size();
  }

  /**
   * Get random move from provided possible moves.
   *
   * @param possibleMoves provided list of moves
   * @return a random move.
   */
  public int[] getRandomMove(List<int[]> possibleMoves) {
    return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
  }

  /**
   * Get valid moves.
   * @return
   */
  public List<int[]> getValidMoves() {
    return strategyGame.getValidMoves();
  }

}