package gogame;

import gogame.server.ServerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SocketConnectionTest {
    private SocketConnection socketConnetion;
    private Socket socket;
    private Board board;

    @BeforeEach
    public void setup() throws IOException {
        socketConnetion = new ServerConnection(new Socket(InetAddress.getByName("localhost"), 8080));
        board = new Board();
    }


    @Test
    public void testGetLocationArray(){
        System.out.println(Arrays.toString(socketConnetion.getLocationArray("5", board)));
        System.out.println(Arrays.toString(new int[]{4, 0}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("5", board), new int[]{4, 0}));

        System.out.println(Arrays.toString(socketConnetion.getLocationArray("34", board)));
        System.out.println(Arrays.toString(new int[]{6, 3}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("34", board), new int[]{6, 3}));

        System.out.println(Arrays.toString(socketConnetion.getLocationArray("5,0", board)));
        System.out.println(Arrays.toString(new int[]{5, 0}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("5,0", board), new int[]{5, 0}));

        System.out.println(Arrays.toString(socketConnetion.getLocationArray("2,5", board)));
        System.out.println(Arrays.toString(new int[]{2, 5}));
        assertTrue(Arrays.equals(socketConnetion.getLocationArray("2,5", board), new int[]{2, 5}));
    }
}
