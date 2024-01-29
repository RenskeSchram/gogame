package gogame.player.strategy;

import gogame.Board;
import gogame.Color;
import gogame.Protocol;
import gogame.player.OnlinePlayer;
import java.util.ArrayList;
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
    System.out.println(getValidMoves().size());
    player.getConnection().sendOutput(Protocol.PRINT);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    if (move == null) {
      if (getTerritoryDifference(player.game.board) < 0) {
        sendMove(getRandomMove(getValidMoves()));
      } else {
        sendPass();
      }
    } else {
      sendMove(move);
      System.out.println(Arrays.toString(move) + player.getColor());
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
    int[] bestMove = null;
    int bestTerritoryDifference = -1;

    List<int[]> shuffledValidMoves = getValidMoves();
    Collections.shuffle(shuffledValidMoves);

    for (int[] move : shuffledValidMoves) {
      Board testBoard = player.game.board.deepCopy();
      player.game.handleValidMove(move, player.getColor());
      if (getTerritoryDifference(player.game.board) > bestTerritoryDifference) {
        bestMove = move;
        bestTerritoryDifference = getTerritoryDifference(player.game.board);
      }
      player.game.board = testBoard;
    }
    return bestMove;
  }

  protected List<int[]> getCaptures(Color color, int numOfLiberties) {
    List<int[]> possibleCaptures = new ArrayList<>();
    for (int[] stone : player.game.board.getStonesWithThisColor(color.other())) {
      if (player.game.board.getLibertiesGroup(player.game.board.getGroup(stone, true)).size() == numOfLiberties) {
        possibleCaptures.add(player.game.board.getLibertiesGroup(player.game.board.getGroup(stone, true)).get(0));
      }
    }
    return possibleCaptures;
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
    return player.game.getValidMoves();
  }

}
