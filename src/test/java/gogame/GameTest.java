package gogame;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import gogame.server.ServerPlayer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

  private Game game;

  public int getRandomPort() {
    Random random = new Random();
    return random.nextInt(9999 - 1) + 1;
  }

  @BeforeEach
  public void setUp() throws IOException {
    int PORT = getRandomPort();
    GameServer gameServer = new GameServer(PORT);
    ServerPlayer playerI = new ServerPlayer();
    playerI.serverConnection = new ServerConnection(
        new Socket(InetAddress.getByName("localhost"), PORT));
    playerI.serverConnection.gameServer = gameServer;
    ServerPlayer playerII = new ServerPlayer();
    playerII.serverConnection = new ServerConnection(
        new Socket(InetAddress.getByName("localhost"), PORT));
    playerII.serverConnection.gameServer = gameServer;

    int DIM = 9;
    game = new Game(playerI, playerII, DIM);

  }

  @AfterEach
  public void reset() {
    System.setOut(System.out);
  }

  @Test
  public void testIsValidMove() {
    //correct
    assertTrue(game.isValidMove(new int[]{1, 6}, Color.BLACK));
    game.doMove(new int[]{1, 6}, Color.BLACK);
    // invalid turn
    assertFalse(game.isValidMove(new int[]{1, 7}, Color.BLACK));
    // invalid move
    assertFalse(game.isValidMove(new int[]{1, 6}, Color.WHITE));
    // correct move
    assertTrue(game.isValidMove(new int[]{2, 6}, Color.WHITE));
  }

  @Test
  public void testDoMove() {
    // Correct player
    assertSame(game.board.getStone(new int[]{6, 0}), Color.EMPTY);
    game.doMove(new int[]{6, 0}, Color.BLACK);
    assertSame(game.board.getStone(new int[]{6, 0}), Color.BLACK);

    //Incorrect player
    game.doMove(new int[]{7, 0}, Color.BLACK);
    assertSame(game.board.getStone(new int[]{7, 0}), Color.EMPTY);
    game.doMove(new int[]{7, 0}, Color.WHITE);
    assertSame(game.board.getStone(new int[]{7, 0}), Color.WHITE);

    //game inactive
    game.setActive(false);
    game.doMove(new int[]{8, 0}, Color.BLACK);
    assertSame(game.board.getStone(new int[]{8, 0}), Color.EMPTY);
    game.setActive(true);
    game.doMove(new int[]{8, 0}, Color.BLACK);
    assertSame(game.board.getStone(new int[]{8, 0}), Color.BLACK);

  }

  @Test
  public void testMultipleOwnSuicide() {
    game.board.setStone(new int[]{2, 8}, Color.WHITE);
    game.board.setStone(new int[]{1, 7}, Color.WHITE);
    game.board.setStone(new int[]{3, 7}, Color.WHITE);
    game.board.setStone(new int[]{1, 6}, Color.WHITE);
    game.board.setStone(new int[]{3, 6}, Color.WHITE);
    game.board.setStone(new int[]{2, 5}, Color.WHITE);
    game.board.setStone(new int[]{2, 7}, Color.BLACK);

    System.out.println(game.board.toString());
    assertSame(Color.EMPTY, game.board.getStone(new int[]{2, 6}));
    game.doMove(new int[]{2, 6}, Color.BLACK);
    assertSame(Color.EMPTY, game.board.getStone(new int[]{2, 6}));

    System.out.println(game.board.toString());

  }

  @Test
  public void testSingleOwnSuicide() {
    game.board.setStone(new int[]{2, 8}, Color.WHITE);
    game.board.setStone(new int[]{1, 7}, Color.WHITE);
    game.board.setStone(new int[]{3, 7}, Color.WHITE);
    game.board.setStone(new int[]{2, 6}, Color.WHITE);
    System.out.println(game.board.toString());

    game.doMove(new int[]{2, 7}, Color.BLACK);
    assertSame(Color.EMPTY, game.board.getStone(new int[]{2, 7}));
    System.out.println(game.board.toString());

  }

  @Test
  public void testDoPass() {
    game.doPass(Color.BLACK);
    assertTrue(game.getActive());
    assertSame(game.getTurn().color, Color.WHITE);
    game.doPass(Color.WHITE);
    assertFalse(game.getActive());
  }

  @Test
  public void testDoResign() {
    assertTrue(game.getActive());
    game.doResign(Color.WHITE);
    assertFalse(game.getActive());

  }

  @Test
  public void testIsKoFight(){
    game.board.setStone(new int[]{2, 1}, Color.WHITE);
    game.board.setStone(new int[]{1, 2}, Color.WHITE);
    game.board.setStone(new int[]{2, 2}, Color.BLACK);
    game.board.setStone(new int[]{1, 3}, Color.BLACK);
    game.board.setStone(new int[]{3, 3}, Color.BLACK);
    System.out.println(game.board.toString());

    game.doMove(new int[]{3, 2}, Color.WHITE);
    game.doMove(new int[]{2, 4}, Color.BLACK);
    game.doMove(new int[]{2, 3}, Color.WHITE);
    System.out.println(game.board.toString());
    assertTrue(game.isKoFight(new int[]{2, 2}, Color.BLACK));
    game.doMove(new int[]{2, 2}, Color.BLACK);
    assertSame(game.getTurn().color, Color.BLACK);
    System.out.println(game.board.toString());
  }

  @Test
  public void testEnd() {
    assertTrue(game.getActive());
    game.end(Color.WHITE);
    assertFalse(game.getActive());
  }

}
