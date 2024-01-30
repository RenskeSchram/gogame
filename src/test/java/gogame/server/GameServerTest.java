package gogame.server;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServerTest {

  private GameServer gameServer;
  private int PORT;

  @BeforeEach
  void setUp() {
    PORT = 8080;
    try {
      gameServer = new GameServer(PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  void tearDown() {
    gameServer.close();
  }

  @Test
  void testPlayerQueueAndGameStart() {

  }

  //TODO: aanpassen voor checken hele server.
  @Test
  void testUsernameAvailability() {

  }

  @Test
  void testQuitGame() {

  }
}
