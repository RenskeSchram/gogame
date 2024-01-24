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
        ServerPlayer playerII = new ServerPlayer();
        playerII.serverConnection = new ServerConnection(new Socket(InetAddress.getByName("localhost"), PORT));

        game = new Game(playerI, playerII, DIM);
    }

    @Test
    public void testGetCoordinate() {
        assertArrayEquals(game.getCoordinate(game.board.DIM+2), new int[]{1, 2});
    }

    @Test
    public void testIsValid() {
        assertTrue(game.isValid(new int[]{1,6}, Color.BLACK));
        game.doMove(new int[]{1,6}, Color.BLACK);
        assertFalse(game.isValid(new int[]{1,6}, Color.BLACK));
        assertFalse(game.isValid(new int[]{1,6}, Color.WHITE));
        assertTrue(game.isValid(new int[]{2,6}, Color.WHITE));
    }

    @Test
    public void testDoMove() {
        // Correct player
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.EMPTY);
        game.doMove(game.getCoordinate(6), Color.BLACK);
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.BLACK);

        // Incorrect player


        // Ko-rule
        game.board.setField(game.board.DIM - 2, game.board.DIM - 1, Color.WHITE);
        game.board.setField(game.board.DIM - 1, game.board.DIM - 2, Color.WHITE);
        game.board.setField(game.board.DIM - 2, game.board.DIM - 2, Color.BLACK);
        game.board.setField(game.board.DIM - 1, game.board.DIM - 3, Color.BLACK);
        game.board.setField(game.board.DIM - 3, game.board.DIM - 3, Color.BLACK);
        System.out.println(game.board.toString());

        game.doMove(new int[]{game.board.DIM-3, game.board.DIM-2} , Color.WHITE);
        game.doMove(new int[]{game.board.DIM-2, game.board.DIM-4} , Color.BLACK);
        game.doMove(new int[]{game.board.DIM-2, game.board.DIM-3} , Color.WHITE);
        System.out.println(game.board.toString());
        assertTrue(game.isKoFight(new int[]{game.board.DIM-2, game.board.DIM-2} , Color.BLACK));
        game.doMove(new int[]{game.board.DIM-2, game.board.DIM-2}, Color.BLACK);
        assertSame(game.getTurn().color, Color.BLACK);
        System.out.println(game.board.toString());
    }

    @Test
    public void testMultipleOwnSuicide() {
        game.board.setField(game.board.DIM - 2, game.board.DIM - 9, Color.WHITE);
        game.board.setField(game.board.DIM - 1, game.board.DIM - 8, Color.WHITE);
        game.board.setField(game.board.DIM - 3, game.board.DIM - 8, Color.WHITE);
        game.board.setField(game.board.DIM - 1, game.board.DIM - 7, Color.WHITE);
        game.board.setField(game.board.DIM - 3, game.board.DIM - 7, Color.WHITE);
        game.board.setField(game.board.DIM - 2, game.board.DIM - 6, Color.WHITE);
        game.board.setField(game.board.DIM - 2, game.board.DIM - 8, Color.BLACK);
        System.out.println(game.board.toString());

        game.doMove(new int[]{7,2}, Color.BLACK);
        System.out.println(game.board.toString());

    }

    @Test
    public void testSingleOwnSuicide() {
        game.board.setField(game.board.DIM - 2, game.board.DIM - 9, Color.WHITE);
        game.board.setField(game.board.DIM - 1, game.board.DIM - 8, Color.WHITE);
        game.board.setField(game.board.DIM - 3, game.board.DIM - 8, Color.WHITE);
        game.board.setField(game.board.DIM - 2, game.board.DIM - 7, Color.WHITE);
        System.out.println(game.board.toString());

        game.doMove(new int[]{8,2}, Color.BLACK);
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

}
