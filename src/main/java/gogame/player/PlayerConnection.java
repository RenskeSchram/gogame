package gogame.player;

import gogame.*;
import java.io.IOException;
import java.net.Socket;
import gogame.SocketConnection;

public class PlayerConnection extends SocketConnection {
    OnlinePlayer player;

    /**
     * Abstract constructor for a new SocketConnection object with buffered reader and writer.
     *
     * @param socket the socket for establishing the connection
     * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
     */
    protected PlayerConnection(Socket socket, OnlinePlayer player) throws IOException {
        super(socket);
        this.start();
        this.player = player;
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
                player.receiveMessage(protocol[1]);
                player.sendUsername();
                break;
            }

            // QUEUE: send queue protocol message
            case Protocol.QUEUED: {
                player.receiveMessage("you are currently queued \nif you want to leave the queue, send: QEUEUE");
                break;
            }

            // MOVE: received move, play in game
            case Protocol.MOVE: {
                if (protocol.length == 3) {
                    // handleReceivedMove: doMove()
                    player.game.doMove(getLocationArray(protocol[1], player.game.board), getColor(protocol[2]));
                }
                break;
            }

            case Protocol.ACCEPTED: {
                player.receiveMessage("username is accepted \nif you want to queue send: QUEUE ");
                break;
            }
            case Protocol.REJECTED: {
                player.receiveMessage("username is rejected\nsend new username using: LOGIN\"-<username>");
                player.sendUsername();
                break;
            }
            case Protocol.GAMESTARTED: {
                // start new strategy PlayerGame
                player.game = new Game(player, new OnlinePlayer());

            }
            case Protocol.MAKEMOVE: {
                player.strategy.determineMove();
                break;
            }
            case Protocol.GAMEOVER: {
                player.receiveMessage(input);
                // TODO: handleGameOver: ask to reconnect to server?
                break;
            }
            case Protocol.ERROR: {
                player.receiveMessage(input);
                break;
            }
        }
    }


    @Override
    public void sendOutput(String output) {
        super.sendOutput(output);
    }

    /**
     * Handles a disconnection of the connection.
     */
    @Override
    protected void handleDisconnect() {

    }
}
