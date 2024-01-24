package gogame;

import java.io.*;
import java.net.Socket;

/**
 * Abstract class to establish the connections for reading and writing of messages from and towards
 * the gameserver and players.
 */
public abstract class SocketConnection {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private boolean active = false;

    /**
     * Abstract constructor for a new SocketConnection object with buffered reader and writer.
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
            throw new IllegalStateException("Cannot start a SocketConnection twice");
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
            // exception ignored, connection is closed
        } finally {
            close();
            handleDisconnect();
        }
    }

    /**
     * Sends message over the network in String format.
     * @param output the line of input to be sent.
     */
    public void sendOutput(String output) {
        try {
            out.write(output);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            // connection is closed
            close();
        }
    }

    /**
     * Closes the connection. No messages can be received afterward.
     */
    protected void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // exception ignored, connection is closed
        }
    }

    /**
     * Handles the start of a connection.
     */
    protected void handleStart() {}

    /**
     * Handles the received input message.
     * @param input line of input to be handled
     */
    protected abstract void handleInput(String input);

    /**
     * Handles a disconnection of the connection.
     */
    protected abstract void handleDisconnect();


    protected int[] getLocationArray(String input, Board board) {
        String[] splittedInput = input.split(",");
        if (splittedInput.length == 1) {
            int row = Integer.parseInt(splittedInput[0]) / board.DIM;
            int col = Integer.parseInt(splittedInput[0]) % board.DIM -1 ;
            return new int[]{col, row};

        } else if (splittedInput.length == 2) {
            return new int[]{Integer.parseInt(splittedInput[0]), Integer.parseInt(splittedInput[1])};

        } else {
            System.out.println("ERROR with provided location");
            return new int[]{-1, -1};
        }

    }

    protected Color getColor(String input) {
        switch (input.toLowerCase()) {
            case "white":
                return Color.WHITE;
            case "black":
                return Color.BLACK;
            case "empty":
                return Color.EMPTY;
            default:
                sendOutput(Protocol.ERROR + Protocol.SEPARATOR + "color is not recognized");
                return null;
        }
    }


}