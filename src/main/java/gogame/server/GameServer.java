package gogame.server;

import gogame.Game;
import gogame.Player;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Networking server for accepting connections from online players.
 */
public class GameServer {
    private final ServerSocket serverSocket;
    protected Map<ServerPlayer, Game> serverMap;
    protected List<ServerPlayer> queue;

    /**
     * Constructor to create a new server which listens on the given port.
     *
     * @param port the port on which this server listens for connections
     * @throws IOException if an I/O error occurs when opening the socket
     */
    protected GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverMap = new HashMap<>();
        queue = new ArrayList<>();
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
        System.out.println("Checking queue with size"+ queue.size());
        if (queue.size() >= 2) {
            startGame(queue.get(0), queue.get(1));
        }
    }

    /**
     * Add serverPlayer to serverMap.
     * @param serverPlayer serverPlayer to be added to the serverMap
     */
    protected void queueServerPlayer(ServerPlayer serverPlayer) {
        if (!queue.contains(serverPlayer)) {
            queue.add(serverPlayer);
            checkQueue();
        } else {
            queue.remove(serverPlayer);
        }
    }

    /**
     * Start a new game, remove assigned plauers from queue to serverMap with corresponding game.
     *
     * @param firstPlayer
     * @param secondPlayer
     */
    protected void startGame(ServerPlayer firstPlayer, ServerPlayer secondPlayer) {
        Game game = new Game(firstPlayer, secondPlayer);
        queue.remove(firstPlayer);
        serverMap.put(firstPlayer, game);
        firstPlayer.game = game;
        queue.remove(secondPlayer);
        serverMap.put(secondPlayer, game);
        secondPlayer.game = game;
    }

    /**
     * Main function to retrieve port number and start create a new GameServer.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("server port number: ");
        int port = scanner.nextInt();
        GameServer gameServer = new GameServer(port);
        gameServer.acceptConnections();
    }

    public boolean correctUsername(String userName) {
        boolean correct = true;
        for (Player player : serverMap.keySet()) {
            if (player.getUsername().equals(userName)) {correct = false; break;}
        }
        return correct;
    }
}
