package gogame.server;

import gogame.Color;
import gogame.Game;
import gogame.Player;
import gogame.SocketConnection;
import java.io.*;

/**
 * Server implementation of player in a game.
 */
public class ServerPlayer extends Player {
    public ServerConnection serverConnection;
    BufferedReader reader;
    PrintWriter writer;


    public ServerPlayer() {

    }

    @Override
    public SocketConnection getConnection() {
        return serverConnection;
    }

    @Override
    public void passGameUpdate(String gameUpdate) {
        serverConnection.sendOutput(gameUpdate);
    }

    @Override
    public void lookAtBoard() {
        if (writer != null) {
            writer.println(game.board.toString());
            writer.flush();
        }

    }

    public void quitGame() {
        super.quitGame();
        serverConnection.gameServer.quitGame(this);
    }
}
