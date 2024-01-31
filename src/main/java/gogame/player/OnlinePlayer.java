package gogame.player;

import gogame.Player;
import gogame.Protocol;
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

  public void makeConnection(Socket socket) throws IOException {
    playerConnection = new PlayerConnection(socket, this);
  }

  @Override
  public SocketConnection getConnection() {
    return playerConnection;
  }


  public void doMove(int[] location) {
    assert game instanceof StrategyGame;
    playerConnection.sendOutput(
        Protocol.MOVE + Protocol.SEPARATOR + gogame.Move.intersectionLocationToString(location));
  }

  public void doPass() {
    playerConnection.sendOutput(Protocol.PASS);
  }

  @Override
  public void passGameUpdate(String gameUpdate) {
    // do nothing, passing not needed for online player
  }

  public void sendUsername() {
    // send username based on online player strategy
    strategy.getUsername();
  }

  public void sendQueue() {
    strategy.sendQueue();
  }

  public void setStrategy(String type) {
    // choose strategy
    if (type.equals("computer")) {
      strategy = new ComputerStrategy(this);
      receiveMessage("Computer Strategy applied");
    } else {
      receiveMessage("Human Strategy applied");
    }
  }

  public void receiveMessage(String string) {
    tui.printMessage(string);
  }
}
