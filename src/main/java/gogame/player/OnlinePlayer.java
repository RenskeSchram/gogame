package gogame.player;

import gogame.Player;
import gogame.SocketConnection;
import java.io.IOException;
import java.net.Socket;

public class OnlinePlayer extends Player {
    PlayerConnection playerConnection;
    PlayerTUI tui;
    Strategy strategy;

    @Override
    public SocketConnection getConnection() {
        return playerConnection;
    }

    @Override
    public void passGameUpdate(String gameUpdate) {
    }

    public void sendUsername() {
        strategy.getUsername();
    }

    public void setStrategy(String type) {
        if (type.equals("computer")){
            strategy = new ComputerStrategy(this);
        } else {
            strategy = new HumanStrategy(this);
        }
    }

    public void receiveMessage(String string) {
        tui.printMessage(string);
    }

    public void makeConnection(Socket socket) throws IOException {
        playerConnection = new PlayerConnection(socket, this);
    }
}
