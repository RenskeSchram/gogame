package gogame;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import gogame.server.ServerPlayer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

  private Game game;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();


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

    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void reset() {
    System.setOut(System.out);
    //game.stopTimer();
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
    assertSame(game.getTurn().getColor(), Color.WHITE);
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
  public void testIsKoFight() {
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
    assertSame(game.getTurn().getColor(), Color.BLACK);
    System.out.println(game.board.toString());
  }

  @Test
  public void testEnd() {
    assertTrue(game.getActive());
    game.end(Color.WHITE);
    assertFalse(game.getActive());
  }
}

//  @Test
//  public void testStartTimer() {
//    game.useTimer = false;
//    game.startTimer();
//    Assertions.assertNull(game.getTimer());
//    Assertions.assertNull(game.getTimeOutPass());
//
//    game.useTimer = true;
//
//    game.startTimer();
//    Assertions.assertNotNull(game.getTimer());
//    Assertions.assertNotNull(game.getTimeOutPass());
//
//    try {
//      // check timing in MoveTimer, might have changed
//      Thread.sleep(1500);
//    } catch (InterruptedException e) {
//      System.err.println("sleep thread interrupted");
//    }
//    assertTrue(outputStreamCaptor.toString().trim().contains("automated pass"));
//  }
//

//  @Test
//  public void testStopTimer() {
//    game.startTimer();
//    Assertions.assertNotNull(game.getTimer());
//    Assertions.assertNotNull(game.getTimeOutPass());
//
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//
//    game.stopTimer();
//    Assertions.assertNull(game.getTimer());
//    Assertions.assertNull(game.getTimeOutPass());
//
//    try {
//      // check timing in Game, might have changed
//      Thread.sleep(1500);
//    } catch (InterruptedException e) {
//      System.err.println("sleep thread interrupted");
//    }
//    Assertions.assertNull(game.getTimer());
//    Assertions.assertNull(game.getTimeOutPass());
//  }
//
//  @Test
//  public void testResetTimer() {
//    game.startTimer();
//    Assertions.assertNotNull(game.getTimer());
//    Assertions.assertNotNull(game.getTimeOutPass());
//
//    try {
//      Thread.sleep(600);
//    } catch (InterruptedException e) {
//      System.err.println("sleep thread interrupted");
//    }
//
//    game.resetTimer();
//
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException e) {
//      System.err.println("sleep thread interrupted");
//    }
//
//    Assertions.assertFalse(outputStreamCaptor.toString().trim().contains("automated pass"));
//
//    try {
//      Thread.sleep(800);
//    } catch (InterruptedException e) {
//      System.err.println("sleep thread interrupted");
//    }
//    Assertions.assertTrue(outputStreamCaptor.toString().trim().contains("automated pass"));
//  }
//
//}
