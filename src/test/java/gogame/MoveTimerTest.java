package gogame;

import static gogame.TestFunctions.getRandomPort;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gogame.server.GameServer;
import gogame.server.ServerConnection;
import gogame.server.ServerPlayer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveTimerTest {

  private Game game;
  private MoveTimer moveTimer;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

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
    moveTimer = new MoveTimer(game);

    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void reset() {
    System.setOut(System.out);
  }

  @Test
  public void testStartTimer() {
    moveTimer.useTimer = false;
    moveTimer.startTimer();
    Assertions.assertNull(moveTimer.getTimer());
    Assertions.assertNull(moveTimer.getTimeOutPass());

    moveTimer.useTimer = true;

    moveTimer.startTimer();
    Assertions.assertNotNull(moveTimer.getTimer());
    Assertions.assertNotNull(moveTimer.getTimeOutPass());

    try {
      // check timing in MoveTimer, might have changed
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(outputStreamCaptor.toString().trim().contains("automated pass"));
  }

  @Test
  public void testStopTimer() {
    moveTimer.startTimer();
    Assertions.assertNotNull(moveTimer.getTimer());
    Assertions.assertNotNull(moveTimer.getTimeOutPass());

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    moveTimer.stopTimer();
    Assertions.assertNull(moveTimer.getTimer());
    Assertions.assertNull(moveTimer.getTimeOutPass());

    try {
      // check timing in Game, might have changed
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Assertions.assertNull(moveTimer.getTimer());
    Assertions.assertNull(moveTimer.getTimeOutPass());

  }

  @Test
  public void testResetTimer() {
    Assertions.assertNull(moveTimer.getTimer());
    Assertions.assertNull(moveTimer.getTimeOutPass());
    moveTimer.startTimer();
    Assertions.assertNotNull(moveTimer.getTimer());
    Assertions.assertNotNull(moveTimer.getTimeOutPass());

    try {
      Thread.sleep(900);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    outputStreamCaptor.reset();
    moveTimer.resetTimer();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Assertions.assertFalse(outputStreamCaptor.toString().trim().contains("automated pass"));
    moveTimer.resetTimer();
    try {
      Thread.sleep(600);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue(outputStreamCaptor.toString().trim().contains("automated pass"));


  }

}
