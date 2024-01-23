package server;

import gogame.Color;
import gogame.Protocol;
import gogame.ServerPlayer;
import java.io.IOException;
import java.net.Socket;

/**
 * ServerConnection to handle receiving and sending messages according to protocol.
 */
public class ServerConnection extends SocketConnection{
    ServerPlayer serverPlayer;
    GameServer gameServer;

    public ServerConnection(Socket socket) throws IOException {
        super(socket);
    }

    /**
     * Deciphers the received input message using the protocol messages.
     *
     * @param input line of input to be handled
     */
    @Override
    protected void handleInput(String input) {
        String[] protocol = input.split(Protocol.SEPARATOR);
        switch (protocol[0]) {

            // HELLO handshake: ask to receive username using LOGIN
            case Protocol.HELLO: {
                sendOutput(Protocol.HELLO + Protocol.SEPARATOR + "please provide username using: LOGIN~<username>");
            }

            // LOGIN: username received. If correct (not existing) send accept and set username for ServerPlayer
            case Protocol.LOGIN: {
                if (protocol.length >= 2) {
                    //TODO: check username in serverMap
                    serverPlayer.setUsername(protocol[1]);
                    //TODO: answer with acceptation according to protocol
                }
            }

            // QUEUE: send queue protocol message to serverPlayer
            case Protocol.QUEUE: {
                //TODO: check if logged in
                gameServer.queueServerPlayer(serverPlayer);
            }

            // MOVE: if player is in a game, send move to serverPlayer
            case Protocol.MOVE: {
                if (protocol.length == 2 && gameServer.serverMap.containsKey(serverPlayer)) {
                    try {
                        int location = Integer.parseInt(protocol[1]);
                        serverPlayer.doMove(location);
                    } catch (NumberFormatException ex){
                        ex.printStackTrace();
                    }
                }
            }

            // PASS: if player is in a game, send pass to serverPlayer
            case Protocol.PASS: {
                if (gameServer.serverMap.containsKey(serverPlayer)) {
                    serverPlayer.doPass();
                }
            }

            // RESIGN: if player is in a game, send resign to serverPlayer
            case Protocol.RESIGN: {
                //TODO: handle resign
            }
        }
    }



    public void sendGameStarted(String nameBlackPlayer, String nameWhitePlayer) {
        sendOutput(Protocol.GAMESTARTED + Protocol.SEPARATOR + nameBlackPlayer + Protocol.SEPARATOR + nameWhitePlayer);
    }

    public void sendGameOver(String name) {
        if (!name.equalsIgnoreCase("draw")) {
            sendOutput(Protocol.GAMEOVER + Protocol.SEPARATOR + "WINNER " + name);

        } else {
            sendOutput(Protocol.GAMEOVER + Protocol.SEPARATOR + "DRAW");
        }

    }

    public void sendMakeMove(String name) {
        sendOutput(Protocol.MAKEMOVE + Protocol.SEPARATOR + name);
    }


    public void sendMove(int locationIndex, Color color) {
        sendOutput(Protocol.MOVE + Protocol.SEPARATOR + locationIndex + Protocol.SEPARATOR + color);
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
