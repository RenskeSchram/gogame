package gogame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gogame.player.OnlinePlayer;
import gogame.player.PlayerConnection;
import gogame.player.strategy.ComputerStrategy;
import gogame.server.GameServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComputerStrategyTest {

  OnlinePlayer player;


  public ComputerStrategyTest() throws IOException {
    player = new OnlinePlayer();
  }

  @BeforeEach
  public void setup() throws IOException {
    int PORT = TestFunctions.getRandomPort();
    new GameServer(PORT);

    player.strategy = new ComputerStrategy(player);
    player.playerConnection = new PlayerConnection(
        new Socket(InetAddress.getByName("localhost"), PORT), player);

  }

  @Test
  public void testGetUserName() {
    assertNull(player.getUsername());
    player.strategy.getUsername();
    assertEquals("computer", player.getUsername());
    player.strategy.getUsername();
    assertEquals("computerr", player.getUsername());
  }

  @Test
  public void testGetRandomValidMove() {
    player.game = new Game(new OnlinePlayer(), player, 9);
    assertTrue(player.strategy instanceof ComputerStrategy computerStrategy);
    ComputerStrategy computerStrategy = (ComputerStrategy) player.strategy;
    //assertTrue(Board.containsIntersection(player.game.getValidMoves(), computerStrategy.getRandodMove(computerStrategy.getValidMoves())));

  }
}
