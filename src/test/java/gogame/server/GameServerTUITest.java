package gogame.server;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServerTUITest {

  private GameServerTUI gameServerTUI;

  @BeforeEach
  protected void setUp() {
    gameServerTUI = new GameServerTUI();
  }

  @AfterEach
  public void reset() {
    System.setIn(System.in);
  }


  @Test
  public void testCorrectPORT() throws IOException {

    // correctly start and exit tui
    String input = "1234\nexit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    try {
      gameServerTUI.runTUI();
    } finally {
      System.setIn(System.in);
    }
    assertFalse(gameServerTUI.runTui);
  }

  @Test
  public void testWrongPORT() throws IOException {
    String input = "wrong\n9090\nexit";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    try {
      gameServerTUI.runTUI();
    } finally {
      System.setIn(System.in);
    }

    assertFalse(gameServerTUI.runTui);
  }

}




