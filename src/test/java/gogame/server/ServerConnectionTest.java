package gogame.server;

import gogame.Protocol;
import gogame.player.OnlinePlayer;
import gogame.player.PlayerConnection;
import gogame.player.PlayerTUI;
import gogame.server.GameServer;
import gogame.server.ServerConnection;
import gogame.server.ServerPlayer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerConnectionTest {
    private ServerConnection serverConnection;
    private ServerPlayer testPlayer;
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

        testPlayer = new ServerPlayer();

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