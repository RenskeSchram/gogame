package gogame.server;

import gogame.Game;
import gogame.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Networking server for accepting connections from online players.
 */
public class GameServer {

  private final ServerSocket serverSocket;
  protected Map<ServerPlayer, Game> gameMap;
  protected List<ServerPlayer> queue;
  int DIM = 9;
  int gameCodeCounter = 0;
  PrintWriter writer;


  /**
   * Constructor to create a new server which listens on the given port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  public GameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    gameMap = new HashMap<>();
    queue = new ArrayList<>();
    writer = new PrintWriter(new FileWriter("Serverlog.txt", true));
  }

  /**
   * @return the port on which this server listens for connections.
   */
  protected int getPort() {
    return serverSocket.getLocalPort();
  }

  /**
   * Accepts connections and starts a new thread for each connection.
   *
   * @throws IOException if an I/O error occurs when waiting for a connection
   */
  protected void acceptConnections() throws IOException {
    while (!serverSocket.isClosed()) {
      try {
        Socket socket = serverSocket.accept();
        handleConnection(socket);
      } catch (SocketException ignored) {
        log("IOException using acceptConnections()");
      }

      }
  }

  /**
   * Closes the server socket.
   */
  protected synchronized void close() {
    try {
      if (!serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch (IOException ignored) {
      log("IOException using close()");

    }
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
  }

  /**
   * Check queue and start game if two players are queued.
   */
  private void checkQueue() {
    if (queue.size() >= 2) {
      startGame(queue.get(0), queue.get(1));
    }
  }

  /**
   * Add serverPlayer to gameMap.
   *
   * @param serverPlayer serverPlayer to be added to the gameMap
   */
  protected void queueServerPlayer(ServerPlayer serverPlayer) {
    if (!queue.contains(serverPlayer)) {
      queue.add(serverPlayer);
      checkQueue();
    } else {
      queue.remove(serverPlayer);
    }
    System.out.println("[SERVERLOG] checking QUEUE");
    System.out.println(Collections.singletonList(queue));
  }

  /**
   * Start a new game, remove assigned players from queue to gameMap with corresponding game.
   *
   * @param firstPlayer
   * @param secondPlayer
   */
  protected void startGame(ServerPlayer firstPlayer, ServerPlayer secondPlayer) {
    Game game = new Game(firstPlayer, secondPlayer, DIM);

    queue.remove(firstPlayer);
    gameMap.put(firstPlayer, game);
    firstPlayer.game = game;
    queue.remove(secondPlayer);
    gameMap.put(secondPlayer, game);
    secondPlayer.game = game;

    game.setGameCode(gameCodeCounter);

    log("[GAME STARTED] with gameCode 00" + gameCodeCounter + " and players "
        + firstPlayer.getUsername() + " and " + secondPlayer.getUsername());

    gameCodeCounter++;

    System.out.println("[SERVERLOG] checking ServerMap");
    System.out.println(Collections.singletonList(gameMap) + "\n");
  }

  //TODO: what about idle players and players in game?
  public boolean usernameAvailable(String userName) {
    for (Player player : queue) {
      if (userName.equalsIgnoreCase(player.getUsername())) {
        return false;
      }
    }
    return true;
  }

  public void quitGame(ServerPlayer player) {
    gameMap.remove(player);

    System.out.println("[SERVERLOG] checking ServerMap");
    System.out.println(Collections.singletonList(gameMap) + "\n");

  }


  protected void log(String logMessage) {
    if (writer != null) {
      writer.println(logMessage);
      writer.flush();
    }
  }

  /**
   * Main function to retrieve port number and start create a new GameServer.
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    // Handling port input with retry
    int port = 0;
    boolean validPortInput = false;
    while (!validPortInput) {
      System.out.print("server port number: \n");
      try {
        port = scanner.nextInt();
        validPortInput = true;
      } catch (InputMismatchException e) {
        System.err.println("Invalid port number. Please enter a valid port.");
        scanner.nextLine();
      }
    }

    GameServer gameServer = new GameServer(port);
    gameServer.acceptConnections();
  }
}

