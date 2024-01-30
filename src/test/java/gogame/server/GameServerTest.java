package gogame.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServerTest {

  private GameServer gameServer;
  private int PORT;
  ServerPlayer player1;
  ServerPlayer player2;

  @BeforeEach
  protected void setUp() {
    PORT = 8080;
    try {
      gameServer = new GameServer(PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }

    player1 = new ServerPlayer();
    player2 = new ServerPlayer();
  }

  @AfterEach
  protected void tearDown() {
    gameServer.close();
  }
  @Test
  protected void testQueueServerPlayer() {
    assertEquals(0, gameServer.queue.size());
    player1.setUsername("player");
    player2.setUsername("player2");
    gameServer.queueServerPlayer(player1);
    gameServer.queueServerPlayer(player2);
    assertEquals(2, gameServer.queue.size());
  }

  @Test
  protected void testCheckQueue() {

    gameServer.queueServerPlayer(player1);
    assertEquals(gameServer.queue.size(), 1);

    gameServer.queueServerPlayer(player2);
    try {
      gameServer.checkQueue();
    } catch( NullPointerException e) {
      // message send to players that game is started, however testPlayers don't have playerconnections.
    } finally {
      assertEquals(gameServer.queue.size(), 0);
    }
  }


}
