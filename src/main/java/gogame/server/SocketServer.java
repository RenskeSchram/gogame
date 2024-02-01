package gogame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Networking server for accepting connections from online players.
 */
public abstract class SocketServer {
  private final ServerSocket serverSocket;

  /**
   * Constructor to create a new server which listens on the given port.
   *
   * @param port the port on which this server listens for connections
   * @throws IOException if an I/O error occurs when opening the socket
   */
  protected SocketServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
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
        System.err.println("IOException using acceptConnections()");
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
      System.err.println("IOException using close()");

    }
  }

  /**
   * Creates a connection handler for the socket.
   *
   * @param socket the socket used to make the connection
   * @throws IOException if an I/O error occurs when handling the connection.
   */
  protected abstract void handleConnection(Socket socket) throws IOException;
}
