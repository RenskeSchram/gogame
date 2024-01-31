package gogame.player.strategy;

import gogame.player.OnlinePlayer;
import gogame.player.StrategyGame;

public class HumanStrategy implements Strategy {
  StrategyGame strategyGame;
  OnlinePlayer player;

  public HumanStrategy(OnlinePlayer player){
    this.player = player;
  }

  @Override
  public void getUsername() {
    player.receiveMessage("Send a username using: LOGIN~<username>");
  }

  @Override
  public void determineMove() {
    player.receiveMessage("Send a new move using: MOVE~<int> or MOVE~<int, int>");
  }

  @Override
  public void sendQueue() {
    //
  }

  @Override
  public void setGame(StrategyGame game) {
    this.strategyGame = game;
  }
}
