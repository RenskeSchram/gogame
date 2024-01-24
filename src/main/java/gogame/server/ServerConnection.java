package gogame.server;

import gogame.Color;
import gogame.Protocol;
import gogame.SocketConnection;
import java.io.IOException;
import java.net.Socket;

/**
 * ServerConnection to handle receiving and sending messages according to protocol.
 */
public class ServerConnection extends SocketConnection {
    public ServerPlayer serverPlayer;
    public GameServer gameServer;

    public ServerConnection(Socket socket) throws IOException {
        super(socket);
    }

    /**
     * Deciphers the received input message using the protocol messages.
     *
     * @param input line of input to be handled
     */
    @Override
    public void handleInput(String input) {
        String[] protocol = input.split(Protocol.SEPARATOR);
        switch (protocol[0]) {

            // HELLO handshake: ask to receive username using LOGIN
            case Protocol.HELLO: {
                if (serverPlayer == null) {
                    serverPlayer = new ServerPlayer();
                    serverPlayer.serverConnection = this;
                    sendOutput(Protocol.HELLO + Protocol.SEPARATOR + "please provide a username using: LOGIN<username>");
                } else {
                    sendError("Hello to you too! We have exchanged HELLOs before right?");
                }
                break;
            }

            // LOGIN: username received. If correct (not existing) send accept and set username for ServerPlayer
            case Protocol.LOGIN: {
                if (serverPlayer != null && protocol.length >= 2) {
                    if (gameServer.correctUsername(protocol[1]) && serverPlayer.getUsername() == null) {
                        serverPlayer.setUsername(protocol[1]);
                    }
                    sendAccepted(gameServer.correctUsername(protocol[1])&& serverPlayer.getUsername() != null);
                } else {
                    sendError("could not handle LOGIN, no username provided");
                }
                break;
            }

            // QUEUE: send queue protocol message to serverPlayer
            case Protocol.QUEUE: {
                if (serverPlayer != null && serverPlayer.getUsername() != null) {
                    gameServer.queueServerPlayer(serverPlayer);
                    sendQueued();
                } else {
                    sendError("correct LOGIN required to queue");
                }
                break;
            }

            // MOVE: if player is in a game, send move to serverPlayer
            case Protocol.MOVE: {
                if (protocol.length >= 2 && gameServer.serverMap.containsKey(serverPlayer)) {
                        serverPlayer.doMove(getLocationArray(protocol[1], serverPlayer.game.board), Color.EMPTY);
                    } else {
                    sendError("could not handle MOVE");
                }
                break;
                }


            // PASS: if player is in a game, send pass to serverPlayer
            case Protocol.PASS: {
                if (gameServer.serverMap.containsKey(serverPlayer)) {
                    serverPlayer.doPass();
                } else {
                    sendError("could not handle PASS");
                }
                break;
            }

            // RESIGN: if player is in a game, send resign to serverPlayer
            case Protocol.RESIGN: {
                if (gameServer.serverMap.containsKey(serverPlayer)) {
                    serverPlayer.doResign();
                } else {
                    sendError("could not handle RESIGN");
                }
                break;
            }


            // RESIGN: if player is in a game, send resign to serverPlayer
            case Protocol.PRINT: {
                System.out.println(serverPlayer.game.board.toString());
                sendOutput(Protocol.PRINT);
                break;
            }
        }
    }


    public void sendQueued() {
        sendOutput(Protocol.QUEUED);
    }

    public void sendAccepted(boolean accepted) {
        if (accepted) {
            sendOutput(Protocol.ACCEPTED);
        } else {
            sendOutput(Protocol.REJECTED);
        }
    }

    public void sendError(String message) {
        sendOutput(Protocol.ERROR + Protocol.SEPARATOR + message);
    }

    /**
     * Handles a disconnection of the connection.
     */
    @Override
    protected void handleDisconnect() {
    }

    public void sendPass() { }
}
