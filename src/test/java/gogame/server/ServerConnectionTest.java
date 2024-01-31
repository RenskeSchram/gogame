package gogame.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gogame.Protocol;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerConnectionTest {

  private ServerConnection serverConnection;
  private GameServer gameServer;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  public int getRandomPort() {
    Random random = new Random();
    return random.nextInt(9999 - 1) + 1;
  }


  @BeforeEach
  public void setUp() throws IOException {
    int PORT = getRandomPort();
    this.gameServer = new GameServer(PORT);

    serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
    serverConnection.gameServer = gameServer;
    serverConnection.start();

    ServerPlayer testPlayer = new ServerPlayer();

    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void reset() {
    System.setOut(System.out);
  }

  @Test
  public void testHandleInputHello(){
    // test correct situation
    assertNull(serverConnection.serverPlayer);
    serverConnection.handleInput(Protocol.HELLO);
    assertNotNull(serverConnection.serverPlayer);
    assertNotNull(serverConnection.serverPlayer.serverConnection);

    // test second handshake, no changes happen
    ServerPlayer oldServerPlayer = serverConnection.serverPlayer;
    serverConnection.handleInput(Protocol.HELLO);
    assertEquals(oldServerPlayer, serverConnection.serverPlayer);
  }

  @Test
  public void testHandleInputLogin() {
    // no previous handshake
    serverConnection.handleInput(Protocol.LOGIN + Protocol.SEPARATOR + "name");
    assertNull(serverConnection.serverPlayer);

    serverConnection.handleInput(Protocol.HELLO);

    // test login without provided username
    assertNull(serverConnection.serverPlayer.getUsername());
    serverConnection.handleInput(Protocol.LOGIN);
    assertNull(serverConnection.serverPlayer.getUsername());

    // test login with correctly provided username
    serverConnection.handleInput(Protocol.LOGIN + Protocol.SEPARATOR + "name");
    assertEquals("name", serverConnection.serverPlayer.getUsername());

    // username already provided, no changes allowed
    serverConnection.handleInput(Protocol.LOGIN + Protocol.SEPARATOR + "newname");
    assertEquals("name", serverConnection.serverPlayer.getUsername());
  }


  @Test
  public void testHandleQueue() {
    // test not logged in jet
    serverConnection.handleInput(Protocol.QUEUE);

    // correct login
    serverConnection.handleInput(Protocol.HELLO);
    serverConnection.handleInput(Protocol.LOGIN+Protocol.SEPARATOR+"name");
    serverConnection.handleInput(Protocol.QUEUE);
  }

  @Test
  public void testHandleMovePassResign() {
    // test not logged in jet
    serverConnection.handleInput(Protocol.MOVE +Protocol.SEPARATOR+"5");
    serverConnection.handleInput(Protocol.PASS);
    serverConnection.handleInput(Protocol.RESIGN);


  }

}