package gogame.player;

import gogame.Game;
import gogame.Player;

public class StrategyGame extends Game {

  /**
   * Constructor for new Game object with players and a new Board.
   *
   * @param firstPlayer Player object, plays with BLACK
   * @param secondPlayer Player object, plaus with white
   * @param DIM
   */
  public StrategyGame(Player firstPlayer, Player secondPlayer, int DIM) {
    super(firstPlayer, secondPlayer, DIM);
    this.useTimer = false;
  }

}
