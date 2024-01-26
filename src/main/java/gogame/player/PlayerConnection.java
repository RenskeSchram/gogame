package gogame.player;

import gogame.Protocol;
import gogame.SocketConnection;
import java.io.IOException;
import java.net.Socket;

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
                    //player.receiveMessage(protocol[1]);
                    player.receiveMessage(printProtocolMessage(protocol));
                }
                player.sendUsername();
                break;
            }

            case Protocol.ACCEPTED: {
                if (protocol.length < 2) {
                    sendError("did not receive username");
                } else {
                    String acceptedMessage = "as your username if you want to queue send: QUEUE";
                    player.receiveMessage(printProtocolMessage(protocol) + acceptedMessage);
                    player.sendQueue();
                }
                break;
            }

            case Protocol.REJECTED: {
                if (protocol.length < 2) {
                    sendError("did not receive username");
                } else {
                    String rejectedMessage = "as your username\nsend a new username using: LOGIN\"-<username>";
                    player.receiveMessage(printProtocolMessage(protocol) + rejectedMessage);
                    player.sendUsername();
                }
                break;
            }

            // QUEUE: send queue protocol message
            case Protocol.QUEUED: {
                String queuedMessage = "you are currently queued \nif you want to leave the queue, send: QUEUE";
                player.receiveMessage(printProtocolMessage(protocol) + queuedMessage);
                break;
            }
            case Protocol.GAMESTARTED: {
                if (player.game == null) {
                    if (protocol.length < 3) {
                        sendError("could not handle GAMESTARTED, too little inputs received");
                    } else {
                        player.receiveMessage(printProtocolMessage(protocol));
                        // check gamesetup
                        int DIM = Integer.parseInt(protocol[2]);
                        if (protocol[1].split(",")[1].equals(player.getUsername())) {
                            player.game = new StrategyGame(player, new OnlinePlayer(), DIM);
                        } else {
                            player.game = new StrategyGame(new OnlinePlayer(), player, DIM);
                        }
                    }
                } else {
                    sendError("could not handle START GAME, player is already in a game");
                }
                break;
            }

            // MOVE: received move, convert to locationArray and send to player
            case Protocol.MOVE: {
                if (protocol.length < 3) {
                    sendError("could not handle MOVE, too little inputs received");
                } else {
                    player.doMove(getLocationArray(protocol[1], player.game.board),
                                  getColor(protocol[2]));
                }
                break;
            }


            // MOVE: received move, convert to locationArray and send to player
            case Protocol.PASS: {
                if (protocol.length < 2) {
                    sendError("could not handle PASS, no color received");
                } else {
                    player.doPass(getColor(protocol[1]));
                }
                break;
            }

            case Protocol.MAKEMOVE: {
                player.strategy.determineMove();
                break;
            }

            case Protocol.GAMEOVER: {
                player.receiveMessage(printProtocolMessage(protocol));
                // TODO: handleGameOver: ask to reconnect to server?
                break;
            }

            case Protocol.ERROR: {
                player.receiveMessage(printProtocolMessage(protocol));
                break;
            }

            case Protocol.PRINT: {
                player.receiveMessage(player.game.board.toString());
                break;
            }

            default: {
                sendError("could not handle the input using the set protocol");
                String defaultMessage = " received unhandled message, server is notified";
                player.receiveMessage(Protocol.ERROR + defaultMessage);
            }
        }
    }

    public String printProtocolMessage(String[] protocol) {
        StringBuilder message = new StringBuilder();
        for (String protocolSnippet : protocol) {
            message.append(protocolSnippet).append(" ");
        }
        return message.toString();
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
