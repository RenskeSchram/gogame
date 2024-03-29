package gogame;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SocketConnectionTest {

  private SocketConnection socketConnetion;

  public int getRandomPort() {
    Random random = new Random();
    return random.nextInt(9999 - 1) + 1;
  }

  @BeforeEach
  public void setup() throws IOException {
    int PORT = getRandomPort();
    new GameServer(PORT);
    socketConnetion = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
  }

  @Test
  public void testGetLocationArray() {
    // Test single number location
    assertArrayEquals(socketConnetion.getLocationArray("9", 9), new int[]{0, 1});
    assertArrayEquals(socketConnetion.getLocationArray("10", 9), new int[]{1, 1});
    assertArrayEquals(socketConnetion.getLocationArray("0", 9), new int[]{0, 0});
    assertArrayEquals(socketConnetion.getLocationArray("80", 9), new int[]{8, 8});

    // Test coordinate location
    assertArrayEquals(socketConnetion.getLocationArray("5,0", 9), new int[]{5, 0});
    assertArrayEquals(socketConnetion.getLocationArray("2,5", 9), new int[]{2, 5});

    //Test incorrect location input
    assertArrayEquals(socketConnetion.getLocationArray("9,0 ", 9), new int[]{-1, -1});
    assertArrayEquals(socketConnetion.getLocationArray("move", 9), new int[]{-1, -1});
    assertArrayEquals(socketConnetion.getLocationArray("23,2s", 9), new int[]{-1, -1});
    assertArrayEquals(socketConnetion.getLocationArray("23,2,4", 9), new int[]{-1, -1});
  }

  @Test
  public void testGetColor() {
    // Test different ways of writing
    assertEquals(socketConnetion.getColor("white"), Color.WHITE);
    assertEquals(socketConnetion.getColor("BLACK"), Color.BLACK);
    assertEquals(socketConnetion.getColor("White"), Color.WHITE);
    assertEquals(socketConnetion.getColor("BlaCK"), Color.BLACK);

    // Test incorrect ways of writing
    assertNull(socketConnetion.getColor("black "));
    assertNull(socketConnetion.getColor("231!@"));

  }


}
