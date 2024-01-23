package gogame.server;

import gogame.Player;
import gogame.SocketConnection;

/**
 * Server implementation of player in a game.
 */
public class ServerPlayer extends Player {
    public ServerConnection serverConnection;

    @Override
    public SocketConnection getConnection() {
        return serverConnection;
    }

    @Override
    public void passGameUpdate(String gameUpdate) {
        serverConnection.sendOutput(gameUpdate);
    }
}
