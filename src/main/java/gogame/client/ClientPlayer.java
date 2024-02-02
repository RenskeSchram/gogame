package gogame.client;

import gogame.Player;
import gogame.Protocol;
import gogame.SocketConnection;
import gogame.client.strategy.ComputerStrategy;
import gogame.client.strategy.HumanStrategy;
import gogame.client.strategy.Strategy;
import java.io.IOException;
import java.net.Socket;

/**
 * Client player. Has a tui and strategy to determine plays in the game.
 */

public class ClientPlayer extends Player {

  private ClientConnection playerConnection;
  protected ClientTUI tui;
  protected Strategy strategy;

  public ClientPlayer(){
    strategy = new HumanStrategy(this);
  }

  protected void makeConnection(Socket socket) throws IOException {
    playerConnection = new ClientConnection(socket, this);
  }

  @Override
  public SocketConnection getConnection() {
    return playerConnection;
  }


  public void doMove(int[] location) {
    assert game instanceof StrategyGame;
    playerConnection.sendOutput(
        Protocol.MOVE + Protocol.SEPARATOR + intersectionArrayToSingleInt(location));
  }

  private int intersectionArrayToSingleInt(int[] intersectionArray) {
    return intersectionArray[1]* 9 + intersectionArray[0];
  }


  public void doPass() {
    playerConnection.sendOutput(Protocol.PASS);
  }

  @Override
  public void passGameUpdate(String gameUpdate) {
    // do nothing, passing not needed for client player
  }

  public void sendUsername() {
    // send username based on client player strategy
    strategy.getUsername();
  }

  public void sendQueue() {
    strategy.sendQueue();
  }

  public void setStrategy(String type) {
    // choose strategy
    if (type.equalsIgnoreCase("computer")) {
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
