package gogame.player;

import gogame.*;
import java.io.IOException;
import java.net.Socket;
import gogame.SocketConnection;
import java.util.Objects;

public class PlayerConnection extends SocketConnection {
    public OnlinePlayer player;

    /**
     * Abstract constructor for a new SocketConnection object with buffered reader and writer.
     *
     * @param socket the socket for establishing the connection
     * @throws IOException if there is an I/O exception while initializing the Reader/Writer objects
     */
    public PlayerConnection(Socket socket, OnlinePlayer player) throws IOException {
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
    public void handleInput(String input) {
        String[] protocol = input.split(Protocol.SEPARATOR);
        switch (protocol[0]) {

            // HELLO handshake, ready to log in with username
            case Protocol.HELLO: {
                if (protocol.length > 1) {
                    player.receiveMessage(protocol[1]);
                }
                player.sendUsername();
                break;
            }

            // QUEUE: send queue protocol message
            case Protocol.QUEUED: {
                player.receiveMessage("["+Protocol.QUEUED+ "]"+" you are currently queued \nif you want to leave the queue, send: QUEUE");
                break;
            }
            case Protocol.GAMESTARTED: {
                if (player.game == null) {
                    if (protocol.length < 3) {
                        sendError("could not handle GAMESTARTED, too little inputs received");
                    } else {
                        player.receiveMessage(input);
                        player.game = new Game(player, new OnlinePlayer());
                    }
                } else {
                    sendError("could not handle START GAME, player is already in a game");
                }
            }

            // MOVE: received move, convert to locationArray and send to player
            case Protocol.MOVE: {
                    if (protocol.length < 3) {
                        sendError("could not handle MOVE, too little inputs received");
                    } else {
                        player.game.doMove(getLocationArray(protocol[1], player.game.board),
                                           getColor(protocol[2]));
                    }
                break;
            }

            case Protocol.ACCEPTED: {
                if (protocol.length < 2) {
                    sendError("did not receive username");
                } else {
                    if (Objects.equals(protocol[1], player.getUsername())) {
                        player.receiveMessage("username " +protocol[1]+ " is accepted \nif you want to queue send: QUEUE ");
                    } else {
                        sendError("accepted username does not match");
                    }
                }
                break;
            }

            case Protocol.REJECTED: {
                if (protocol.length < 2) {
                    sendError("did not receive username");
                } else {
                    if (Objects.equals(protocol[1], player.getUsername())) {
                        player.receiveMessage("username " + protocol[1] + " is rejected\nsend new username using: LOGIN\"-<username>");
                        player.sendUsername();
                    } else {
                        sendError("rejected username does not match");
                    }
                }
                break;
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

            case Protocol.PRINT: {
                player.receiveMessage(player.game.board.toString());
                break;
            }

            default: {
                sendError("could not handle the input using the set protocol");
                player.receiveMessage(Protocol.ERROR + Protocol.SEPARATOR + "received unhandled message, server is notified");
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
