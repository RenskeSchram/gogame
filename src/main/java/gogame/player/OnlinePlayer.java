package gogame.player;

import gogame.Color;
import gogame.Player;
import gogame.SocketConnection;
import gogame.player.strategy.ComputerStrategy;
import gogame.player.strategy.HumanStrategy;
import gogame.player.strategy.Strategy;
import java.io.IOException;
import java.net.Socket;

public class OnlinePlayer extends Player {

  public PlayerConnection playerConnection;
  public PlayerTUI tui;
  public Strategy strategy;

  public OnlinePlayer(){
    strategy = new HumanStrategy(this);
  }

  @Override
  public SocketConnection getConnection() {
    return playerConnection;
  }

  /**
   * Send move to board and add this ServerPlayer as parameter.
   * @param location
   */
  public void doMove(int[] location, Color color) {
    assert game instanceof StrategyGame;
    // play moves for both players in the game
    game.doMove(location, color);
  }

  public void doPass(Color color) {
    // play moves for both players in the game
    game.doPass(color);
  }

  @Override
  public void passGameUpdate(String gameUpdate) {
    // do nothing, passing not needed for online player
  }

  public void sendUsername() {
    // send username based on online player strategy
    strategy.getUsername();
  }

  public void setStrategy(String type) {
    // choose strategy
    if (type.equals("computer")){
      strategy = new ComputerStrategy(this);
    } else {
      receiveMessage("human strategy applied");
    }
  }

  public void receiveMessage(String string) {
    // print received messages via the TUI
    tui.printMessage(string);
  }

  public void makeConnection(Socket socket) throws IOException {
    // create a PlayerConnection Object
    playerConnection = new PlayerConnection(socket, this);
  }


  public void sendQueue() {
    strategy.sendQueue();
  }
}
