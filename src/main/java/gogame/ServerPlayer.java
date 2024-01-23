package gogame;

import server.ServerConnection;
import server.SocketConnection;

/**
 * Server implementation of player in a game.
 */
public class ServerPlayer extends Player {
    public ServerConnection serverConnection;

    @Override
    public SocketConnection getConnection() {
        return serverConnection;
    }
}
