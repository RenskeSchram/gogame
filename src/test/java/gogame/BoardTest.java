package gogame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

  private Board board;

  @BeforeEach
  public void setUp() {
    board = new Board(9);
  }

  @Test
  public void testIsField() {
    assertFalse(board.isField(new int[]{-1, 1}));
    assertTrue(board.isField(new int[]{0, 0}));
    assertTrue(board.isField(new int[]{1, 1}));
    assertFalse(board.isField(new int[]{board.DIM, board.DIM}));
  }

  @Test
  public void testIsEmpty() {
    board.setField(new int[]{0, 0}, Color.WHITE);
    assertFalse(board.isEmpty(new int[]{0, 0}));
    assertTrue(board.isEmpty(new int[]{0, 1}));
  }

  @Test
  public void testSetField() {
    board.setField(new int[]{0, 0}, Color.WHITE);
    assertSame(board.getField(new int[]{0, 0}), Color.WHITE);
  }

  @Test
  public void testIsValid() {
    assertTrue(board.isValid(new int[]{0, 0}));
    board.setField(new int[]{0, 0}, Color.WHITE);
    assertFalse(board.isValid(new int[]{0, 0}));
    assertFalse(board.isValid(new int[]{board.DIM, board.DIM}));
  }

  @Test
  public void testHasLiberty() {
    //Test without edge
    board.setField(new int[]{1, 2}, Color.BLACK);
    assertFalse(board.getLibertiesStone(new int[]{1, 2}).isEmpty());
    board.setField(new int[]{1, 1}, Color.WHITE);
    board.setField(new int[]{2, 2}, Color.WHITE);
    board.setField(new int[]{1, 3}, Color.WHITE);
    assertFalse(board.getLibertiesStone(new int[]{1, 2}).isEmpty());
    board.setField(new int[]{0, 2}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesStone(new int[]{1, 2}).isEmpty());
    board.setField(new int[]{0, 2}, Color.BLACK);
    assertTrue(board.getLibertiesStone(new int[]{1, 2}).isEmpty());

    //Test with edge
    assertFalse(board.getLibertiesStone(new int[]{0, 2}).isEmpty());
    board.setField(new int[]{0, 1}, Color.WHITE);
    assertFalse(board.getLibertiesStone(new int[]{0, 2}).isEmpty());
    board.setField(new int[]{0, 3}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesStone(new int[]{0, 2}).isEmpty());
  }


  @Test
  public void testGetGroup() {
    board.setField(new int[]{2, 2}, Color.WHITE);
    board.setField(new int[]{2, 1}, Color.WHITE);
    board.setField(new int[]{3, 2}, Color.WHITE);
    board.setField(new int[]{2, 3}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(board.getGroup(new int[]{2, 1}, true).size(), 4);

    board.setField(new int[]{2, 4}, Color.WHITE);
    board.setField(new int[]{2, 5}, Color.WHITE);
    board.setField(new int[]{3, 6}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(board.getGroup(new int[]{2, 1}, true).size(), 6);
  }

  @Test
  public void testGetCaptured() {
    //Test without edge
    board.setField(new int[]{1, 2}, Color.BLACK);
    board.setField(new int[]{1, 1}, Color.WHITE);
    board.setField(new int[]{2, 2}, Color.WHITE);
    board.setField(new int[]{1, 3}, Color.WHITE);
    assertFalse(board.getLibertiesStone(new int[]{1, 2}).isEmpty());
    board.setField(new int[]{0, 2}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesStone(new int[]{1, 2}).isEmpty());

    for (int[] stone : board.getCaptured(new int[]{0, 2})) {
      System.out.println(Arrays.toString(stone));
    }

    assertEquals(1, board.getCaptured(new int[]{0, 2}).size());
    board.removeCaptured(board.getCaptured(new int[]{0, 2}));
    System.out.println(board.toString());

    // test with edge
    board.setField(new int[]{0, 1}, Color.WHITE);
    board.setField(new int[]{0, 2}, Color.BLACK);
    board.setField(new int[]{1, 2}, Color.BLACK);
    board.setField(new int[]{0, 3}, Color.WHITE);
    System.out.println(board.toString());

    for (int[] stone : board.getCaptured(new int[]{2, 2})) {
      System.out.println(Arrays.toString(stone));
    }

    assertEquals(board.getCaptured(new int[]{2, 2}).size(), 2);
    board.removeCaptured(board.getCaptured(new int[]{2, 2}));
    System.out.println(board.toString());

    // test two captures at the same time

  }

  @Test
  public void testGetTerritory() {
    board.setField(new int[]{1, 0}, Color.BLACK);
    board.setField(new int[]{2, 1}, Color.BLACK);
    board.setField(new int[]{1, 1}, Color.BLACK);
    board.setField(new int[]{1, 2}, Color.WHITE);
    board.setField(new int[]{3, 0}, Color.BLACK);

    System.out.println(board.toString());
    assertEquals(4, board.getStonesWithThisColor(Color.BLACK).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(5, board.getStonesWithThisColor(Color.BLACK).size());

    // filled edge case
    setUp();

    board.setField(new int[]{4, 3}, Color.WHITE);
    board.setField(new int[]{3, 4}, Color.WHITE);
    for (int i = 0; i <= board.DIM; i++) {
      board.setField(new int[]{i, 1}, Color.BLACK);
    }

    assertEquals(9, board.getStonesWithThisColor(Color.BLACK).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(18, board.getStonesWithThisColor(Color.BLACK).size());

    // centre situation
    setUp();

    board.setField(new int[]{4, 3}, Color.WHITE);
    board.setField(new int[]{3, 4}, Color.WHITE);
    board.setField(new int[]{5, 5}, Color.WHITE);
    board.setField(new int[]{3, 6}, Color.WHITE);
    board.setField(new int[]{5, 4}, Color.WHITE);
    board.setField(new int[]{4, 6}, Color.WHITE);
    board.setField(new int[]{2, 5}, Color.WHITE);
    board.setField(new int[]{1, 5}, Color.BLACK);

    assertEquals(7, board.getStonesWithThisColor(Color.WHITE).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(10, board.getStonesWithThisColor(Color.WHITE).size());
  }

}
