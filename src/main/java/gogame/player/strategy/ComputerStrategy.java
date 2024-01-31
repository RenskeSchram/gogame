package gogame.player.strategy;

import gogame.Board;
import gogame.Protocol;
import gogame.player.OnlinePlayer;
import gogame.player.StrategyGame;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ComputerStrategy implements Strategy {

  StrategyGame strategyGame;
  OnlinePlayer player;

  public ComputerStrategy(OnlinePlayer player) {
    this.player = player;
  }

  @Override
  public void getUsername() {
    if (player.getUsername() == null) {
      player.setUsername("computer");
    } else {
      player.setUsername(player.getUsername() + "r");
    }
    player.getConnection().sendOutput(Protocol.LOGIN + Protocol.SEPARATOR + player.getUsername());
  }

  public void sendQueue() {
    player.getConnection().sendOutput(Protocol.QUEUE);
  }

  @Override
  public void setGame(StrategyGame game) {
    this.strategyGame = game;
  }

  @Override
  public void determineMove() {
    int[] move = getImprovingTerritoryMove();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    Board testBoard = strategyGame.board.deepCopy();
    if (move == null) {
      if (!getValidMoves().isEmpty() || getTerritoryDifference(testBoard) < 0) {
        int[] randomMove = getRandomMove(getValidMoves());
        player.doMove(randomMove);
        System.out.println(Arrays.toString(randomMove) + player.getColor());

      } else {
        player.doPass();
      }
    } else {
      player.doMove(move);
      System.out.println(Arrays.toString(move) + player.getColor());
    }
  }


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

  protected int getTerritoryDifference(Board board) {
    board.getFilledBoard();
    return board.getStonesWithThisColor(player.getColor()).size() - board.getStonesWithThisColor(
        player.getColor().other()).size();
  }

  public int[] getRandomMove(List<int[]> possibleMoves) {
    return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
  }

  public List<int[]> getValidMoves() {
    return strategyGame.getValidMoves();
  }

}