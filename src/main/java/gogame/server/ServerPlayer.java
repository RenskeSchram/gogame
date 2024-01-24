package gogame.server;

import gogame.Color;
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
        try {
            writer = new PrintWriter(new FileWriter("log_" + this.toString() + ".txt", true));
            reader = new BufferedReader(new FileReader("log_" + this.toString() + ".txt"));
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }

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
}
