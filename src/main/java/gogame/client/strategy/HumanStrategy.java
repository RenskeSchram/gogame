package gogame.client.strategy;

import gogame.client.ClientPlayer;
import gogame.client.StrategyGame;

public class HumanStrategy implements Strategy {
  protected StrategyGame strategyGame;
  private final ClientPlayer player;

  public HumanStrategy(ClientPlayer player){
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
  }

  @Override
  public void setGame(StrategyGame game) {
    this.strategyGame = game;
  }
}
