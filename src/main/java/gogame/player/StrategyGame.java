package gogame.player;

import gogame.Color;
import gogame.Game;
import gogame.GoBoardGui;
import gogame.Player;

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
    //this.useTimer = false;
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

//  public static void main(String[] args) {
//    StrategyGame game = new StrategyGame(new OnlinePlayer(), new OnlinePlayer(), 9);
//    game.handleValidMove(new int[]{5,6}, Color.WHITE);
//    game.handleValidMove(new int[]{5,5}, Color.BLACK);
//  }
}
