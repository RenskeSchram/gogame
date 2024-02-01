package gogame.client.strategy;

import gogame.client.StrategyGame;

/**
 * Strategy for ClientPlayer, determines how responds are required.
 */
public interface Strategy {

  void getUsername();

  void determineMove();

  void sendQueue();

  void setGame(StrategyGame game);
}
