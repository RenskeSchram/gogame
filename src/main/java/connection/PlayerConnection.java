package connection;

import java.io.IOException;
import java.net.Socket;

public class PlayerConnection extends SocketConnection {
    /**
     * Abstract constructor for a new SocketConnection object with buffered reader and writer.
     *
     * @param socket the socket for establishing the connection
     * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
     */
    protected PlayerConnection(Socket socket) throws IOException {
        super(socket);
        this.start();
    }

    /**
     * Handles the received input message.
     *
     * @param input line of input to be handled
     */
    @Override
    protected void handleInput(String input) {

    }

    /**
     * Handles a disconnection of the connection.
     */
    @Override
    protected void handleDisconnect() {

    }
}
