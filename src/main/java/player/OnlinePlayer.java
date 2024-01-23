package player;

import gogame.Player;
import server.SocketConnection;

public class OnlinePlayer extends Player {
    PlayerConnection playerConnection;

    @Override
    public SocketConnection getConnection() {
        return playerConnection;
    }
}
