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
import java.util.Map.Entry;

/**
 * Networking server for accepting connections from online players.
 */
public class GameServer extends SocketServer {

  protected Map<ServerPlayer, Game> serverMap;
  private List<ServerPlayer> queue;
  private int serverBoardDIM;
  private int gameCodeCounter = 0;

  /**
   * Constructor to create a new server which listens on the given port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  public GameServer(int port) throws IOException {
    super(port);
    serverBoardDIM = 9;
    serverMap = new HashMap<>();
    queue = new ArrayList<>();
    startServer();
  }

  /**
   * Start server and accept new connections.
   */
  private void startServer() {
    Thread acceptConnectionsThread = new Thread(() -> {
      try {
        acceptConnections();
      } catch (IOException e) {
        System.err.println("IOExceptions for while accepting connections.");
      }
    });
    acceptConnectionsThread.start();
  }

  /**
   * Start server, end all games, remove all logged players and close socket.
   */
  public void stopServer() {
    for (Game game : serverMap.values()) {
      game.setActive(false);
    }
    serverMap.clear();
    super.close();
  }

  /**
   * Creates a connection handler for the socket.
   *
   * @param socket the socket used to make the connection
   */
  protected void handleConnection(Socket socket) throws IOException {
    ServerConnection serverConnection = new ServerConnection(socket);
    serverConnection.gameServer = this;
    serverConnection.start();
    serverConnection.sendOutput(Protocol.HELLO + Protocol.SEPARATOR
        + "You connected to Renske's GameServer. Please login to proceed.");
  }

  public void handleDisconnect(ServerPlayer serverPlayer) {
    // check queue
    queue.remove(serverPlayer);
    // check serverMap and game
    if (serverMap.get(serverPlayer) != null) {
      serverMap.get(serverPlayer).doResign(serverPlayer.getColor());
    }
    removeInactiveGames();
    serverMap.remove(serverPlayer);
    System.out.println(
        String.format("%-20s", "[CURRENT GAMEMAP]") + Collections.singletonList(serverMap));
  }

  @Override
  public String toString() {
    StringBuilder serverString = new StringBuilder();
    serverString.append("Renske's GameServer");
    if (!serverMap.isEmpty()) {
      serverString.append(" (").append(serverMap.size()).append(" online players are connected)");
      for (ServerPlayer player : serverMap.keySet()) {
        serverString.append("\n     ").append(player.getUsername());
        if (serverMap.get(player) != null) {
          serverString.append(" in Game ").append(serverMap.get(player).toString());
        }
      }
    }

    return serverString.toString();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                Logging of player-status                                    ///
  //////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * Login new players and save player status in serverMap.
   *
   * @param serverPlayer new player.
   */
  public void loginPlayer(ServerPlayer serverPlayer) {
    if (serverMap.containsKey(serverPlayer)) {
      System.err.println("Cannot LOGIN this player twice");
    } else {
      serverMap.put(serverPlayer, null);
    }
  }

  /**
   * Verify if this username is available.
   *
   * @param userName username to be checked.
   * @return true of username is not taken by another connected player.
   */
  public boolean usernameAvailable(String userName) {
    for (Player player : serverMap.keySet()) {
      if (userName.equalsIgnoreCase(player.getUsername())) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return queue
   */
  public List<ServerPlayer> getQueue() {
    return queue;
  }


  /**
   * Add serverPlayer to queue.
   *
   * @param serverPlayer serverPlayer to be added to the queue
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
  protected synchronized void checkQueue() {
    if (queue.size() >= 2) {
      startGame(queue.get(0), queue.get(1));

    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                         Handling games                                     ///
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Start a new game, remove assigned players from queue to gameMap with corresponding game.
   *
   * @param firstPlayer  first player in game.
   * @param secondPlayer second player in game.
   */
  protected void startGame(ServerPlayer firstPlayer, ServerPlayer secondPlayer) {
    Game game = new Game(firstPlayer, secondPlayer, serverBoardDIM);
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

  public void removeInactiveGames() {
    for (Entry<ServerPlayer, Game> entry : serverMap.entrySet()) {
      if (entry.getValue() != null) {
        if (!entry.getValue().getActive()) {
          serverMap.replace(entry.getKey(), null);
        }
      }
    }
  }

  public void setServerBoardDIM(int DIM) {
    serverBoardDIM = DIM;

  }

  public int getServerBoardDIM() {
    return serverBoardDIM;
  }

  public int getGameCodeCounter() {
    return gameCodeCounter;
  }

}

