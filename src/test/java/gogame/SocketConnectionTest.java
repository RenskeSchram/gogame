package gogame;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SocketConnectionTest {
    private GameServer gameServer;
    private SocketConnection socketConnetion;
    private Board board;

    public int getRandomPort() {
        Random random = new Random();
        return random.nextInt(9999 - 1) + 1;
    }

    @BeforeEach
    public void setup() throws IOException {
        int PORT = getRandomPort();
        gameServer = new GameServer(PORT);
        socketConnetion = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
        board = new Board(9);
    }

    @Test
    public void testGetLocationArray(){
        // Test single number location
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("9", board), new int[]{8, 0}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("25", board), new int[]{6, 2}));

        // Test coordinate location
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("5,0", board), new int[]{5, 0}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("2,5", board), new int[]{2, 5}));

        //Test incorrect location input
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("9,0 ", board), new int[]{-1, -1}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("move", board), new int[]{-1, -1}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("23,2s", board), new int[]{-1, -1}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("23,2,4", board), new int[]{-1, -1}));
    }

    @Test
    public void testGetColor(){
        // Test different ways of writing
        assertEquals(socketConnetion.getColor("white"), Color.WHITE);
        assertEquals(socketConnetion.getColor("BLACK"), Color.BLACK);
        assertEquals(socketConnetion.getColor("White"), Color.WHITE);
        assertEquals(socketConnetion.getColor("BlaCK"), Color.BLACK);

        // Test incorrect ways of writing
        assertEquals(socketConnetion.getColor("black "), null);
        assertEquals(socketConnetion.getColor("231!@"), null);

    }



}
