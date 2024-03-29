package gogame;

import java.io.*;
import java.net.Socket;

/**
 * Abstract class to establish the connections for reading and writing of messages from and towards
 * the gameserver and players.
 */
public abstract class SocketConnection {

  protected final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;
  private boolean active = false;

  /**
   * Abstract constructor for a new SocketConnection object with buffered reader and writer.
   *
   * @param socket the socket for establishing the connection
   * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
   */
  protected SocketConnection(Socket socket) throws IOException {
    this.socket = socket;
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  /**
   * Starting the SocketConnection and a thread to receive messages and call methods of the connection.
   */
  public void start() {
    if (active) {
      System.err.println("IllegalStateException. Cannot start a SocketConnection twice");
    }
    active = true;
    Thread thread = new Thread(this::receiveInput);
    thread.start();
  }

  /**
   * The thread to receive messages. For every message, the handleMessage method is called.
   * When starting the thread, it will call the handleStart method of the handler.
   * When the connection is closed, it will call the handleDisconnect method of the handler.
   */
  private void receiveInput() {
    handleStart();
    try {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        handleInput(inputLine);
      }
    } catch (IOException e) {
      System.err.println("IOException. Received incorrect input");
    } finally {
      close();
      handleDisconnect();
    }
  }

  /**
   * Sends message over the network in String format.
   *
   * @param output the line of input to be sent.
   */
  public void sendOutput(String output) {
    if (output != "") {
      try {
        out.write(output);
        out.newLine();
        out.flush();
      } catch (IOException e) {
        System.err.println("IOException. Received output not correctly send");
        close();
      }
    }
  }

  /**
   * Closes the connection. No messages can be received afterward.
   */
  public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      System.err.println("IOException. Socket was not correctly closed");
    }
  }

  /**
   * Handles the start of a connection.
   */
  protected void handleStart() {}

  /**
   * Handles the received input message.
   *
   * @param input line of input to be handled
   */
  protected abstract void handleInput(String input);

  /**
   * Handles a disconnection of the connection.
   */
  protected abstract void handleDisconnect();


  /**
   * Correctly format string input to intersection array.
   *
   * @param input String input of intersection.
   * @param DIM dimension of board for correct formatting.
   * @return int array with intersection.
   */
  protected int[] getLocationArray(String input, int DIM) {
    String[] splitInput = input.split(",");
    try {
      if (splitInput.length == 1) {
        int col = Integer.parseInt(splitInput[0]) % DIM;
        int row = Integer.parseInt(splitInput[0]) / DIM;
        return new int[]{col, row};

      } else if (splitInput.length == 2) {
        return new int[]{Integer.parseInt(splitInput[0]), Integer.parseInt(splitInput[1])};

      } else {
        sendOutput(
            Protocol.ERROR + Protocol.SEPARATOR + "incorrect number of comma inputs for location "
                + "(use format:  MOVE-<int> or MOVE-<int, int>)");
        return new int[]{-1, -1};
      }
    } catch (NumberFormatException e) {
      sendOutput(
          Protocol.ERROR + Protocol.SEPARATOR + "incorrect input for location "
              + "(check format:  MOVE-<int> or MOVE-<int, int>)");
      return new int[]{-1, -1};
    }
  }

  /**
   * Format input String to color object.
   *
   * @param input String defining color.
   * @return color object.
   */
  protected Color getColor(String input) {
    return switch (input.toLowerCase()) {
      case "white" -> Color.WHITE;
      case "black" -> Color.BLACK;
      case "empty" -> Color.EMPTY;
      default -> {
        sendOutput(Protocol.ERROR + Protocol.SEPARATOR + "color is not recognized");
        yield null;
      }
    };
  }

  public void sendError(String errorMessage) {
    sendOutput(Protocol.ERROR + Protocol.SEPARATOR + errorMessage);
  }

}
