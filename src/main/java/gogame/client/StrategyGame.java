package gogame.client;

import gogame.Color;
import gogame.Game;
import gogame.Player;

/**
 * Inherited object from Game class, with adjustments for the Client, e.g. update GUI.
 */
public class StrategyGame extends Game {

  GoBoardGui goGui;

  /**
   * Constructor for new Game object with players and a new Board.
   *
   * @param firstPlayer Player object, plays with BLACK
   * @param secondPlayer Player object, plays with white
   * @param DIM
   */
  public StrategyGame(Player firstPlayer, Player secondPlayer, int DIM) {
    super(firstPlayer, secondPlayer, DIM);
    this.goGui = new GoBoardGui(DIM);
  }

  @Override
  public void doMove(int[] location, Color color) {
    super.doMove(location, color);
    updateGoGui();
  }

  private void updateGoGui() {
    goGui.updateGUI(board);
  }

  @Override
  public void end(Color color) {
    super.end(color);
    System.out.println(board.toString());
    System.out.println("WHITE: " + board.getStonesWithThisColor(Color.WHITE).size());
    System.out.println("BLACK: " + board.getStonesWithThisColor(Color.BLACK).size());
  }
}
