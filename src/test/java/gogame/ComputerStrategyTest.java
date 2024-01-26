package gogame;

import gogame.player.strategy.ComputerStrategy;
import gogame.player.OnlinePlayer;
import gogame.player.PlayerConnection;
import gogame.server.GameServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComputerStrategyTest {
    OnlinePlayer player = new OnlinePlayer();


    public ComputerStrategyTest() throws IOException { }

    @BeforeEach
    public void setup() throws IOException {
        int PORT = TestFunctions.getRandomPort();
        new GameServer(PORT);

        player.strategy = new ComputerStrategy(player);
        player.playerConnection = new PlayerConnection(new Socket(InetAddress.getByName("localhost"), PORT), player);

    }

    @Test
    public void testGetUserName(){
        assertNull(player.getUsername());
        player.strategy.getUsername();
        assertEquals("Computer", player.getUsername());
        player.strategy.getUsername();
        assertEquals("Computerr", player.getUsername());
    }
    @Test
    public void testGetRandomValidMove() {
        player.game = new Game(new OnlinePlayer(), player, 9);
        assertTrue(player.strategy instanceof ComputerStrategy computerStrategy);
        ComputerStrategy computerStrategy = (ComputerStrategy) player.strategy;
        assertTrue(Board.containsLocation(player.game.getValidMoves(), computerStrategy.getRandomValidMove()));

    }
}
