package connection;

import gogame.ServerPlayer;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends SocketConnection{
    ServerPlayer serverPlayer;

    public ServerConnection(Socket socket) throws IOException {
        super(socket);
    }


    public void start(){

    }

    /**
     * Handles the received input message.
     *
     * @param input line of input to be handled
     */
    @Override
    protected void handleInput(String input) {

    }

    protected void handleOutput(String output) {
        super.sendOutput(output);
    }
    /**
     * Handles a disconnection of the connection.
     */
    @Override
    protected void handleDisconnect() {
    }
}
