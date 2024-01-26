package gogame.player.strategy;

import gogame.Board;
import gogame.Protocol;
import gogame.player.OnlinePlayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ComputerStrategy implements Strategy {

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
  public void determineMove() {
    int[] move = getImprovingTerritoryMove();
    System.out.println(Arrays.toString(move));
    if (move != null) {
      sendMove(move);
    } else {
      sendPass();
    }

  }

  private void sendPass() {
    player.getConnection().sendOutput(Protocol.PASS);
  }

  public void sendMove(int[] move) {
    player.getConnection().sendOutput(
        Protocol.MOVE + Protocol.SEPARATOR + gogame.Move.intersectionLocationToString(move));
  }

  public int[] getImprovingTerritoryMove() {
    int[] bestMove = getRandomMove(getValidMoves());
    int bestTerritoryDifference = -10;

    List<int[]> shuffledValidMoves = getValidMoves();
    Collections.shuffle(shuffledValidMoves);

    for (int[] move : shuffledValidMoves) {
      player.game.handleValidMove(move, player.getColor());
      Board testBoard = player.game.board.deepCopy();
      if (getTerritoryDifference(testBoard) > bestTerritoryDifference) {
        bestMove = move;
        bestTerritoryDifference = getTerritoryDifference(testBoard);
      }
    }
    return bestMove;
  }

  protected int getTerritoryDifference(Board board) {
    board.getFilledBoard();
    return board.getNumberOfStones(player.getColor()) - board.getNumberOfStones(
        player.getColor().other());
  }

  public int[] getRandomMove(List<int[]> possibleMoves) {
    return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
  }

  public List<int[]> getValidMoves() {
    return player.game.getValidMoves();
  }

}
