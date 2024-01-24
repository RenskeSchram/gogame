package gogame;

import gogame.player.OnlinePlayer;
import gogame.player.PlayerConnection;
import gogame.player.PlayerTUI;
import gogame.server.GameServer;
import gogame.server.ServerConnection;
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

public class PlayerConnectionTest {
    private PlayerConnection playerConnection;
    private OnlinePlayer testPlayer;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    public int getRandomPort() {
        Random random = new Random();
        return random.nextInt(9999 - 1) + 1;
    }


    @BeforeEach
    public void setUp() throws IOException {
        int PORT = getRandomPort();
        new GameServer(PORT);

        playerConnection = new PlayerConnection(new Socket(InetAddress.getByName("localhost"), PORT), testPlayer);
        testPlayer = new OnlinePlayer();
        testPlayer.tui = new PlayerTUI();
        playerConnection.player = testPlayer;

        System.setOut(new PrintStream(outputStreamCaptor));


    }

    @AfterEach
    public void reset() {
        System.setOut(System.out);
    }

    @Test
    public void testHandleInputDefault() {
        playerConnection.handleInput("INCORRECT MESSAGE");
        assertEquals(outputStreamCaptor.toString().trim(), Protocol.ERROR + Protocol.SEPARATOR + "received unhandled message, server is notified");
    }
    
    @Test
    public void testHandleInputHello() {
        playerConnection.handleInput(Protocol.HELLO);

        // correct handling of possible added message to hello
        playerConnection.handleInput(Protocol.HELLO+Protocol.SEPARATOR+"MESSAGE");
        assertEquals("MESSAGE", outputStreamCaptor.toString().trim());

        outputStreamCaptor.reset();

        // only showing possible first added message
        playerConnection.handleInput(Protocol.HELLO+Protocol.SEPARATOR+"MESSAGE"+Protocol.SEPARATOR+"SECOND MESSAGE");
        assertEquals("MESSAGE", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testHandleInputQueued() {
        // test if (correct) queued message is invoked
        playerConnection.handleInput(Protocol.QUEUED);
        assertTrue(outputStreamCaptor.toString().contains("["+Protocol.QUEUED+ "]"));
    }

    @Test
    public void testHandleInputGameStarted() {
        // Test correct handling of GAME STARTED
        assertNull(playerConnection.player.game);
        playerConnection.handleInput(Protocol.GAMESTARTED + Protocol. SEPARATOR+"nameI"+ Protocol. SEPARATOR+"nameII");
        assertEquals(Protocol.GAMESTARTED + Protocol. SEPARATOR+"nameI"+ Protocol. SEPARATOR+"nameII", outputStreamCaptor.toString().trim());
        assertNotNull(playerConnection.player.game);

        // Test incorrect GAME STARTED inputs are not handled
        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.GAMESTARTED);
        assertEquals("", outputStreamCaptor.toString().trim());

        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.GAMESTARTED + Protocol. SEPARATOR+"name, name");
        assertEquals("", outputStreamCaptor.toString().trim());

        // Test cannot start second game for this player, second call will not start new game and game stays the same
        outputStreamCaptor.reset();
        playerConnection.player.game = null;
        playerConnection.handleInput(Protocol.GAMESTARTED + Protocol. SEPARATOR+"nameI"+ Protocol. SEPARATOR+"nameII");
        Game oldGame = playerConnection.player.game;
        assertEquals(Protocol.GAMESTARTED + Protocol. SEPARATOR+"nameI"+ Protocol. SEPARATOR+"nameII", outputStreamCaptor.toString().trim());

        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.GAMESTARTED + Protocol. SEPARATOR+"nameI"+ Protocol. SEPARATOR+"nameII");
        assertEquals("", outputStreamCaptor.toString().trim());
        assertEquals(oldGame, playerConnection.player.game);
    }
    @Test
    public void testHandleInputAccepted() {
        // player does not have username jet
        playerConnection.handleInput(Protocol.ACCEPTED + Protocol.SEPARATOR + "username");
        assertEquals("", outputStreamCaptor.toString().trim());

        // correct situation
        outputStreamCaptor.reset();
        playerConnection.player.setUsername("username");
        playerConnection.handleInput(Protocol.ACCEPTED + Protocol.SEPARATOR + "username");
        assertTrue(outputStreamCaptor.toString().trim().contains("accepted"));

        // no username added to input
        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.ACCEPTED);
        assertEquals("", outputStreamCaptor.toString().trim());

        // incorrect username received
        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.ACCEPTED + Protocol.SEPARATOR + "wrongusername");
        assertEquals("", outputStreamCaptor.toString().trim());
    }
@Test
    public void testHandleInputRejected() {
        // player does not have username jet
        playerConnection.handleInput(Protocol.REJECTED + Protocol.SEPARATOR + "username");
        assertEquals("", outputStreamCaptor.toString().trim());

        // correct situation
        outputStreamCaptor.reset();
        playerConnection.player.setUsername("username");
        playerConnection.handleInput(Protocol.REJECTED + Protocol.SEPARATOR + "username");
        assertTrue(outputStreamCaptor.toString().trim().contains("rejected"));

        // no username added to input
        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.REJECTED);
        assertEquals("", outputStreamCaptor.toString().trim());

        // incorrect username received
        outputStreamCaptor.reset();
        playerConnection.handleInput(Protocol.REJECTED + Protocol.SEPARATOR + "wrongusername");
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    public void testHandleInputGameOver() {
        // TODO: add possibility to reconnect
    }

}
