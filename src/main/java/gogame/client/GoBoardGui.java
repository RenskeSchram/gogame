package gogame.client;

import gogame.Board;
import gogame.Color;
import gogame.gui.GoGuiIntegrator;

/**
 * GoGui Board real time visualization.
 *
 */
public class GoBoardGui {

  private final GoGuiIntegrator gogui;

  public GoBoardGui(int boardSize) {
    gogui = new GoGuiIntegrator(true, true, boardSize);
    gogui.startGUI();
    gogui.setBoardSize(boardSize);
  }

  /**
   * Update GUI visualization based on retrieved accepted move by Server.
   *
   * @param board board to be shown.
   */
  public void updateGUI(Board board) {
    gogui.clearBoard();

    for (int row = 0; row < board.getDIM(); row++) {
      for (int col = 0; col < board.getDIM(); col++) {
        switch (board.getStone(new int[]{col, row})) {
          case Color.WHITE:
            gogui.addStone(col, row, true);
            break;
          case Color.BLACK:
            gogui.addStone(col, row, false);
            break;
          case Color.EMPTY, Color.NEUTRAL:
            // do nothing
            break;
        }
      }
    }
  }
}
