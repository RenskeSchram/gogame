package gogame;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import gogame.server.ServerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private GameServer gameServer;
    int DIM = 9;

    public int getRandomPort() {
        Random random = new Random();
        return random.nextInt(9999 - 1) + 1;
    }


    @BeforeEach
    public void setUp() throws IOException {
        int PORT = getRandomPort();
        this.gameServer = new GameServer(PORT);
        ServerPlayer playerI = new ServerPlayer();
        playerI.serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
        playerI.serverConnection.gameServer = gameServer;
        ServerPlayer playerII = new ServerPlayer();
        playerII.serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));
        playerII.serverConnection.gameServer = gameServer;

        game = new Game(playerI, playerII, DIM);
    }

    @Test
    public void testIsValid() {
        assertTrue(game.isValidMove(new int[]{1,6}, Color.BLACK));
        game.doMove(new int[]{1,6}, Color.BLACK);
        assertFalse(game.isValidMove(new int[]{1,6}, Color.BLACK));
        assertFalse(game.isValidMove(new int[]{1,6}, Color.WHITE));
        assertTrue(game.isValidMove(new int[]{2,6}, Color.WHITE));
    }

    @Test
    public void testDoMove() {
        // Correct player
        assertSame(game.board.getField(new int[]{6,0}), Color.EMPTY);
        game.doMove(new int[]{6,0}, Color.BLACK);
        assertSame(game.board.getField(new int[]{6,0}), Color.BLACK);

        // Incorrect player


        // Ko-rule
        game.board.setField(new int[]{2, 1}, Color.WHITE);
        game.board.setField(new int[]{1, 2}, Color.WHITE);
        game.board.setField(new int[]{2, 2}, Color.BLACK);
        game.board.setField(new int[]{1, 3}, Color.BLACK);
        game.board.setField(new int[]{3, 3}, Color.BLACK);
        System.out.println(game.board.toString());

        game.doMove(new int[]{3, 2} , Color.WHITE);
        game.doMove(new int[]{2, 4} , Color.BLACK);
        game.doMove(new int[]{2, 3} , Color.WHITE);
        System.out.println(game.board.toString());
        assertTrue(game.isKoFight(new int[]{2, 2} , Color.BLACK));
        game.doMove(new int[]{2, 2}, Color.BLACK);
        assertSame(game.getTurn().color, Color.BLACK);
        System.out.println(game.board.toString());
    }

    @Test
    public void testMultipleOwnSuicide() {
        game.board.setField(new int[]{2, 8}, Color.WHITE);
        game.board.setField(new int[]{1, 7}, Color.WHITE);
        game.board.setField(new int[]{3, 7}, Color.WHITE);
        game.board.setField(new int[]{1, 6}, Color.WHITE);
        game.board.setField(new int[]{3, 6}, Color.WHITE);
        game.board.setField(new int[]{2, 5}, Color.WHITE);
        game.board.setField(new int[]{2, 7}, Color.BLACK);

        System.out.println(game.board.toString());
        assertSame(Color.EMPTY, game.board.getField(new int[]{2, 6}));
        game.doMove(new int[]{2,6}, Color.BLACK);
        assertSame(Color.EMPTY, game.board.getField(new int[]{2, 6}));

        System.out.println(game.board.toString());

    }

    @Test
    public void testSingleOwnSuicide() {
        game.board.setField(new int[]{2, 8}, Color.WHITE);
        game.board.setField(new int[]{1, 7}, Color.WHITE);
        game.board.setField(new int[]{3, 7}, Color.WHITE);
        game.board.setField(new int[]{2, 6}, Color.WHITE);
        System.out.println(game.board.toString());

        game.doMove(new int[]{2,7}, Color.BLACK);
        assertSame(Color.EMPTY, game.board.getField(new int[]{2, 7}));
        System.out.println(game.board.toString());

    }

    @Test
    public void testDoPass() {
        game.doPass(Color.BLACK);
        assertTrue(game.active);
        assertSame(game.getTurn().color, Color.WHITE);
        game.doPass(Color.WHITE);
        assertFalse(game.active);
    }
    @Test
    public void testStartTimer() {
        assertNull(game.timer);
        game.doMove(new int[]{1,1}, Color.BLACK);
        assertNotNull(game.timer);
        assertSame(Color.WHITE, game.getTurn().getColor());
        try {
            // check timing in Game, might have changed
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertSame(Color.BLACK, game.getTurn().getColor());
    }
    @Test
    public void testStopTimer() {
        assertNull(game.timer);
        assertNull(game.timeOutPass);

        game.doMove(new int[]{1,1}, Color.BLACK);
        assertSame(Color.WHITE, game.getTurn().getColor());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        game.stopTimer();

        try {
            // check timing in Game, might have changed
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertSame(Color.WHITE, game.getTurn().getColor());

        assertNull(game.timer);
        assertNull(game.timeOutPass);

    }

}
