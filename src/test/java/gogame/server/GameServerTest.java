package gogame.server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServerTest {

  private GameServer gameServer;
  private int PORT;
  ServerPlayer player1;
  ServerPlayer player2;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  protected void setUp() throws IOException {
    PORT = 8080;
    try {
      gameServer = new GameServer(PORT);
    } catch (IOException e) {
      System.err.println("Failed to open new GameServer");
    }

    player1 = new ServerPlayer();
    player1.serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
    player2 = new ServerPlayer();
    player2.serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
    player1.setUsername("one");
    player2.setUsername("two");
  }

  @AfterEach
  public void reset() {
    gameServer.close();
    System.setOut(System.out);
  }

  @Test
  public void testStopServer() {
    gameServer.loginPlayer(player1);
    gameServer.loginPlayer(player2);

    gameServer.startGame(player1, player2);
    gameServer.stopServer();
    assertTrue(gameServer.serverMap.isEmpty());

    gameServer.loginPlayer(player1);
  }

  @Test
  public void testLoginPlayer() {
    assertFalse(gameServer.serverMap.containsKey(player1));
    gameServer.loginPlayer(player1);
    assertTrue(gameServer.serverMap.containsKey(player1));
    gameServer.loginPlayer(player1);
  }


  @Test
  protected void testQueueServerPlayer() {
    assertEquals(0, gameServer.getQueue().size());
    gameServer.queueServerPlayer(player1);
    gameServer.queueServerPlayer(player2);
    assertEquals(2, gameServer.getQueue().size());
  }

  @Test
  public void testUsernameAvailable() {
    player1.setUsername("username");
    gameServer.loginPlayer(player1);
    assertFalse(gameServer.usernameAvailable(player1.getUsername()));
    assertTrue(gameServer.usernameAvailable("NewUsername"));
  }
  @Test
  public void testCheckQueue() {
    gameServer.loginPlayer(player1);
    gameServer.loginPlayer(player2);

    gameServer.queueServerPlayer(player1);
    gameServer.queueServerPlayer(player2);
    gameServer.checkQueue();
    assertEquals(0, gameServer.getQueue().size());
  }

  @Test
  public void testStartGame() {
    gameServer.loginPlayer(player1);
    gameServer.loginPlayer(player2);

    gameServer.startGame(player1, player2);
    assertNotNull(gameServer.serverMap.get(player1));
    assertNotNull(gameServer.serverMap.get(player1));
    assertEquals(1, gameServer.getGameCodeCounter());
  }

  @Test
  public void testHandleDisconnect() {
    gameServer.loginPlayer(player1);
    gameServer.loginPlayer(player2);

    gameServer.startGame(player1, player2);
    assertNotNull(gameServer.serverMap.get(player1));
    gameServer.handleDisconnect(player1);
    assertFalse(gameServer.serverMap.containsKey(player1));
    assertNull(gameServer.serverMap.get(player2));
  }
}
