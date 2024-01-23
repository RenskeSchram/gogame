package player;

import gogame.Game;
import gogame.Player;
import gogame.Protocol;
import gogame.ServerPlayer;
import java.io.IOException;
import java.net.Socket;
import server.SocketConnection;

public class PlayerConnection extends SocketConnection {
    Player player;
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
        String[] protocol = input.split(Protocol.SEPARATOR);
        switch (protocol[0]) {

            case Protocol.HELLO: {
                // TODO: send username

                break;
            }

            // QUEUE: send queue protocol message to serverPlayer
            case Protocol.QUEUED: {
                //TODO: handleQueued
                break;
            }

            // MOVE: if player is in a game, send move to serverPlayer
            case Protocol.MOVE: {
                if (protocol.length == 2) {
                    //TODO: handleReceivedMove
                }
                break;
            }

            case Protocol.ACCEPTED: {

                break;
            }
            case Protocol.REJECTED: {
                //TODO: handleReceivedMove
                break;
            }
            case Protocol.GAMESTARTED: {
                //TODO: start new Game
            }
            case Protocol.MAKEMOVE: {
                //TODO: handleReceivedMove
                break;
            }
            case Protocol.GAMEOVER: {
                //TODO: handleReceivedMove
                break;
            }
            case Protocol.ERROR: {
                //TODO: handleReceivedMove
                break;
            }
        }
    }

    /**
     * Handles a disconnection of the connection.
     */
    @Override
    protected void handleDisconnect() {

    }
}
