package gogame.server;

import gogame.Game;
import gogame.Player;
import gogame.Protocol;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Networking server for accepting connections from online players.
 */
public class GameServer extends SocketServer {

  protected Map<ServerPlayer, Game> serverMap;
  protected List<ServerPlayer> queue;
  int standardBoardDIM = 9;
  int gameCodeCounter = 0;
  boolean runServer;

  /**
   * Constructor to create a new server which listens on the given port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  public GameServer(int port) throws IOException {
    super(port);
    runServer = true;
    serverMap = new HashMap<>();
    queue = new ArrayList<>();
    startServer();
  }

  private void startServer() {
    Thread acceptConnectionsThread = new Thread(() -> {
      try {
        acceptConnections();
      } catch (IOException e) {
        e.printStackTrace(); //
      }
    });
    acceptConnectionsThread.start();
  }


  public void stopServer() {
    runServer = false;

    // TODO: Close all active connections, games etc.

    super.close();
  }

  /**
   * Creates a connection handler for the socket.
   *
   * @param socket the socket used to make the connection
   * @throws IOException
   */
  protected void handleConnection(Socket socket) throws IOException {
    ServerConnection serverConnection = new ServerConnection(socket);
    serverConnection.gameServer = this;
    serverConnection.start();
    serverConnection.sendOutput(Protocol.HELLO + Protocol.SEPARATOR + "You connected to Renske's GameServer. Please login to proceed.");
  }

  public void loginPlayer(ServerPlayer serverPlayer) {
    serverMap.put(serverPlayer, null);
  }

  public boolean usernameAvailable(String userName) {
    for (Player player : serverMap.keySet()) {
      if (userName.equalsIgnoreCase(player.getUsername())) {
        return false;
      }
    }
    return true;
  }

  public void handleQueue(ServerPlayer serverPlayer) {
    queueServerPlayer(serverPlayer);
    checkQueue();
  }

  /**
   * Add serverPlayer to gameMap.
   *
   * @param serverPlayer serverPlayer to be added to the gameMap
   */
  protected void queueServerPlayer(ServerPlayer serverPlayer) {
    if (!queue.contains(serverPlayer)) {
      queue.add(serverPlayer);
    } else {
      queue.remove(serverPlayer);
    }
    System.out.println(
        String.format("%-20s", "[CURRENT QUEUE]") + Collections.singletonList(queue));
  }

  /**
   * Check queue and start game if two players are queued.
   */
  void checkQueue() {
    if (queue.size() >= 2) {
      startGame(queue.get(0), queue.get(1));
    }
  }

  /**
   * Start a new game, remove assigned players from queue to gameMap with corresponding game.
   *
   * @param firstPlayer
   * @param secondPlayer
   */
  protected void startGame(ServerPlayer firstPlayer, ServerPlayer secondPlayer) {
    Game game = new Game(firstPlayer, secondPlayer, standardBoardDIM);

    queue.remove(firstPlayer);
    serverMap.replace(firstPlayer, game);
    firstPlayer.game = game;
    queue.remove(secondPlayer);
    serverMap.replace(secondPlayer, game);
    secondPlayer.game = game;

    game.setGameCode(gameCodeCounter);
    System.out.println(
        String.format("%-20s", "[GAME STARTED]") + "with gameCode 00" + gameCodeCounter
            + " and players " + firstPlayer.getUsername() + " and " + secondPlayer.getUsername());

    gameCodeCounter++;

    System.out.println(
        String.format("%-20s", "[CURRENT GAMEMAP]") + Collections.singletonList(serverMap));
  }

  public void quitGame(ServerPlayer serverPlayer) {
    serverMap.replace(serverPlayer, null);

    System.out.println(
        String.format("%-20s", "[CURRENT GAMEMAP]") + Collections.singletonList(serverMap));

  }

  public void handleDisconnect(ServerPlayer serverPlayer) {
    serverMap.get(serverPlayer).doResign(serverPlayer.getColor());
    serverMap.remove(serverPlayer);
  }

  @Override
  public String toString() {
    StringBuilder serverString = new StringBuilder();
    serverString.append("Renske's GameServer");
    if (!serverMap.isEmpty()) {
      serverString.append(" (").append(serverMap.size()).append(" online players are connected)");
      for (ServerPlayer player : serverMap.keySet()) {
        serverString.append("\n     ").append(player.getUsername()).append(" in Game ")
            .append(serverMap.get(player).toString());
      }
    }
    return serverString.toString();
  }
}

