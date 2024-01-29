package gogame.player.strategy;

import gogame.player.StrategyGame;

public interface Strategy {

  void getUsername();

  void determineMove();

  void sendQueue();

  void setGame(StrategyGame game);
}
