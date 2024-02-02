package gogame.server;

import gogame.Color;
import gogame.Protocol;
import gogame.SocketConnection;
import java.io.IOException;
import java.net.Socket;

/**
 * ServerConnection to handle receiving and sending messages according to protocol.
 */
public class ServerConnection extends SocketConnection {

  public ServerPlayer serverPlayer;
  public GameServer gameServer;

  public ServerConnection(Socket socket) throws IOException {
    super(socket);
    initializeServerPlayer();
  }

  /**
   * Deciphers the received input message using the protocol messages.
   *
   * @param input line of input to be handled
   */
  @Override
  public void handleInput(String input) {
    System.out.println("\u001B[37m" + String.format("%-20s", "[ " + serverPlayer.getUsername() + " ]") + input+ "\u001B[0m");

    String[] protocol = input.split(Protocol.SEPARATOR);
    switch (protocol[0]) {
      case Protocol.HELLO:
        sendError("Hello to you too! We have exchanged HELLOs before right?");
        break;
      case Protocol.LOGIN:
        handleLogin(protocol);
        break;
      case Protocol.QUEUE:
        handleQueue();
        break;
      case Protocol.MOVE:
        handleMove(protocol);
        break;
      case Protocol.PASS:
        handlePass();
        break;
      case Protocol.RESIGN:
        handleResign();
        break;
      case Protocol.PRINT:
        sendOutput(Protocol.PRINT);
        break;
      case Protocol.ERROR:
        handleError(protocol);
        break;
      default:
        sendError("Could not handle message on serverside");
    }
  }
  private void initializeServerPlayer() {
    serverPlayer = new ServerPlayer();
    serverPlayer.serverConnection = this;
  }

  private void handleLogin(String[] protocol) {
    if (protocol.length >= 2) {
      if (gameServer.usernameAvailable(protocol[1]) && serverPlayer.getUsername() == null) {
        serverPlayer.setUsername(protocol[1]);
        gameServer.loginPlayer(serverPlayer);
        sendOutput(Protocol.ACCEPTED + Protocol.SEPARATOR + protocol[1]);
      } else {
        sendOutput(Protocol.REJECTED + Protocol.SEPARATOR + protocol[1]);
      }
    } else {
      sendError("Could not handle LOGIN, no username provided");
    }
  }

  private void handleQueue() {
    if (serverPlayer != null && serverPlayer.getUsername() != null) {
      gameServer.queueServerPlayer(serverPlayer);
      if (gameServer.getQueue().contains(serverPlayer)) {
        sendOutput(Protocol.QUEUED);
      }
      gameServer.checkQueue();
    } else {
      sendError("Correct LOGIN required to queue");
    }
  }

  private void handleMove(String[] protocol) {
    if (protocol.length >= 2 && gameServer.serverMap.get(serverPlayer) != null) {
      serverPlayer.doMove(getLocationArray(protocol[1], serverPlayer.game.board.getDIM()), Color.EMPTY);
    } else {
      sendError("Could not handle MOVE");
    }
  }

  private void handlePass() {
    if (gameServer.serverMap.containsKey(serverPlayer)) {
      serverPlayer.doPass();
    } else {
      sendError("Could not handle PASS");
    }
  }

  private void handleResign() {
    if (gameServer.serverMap.containsKey(serverPlayer)) {
      serverPlayer.doResign();
    } else {
      sendError("Could not handle RESIGN");
    }
  }

  /**
   * Handle error by logging the error.
   * @param protocol input message.
   */
  private void handleError(String[] protocol) {
    if (protocol.length >= 1) {
      System.err.println("[RECEIVED ERROR]" + protocol[1]);
    } else {
      System.err.println("[RECEIVED ERROR]");
    }
  }

  @Override
  public void sendOutput(String output) {
    super.sendOutput(output);
    //System.out.println(output);
  }

  /**
   * Handles a disconnection of the connection.
   */
  @Override
  protected void handleDisconnect() {
    System.out.println(String.format("%-20s", "[DISCONNECTED]") + serverPlayer.getUsername());
    gameServer.handleDisconnect(serverPlayer);
  }
}
