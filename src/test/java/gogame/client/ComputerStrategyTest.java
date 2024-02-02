package gogame.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gogame.client.strategy.ComputerStrategy;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComputerStrategyTest {
  ComputerStrategy computerStrategy;
  StrategyGame strategyGame;

  @BeforeEach
  protected void setUp() {
    computerStrategy = new ComputerStrategy(new ClientPlayer());
    strategyGame = new StrategyGame(new ClientPlayer(), new ClientPlayer(), 9);

  }

  @Test
  protected void testIsEqualNumOfBlackAndWhiteStones() {
    int[] oldStones = new int[]{2,4};
    int[] newStones = new int[]{2,4};
    assertTrue(Arrays.equals(oldStones, newStones));

    oldStones = new int[]{3,4};
    assertFalse(Arrays.equals(oldStones, newStones));
  }


}
