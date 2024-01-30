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
    System.out.println(String.format("%-20s", "[RECEIVED MESSAGE]") + input);

    String[] protocol = input.split(Protocol.SEPARATOR);
    switch (protocol[0]) {
      case Protocol.HELLO:
        sendError("Hello to you too! We have exchanged HELLOs before right?");
        break;
      case Protocol.LOGIN:
        handleLoginProtocol(protocol);
        break;
      case Protocol.QUEUE:
        handleQueueProtocol();
        break;
      case Protocol.MOVE:
        handleMoveProtocol(protocol);
        break;
      case Protocol.PASS:
        handlePassProtocol(protocol);
        break;
      case Protocol.RESIGN:
        handleResignProtocol(protocol);
        break;
      case Protocol.PRINT:
        sendOutput(Protocol.PRINT);
        break;
      case Protocol.ERROR:
        handleErrorProtocol(protocol);
        break;
      default:
        sendError("Could not handle message on serverside");
    }
  }
  private void initializeServerPlayer() {
    serverPlayer = new ServerPlayer();
    serverPlayer.serverConnection = this;
  }

  private void handleLoginProtocol(String[] protocol) {
    if (protocol.length >= 2) {
      if (gameServer.usernameAvailable(protocol[1]) && serverPlayer.getUsername() == null) {
        serverPlayer.setUsername(protocol[1]);
        sendOutput(Protocol.ACCEPTED + Protocol.SEPARATOR + protocol[1]);
      } else {
        sendError("GAMESERVER could not handle LOGIN, no username provided");
      }
    }
  }

  private void handleQueueProtocol() {
    if (serverPlayer != null && serverPlayer.getUsername() != null) {
      gameServer.queueServerPlayer(serverPlayer);
      if (gameServer.queue.contains(serverPlayer)) {
        sendOutput(Protocol.QUEUED);
      }
    } else {
      sendError("GAMESERVER correct LOGIN required to queue");
    }
  }

  private void handleMoveProtocol(String[] protocol) {
    if (protocol.length >= 2 && gameServer.gameMap.containsKey(serverPlayer)) {
      serverPlayer.doMove(getLocationArray(protocol[1], serverPlayer.game.board.DIM), Color.EMPTY);
    } else {
      sendError("GAMESERVER could not handle MOVE");
    }
  }

  private void handlePassProtocol(String[] protocol) {
    if (gameServer.gameMap.containsKey(serverPlayer)) {
      serverPlayer.doPass();
    } else {
      sendError("GAMESERVER could not handle PASS");
    }
  }

  private void handleResignProtocol(String[] protocol) {
    if (gameServer.gameMap.containsKey(serverPlayer)) {
      serverPlayer.doResign();
    } else {
      sendError("GAMESERVER could not handle RESIGN");
    }
  }

  private void handleErrorProtocol(String[] protocol) {
    if (protocol.length >= 1) {
      System.err.println("[RECEIVED ERROR]" + protocol[1]);
    } else {
      System.err.println("[RECEIVED ERROR]");
    }
  }

  public void sendOutput(String output) {
    super.sendOutput(output);
    //System.out.println(String.format("%-20s", "[OUTPUT MESSAGE]") + output);
  }

  /**
   * Handles a disconnection of the connection.
   */
  @Override
  protected void handleDisconnect() {
    // Perform any cleanup or notification on disconnect
  }
}
