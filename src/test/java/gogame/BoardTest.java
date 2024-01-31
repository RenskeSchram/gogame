package gogame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

  private Board board;

  @BeforeEach
  public void setUp() {
    board = new Board(9);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                  Intersections & Stones                                    ///
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void testIsIntersection() {
    assertFalse(board.isIntersection(new int[]{-1, 1}));
    assertTrue(board.isIntersection(new int[]{0, 0}));
    assertTrue(board.isIntersection(new int[]{1, 1}));
    assertFalse(board.isIntersection(new int[]{board.getDIM(), board.getDIM()}));
  }

  @Test
  public void testIsEmptyIntersection() {
    board.setStone(new int[]{0, 0}, Color.WHITE);
    assertFalse(board.isEmptyIntersection(new int[]{0, 0}));
    assertTrue(board.isEmptyIntersection(new int[]{0, 1}));
  }

  @Test
  public void testGetAdjacentIntersections() {
    assertEquals(2, board.getAdjacentIntersections(new int[]{0,0}).size());
    assertEquals(4, board.getAdjacentIntersections(new int[]{5,5}).size());
    board.setStone(new int[]{5, 4}, Color.WHITE);
    assertEquals(4, board.getAdjacentIntersections(new int[]{5,5}).size());
  }


  @Test
  public void testSetStone() {
    board.setStone(new int[]{0, 0}, Color.WHITE);
    assertSame(board.getStone(new int[]{0, 0}), Color.WHITE);
    Board copyBoard = board.deepCopy();
    board.setStone(new int[]{22, 22}, Color.WHITE);
    Arrays.equals(copyBoard.getIntersections(), board.getIntersections());
  }

  @Test
  public void testGetStone() {
    assertNull(board.getStone(new int[]{22, 22}));
    assertEquals(Color.EMPTY, board.getStone(new int[]{0,0}));
    board.setStone(new int[]{0,0}, Color.WHITE);
    assertEquals(Color.WHITE, board.getStone(new int[]{0,0}));
  }

  @Test
  public void testGetStoneWithThisColor() {
    assertEquals(0, board.getStonesWithThisColor(Color.WHITE).size());
    board.setStone(new int[]{0,1}, Color.WHITE);
    board.setStone(new int[]{0,2}, Color.WHITE);
    board.setStone(new int[]{0,3}, Color.WHITE);
    board.setStone(new int[]{0,4}, Color.WHITE);
    board.setStone(new int[]{1,1}, Color.BLACK);
    board.setStone(new int[]{1,2}, Color.BLACK);
    board.setStone(new int[]{1,3}, Color.BLACK);
    assertEquals(4, board.getStonesWithThisColor(Color.WHITE).size());
    assertEquals(3, board.getStonesWithThisColor(Color.BLACK).size());
    assertEquals(board.getDIM()*board.getDIM() -7, board.getStonesWithThisColor(Color.EMPTY).size());
  }

  @Test
  public void testGetLibertiesOfStone() {
    //Test without edge
    board.setStone(new int[]{1, 2}, Color.BLACK);
    assertFalse(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());
    board.setStone(new int[]{1, 1}, Color.WHITE);
    board.setStone(new int[]{2, 2}, Color.WHITE);
    board.setStone(new int[]{1, 3}, Color.WHITE);
    assertFalse(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());
    board.setStone(new int[]{0, 2}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());
    board.setStone(new int[]{0, 2}, Color.BLACK);
    assertTrue(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());

    //Test with edge
    assertFalse(board.getLibertiesOfStone(new int[]{0, 2}).isEmpty());
    board.setStone(new int[]{0, 1}, Color.WHITE);
    assertFalse(board.getLibertiesOfStone(new int[]{0, 2}).isEmpty());
    board.setStone(new int[]{0, 3}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesOfStone(new int[]{0, 2}).isEmpty());

    // test with other adjacent color
    assertEquals(1, board.getLibertiesOfStone(new int[]{0, 1}).size());
    assertEquals(2, board.getLibertiesOfStone(new int[]{1, 1}).size());
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                      Groups                                                ///
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void testGetGroup() {
    board.setStone(new int[]{2, 2}, Color.WHITE);
    board.setStone(new int[]{2, 1}, Color.WHITE);
    board.setStone(new int[]{3, 2}, Color.WHITE);
    board.setStone(new int[]{2, 3}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(board.getGroup(new int[]{2, 1}, true).size(), 4);

    board.setStone(new int[]{2, 4}, Color.WHITE);
    board.setStone(new int[]{2, 5}, Color.WHITE);
    board.setStone(new int[]{3, 6}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(board.getGroup(new int[]{2, 1}, true).size(), 6);

    board.setStone(new int[]{4, 2}, Color.BLACK);
    board.setStone(new int[]{4, 3}, Color.BLACK);
    board.setStone(new int[]{4, 4}, Color.BLACK);
    board.setStone(new int[]{4, 5}, Color.BLACK);
    System.out.println(board.toString());
    assertEquals(board.getGroup(new int[]{2, 1}, true).size(), 6);
    assertEquals(board.getGroup(new int[]{4, 3}, true).size(), 4);

    // get empty group
    assertEquals(board.getGroup(new int[]{3, 3}, false).size(), 3);
    assertEquals(board.getGroup(new int[]{0, 0}, false).size(), board.getDIM()* board.getDIM() - (6+1+4+3));

  }

  @Test
  public void testGetLibertiesOfGroup() {
    board.setStone(new int[]{2, 2}, Color.WHITE);
    board.setStone(new int[]{2, 1}, Color.WHITE);
    board.setStone(new int[]{3, 2}, Color.WHITE);
    board.setStone(new int[]{2, 3}, Color.WHITE);
    System.out.println(board.toString());

    assertEquals(8, board.getLibertiesOfGroup(board.getGroup(new int[]{2, 1}, true)).size());

    board.setStone(new int[]{2, 4}, Color.WHITE);
    board.setStone(new int[]{2, 5}, Color.WHITE);
    board.setStone(new int[]{3, 6}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(12, board.getLibertiesOfGroup(board.getGroup(new int[]{2, 1}, true)).size());

    board.setStone(new int[]{4, 2}, Color.BLACK);
    board.setStone(new int[]{4, 3}, Color.BLACK);
    board.setStone(new int[]{4, 4}, Color.BLACK);
    board.setStone(new int[]{4, 5}, Color.BLACK);
    System.out.println(board.toString());
    assertEquals(9, board.getLibertiesOfGroup(board.getGroup(new int[]{4, 3}, true)).size());

    // test no liberties
    for (int[] intersection : board.getAdjacentIntersections(new int[]{3, 6})) {
      board.setStone(intersection, Color.BLACK);
    }

    System.out.println(board.toString());
    assertEquals(0, board.getLibertiesOfGroup(board.getGroup(new int[]{3, 6}, true)).size());

    for (int[] newintersection : board.getAdjacentIntersections(new int[]{3, 7})) {
      board.setStone(newintersection, Color.BLACK);
    }

    board.setStone(new int[]{3, 6}, Color.WHITE);
    board.setStone(new int[]{3, 7}, Color.WHITE);

    System.out.println(board.toString());
    assertEquals(0, board.getLibertiesOfGroup(board.getGroup(new int[]{3, 6}, true)).size());

    board.setStone(new int[]{4, 6}, Color.WHITE);
    System.out.println(board.toString());
    assertEquals(1, board.getLibertiesOfGroup(board.getGroup(new int[]{3, 6}, true)).size());
  }

  @Test
  public void testContainsIntersection() {
    List<int[]> testList = new ArrayList<>();
    testList.add(new int[]{2,2});
    testList.add(new int[]{3,2});
    testList.add(new int[]{2,4});
    assertTrue(Board.containsIntersection(testList, new int[]{2,2}));
    assertFalse(Board.containsIntersection(testList, new int[]{5,5}));
    assertFalse(Board.containsIntersection(testList, new int[]{-1,-1}));
  }

  @Test
  public void testGetTerritoryColor() {
    board.setStone(new int[]{1, 0}, Color.BLACK);
    board.setStone(new int[]{2, 1}, Color.BLACK);
    board.setStone(new int[]{1, 1}, Color.BLACK);
    board.setStone(new int[]{1, 2}, Color.WHITE);
    board.setStone(new int[]{3, 0}, Color.BLACK);

    System.out.println(board.toString());
    assertEquals(Color.BLACK, board.getTerritoryColor(board.getGroup(new int[]{2,0}, false)));

    setUp();

    board.setStone(new int[]{4, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    for (int i = 0; i <= board.getDIM(); i++) {
      board.setStone(new int[]{i, 1}, Color.BLACK);
    }

    System.out.println(board.toString());
    assertEquals(Color.BLACK, board.getTerritoryColor(board.getGroup(new int[]{0,0}, false)));
    assertEquals(Color.NEUTRAL, board.getTerritoryColor(board.getGroup(new int[]{2,7}, false)));

    // centre situation
    setUp();

    board.setStone(new int[]{4, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    board.setStone(new int[]{5, 5}, Color.WHITE);
    board.setStone(new int[]{3, 6}, Color.WHITE);
    board.setStone(new int[]{5, 4}, Color.WHITE);
    board.setStone(new int[]{4, 6}, Color.WHITE);
    board.setStone(new int[]{2, 5}, Color.WHITE);
    board.setStone(new int[]{1, 5}, Color.BLACK);

    System.out.println(board.toString());
    assertEquals(Color.WHITE, board.getTerritoryColor(board.getGroup(new int[]{4,4}, false)));
    assertEquals(Color.NEUTRAL, board.getTerritoryColor(board.getGroup(new int[]{0,0}, false)));
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                      Board Rules                                           ///
  //////////////////////////////////////////////////////////////////////////////////////////////////
  @Test
  public void testIsValid() {
    assertTrue(board.isValid(new int[]{0, 0}));
    board.setStone(new int[]{0, 0}, Color.WHITE);
    assertFalse(board.isValid(new int[]{0, 0}));
    assertFalse(board.isValid(new int[]{board.getDIM(), board.getDIM()}));
  }
  @Test
  public void isSuicide() {
    for (int[] intersection : board.getAdjacentIntersections(new int[]{3, 6})) {
      board.setStone(intersection, Color.BLACK);
    }
    assertTrue(board.isSuicide(new int[]{3,6}));

    for (int[] intersection : board.getAdjacentIntersections(new int[]{3, 7})) {
      board.setStone(intersection, Color.BLACK);
    }
    board.setStone(new int[]{3, 6}, Color.WHITE);
    board.setStone(new int[]{3, 7}, Color.WHITE);

    assertTrue(board.isSuicide(new int[]{3,6}));

    // has liberty
    board.setStone(new int[]{3, 6}, Color.EMPTY);
    board.setStone(new int[]{3, 7}, Color.EMPTY);

    assertFalse(board.isSuicide(new int[]{3,6}));
  }

  @Test
  public void testGetAndRemoveCaptured() {
    //Test without edge
    board.setStone(new int[]{1, 2}, Color.BLACK);
    board.setStone(new int[]{1, 1}, Color.WHITE);
    board.setStone(new int[]{2, 2}, Color.WHITE);
    board.setStone(new int[]{1, 3}, Color.WHITE);
    assertFalse(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());
    board.setStone(new int[]{0, 2}, Color.WHITE);
    System.out.println(board.toString());
    assertTrue(board.getLibertiesOfStone(new int[]{1, 2}).isEmpty());

    assertEquals(1, board.getCaptured(new int[]{0, 2}).size());
    board.removeCaptured(board.getCaptured(new int[]{0, 2}));
    assertEquals(Color.EMPTY, board.getStone(new int[]{1,2}));
    System.out.println(board.toString());

    // test with edge
    board.setStone(new int[]{0, 1}, Color.WHITE);
    board.setStone(new int[]{0, 2}, Color.BLACK);
    board.setStone(new int[]{1, 2}, Color.BLACK);
    board.setStone(new int[]{0, 3}, Color.WHITE);
    System.out.println(board.toString());

    assertEquals(board.getCaptured(new int[]{2, 2}).size(), 2);
    board.removeCaptured(board.getCaptured(new int[]{2, 2}));
    assertEquals(Color.EMPTY, board.getStone(new int[]{1,2}));
    assertEquals(Color.EMPTY, board.getStone(new int[]{0,2}));

    System.out.println(board.toString());
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  ///                                         Scoring                                            ///
  //////////////////////////////////////////////////////////////////////////////////////////////////
  @Test
  public void testGetFilledBoard() {
    board.setStone(new int[]{1, 0}, Color.BLACK);
    board.setStone(new int[]{2, 1}, Color.BLACK);
    board.setStone(new int[]{1, 1}, Color.BLACK);
    board.setStone(new int[]{1, 2}, Color.WHITE);
    board.setStone(new int[]{3, 0}, Color.BLACK);

    System.out.println(board.toString());
    assertEquals(4, board.getStonesWithThisColor(Color.BLACK).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(5, board.getStonesWithThisColor(Color.BLACK).size());

    // filled edge case
    setUp();

    board.setStone(new int[]{4, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    for (int i = 0; i <= board.getDIM(); i++) {
      board.setStone(new int[]{i, 1}, Color.BLACK);
    }

    assertEquals(9, board.getStonesWithThisColor(Color.BLACK).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(18, board.getStonesWithThisColor(Color.BLACK).size());

    // centre situation
    setUp();

    board.setStone(new int[]{4, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    board.setStone(new int[]{5, 5}, Color.WHITE);
    board.setStone(new int[]{3, 6}, Color.WHITE);
    board.setStone(new int[]{5, 4}, Color.WHITE);
    board.setStone(new int[]{4, 6}, Color.WHITE);
    board.setStone(new int[]{2, 5}, Color.WHITE);
    board.setStone(new int[]{1, 5}, Color.BLACK);

    assertEquals(7, board.getStonesWithThisColor(Color.WHITE).size());

    board.getFilledBoard();
    System.out.println(board.toString());
    assertEquals(10, board.getStonesWithThisColor(Color.WHITE).size());
  }

  @Test
  public void testGetWinner() {
    board.setStone(new int[]{1, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    board.setStone(new int[]{5, 5}, Color.WHITE);
    board.setStone(new int[]{3, 6}, Color.WHITE);
    board.setStone(new int[]{5, 8}, Color.BLACK);
    board.setStone(new int[]{4, 6}, Color.WHITE);
    board.setStone(new int[]{2, 5}, Color.WHITE);
    board.setStone(new int[]{1, 5}, Color.BLACK);
    board.setStone(new int[]{2, 3}, Color.WHITE);
    board.setStone(new int[]{3, 4}, Color.WHITE);
    board.setStone(new int[]{4, 5}, Color.BLACK);
    board.setStone(new int[]{6, 6}, Color.WHITE);
    for (int i = 0; i <= board.getDIM(); i++) {
      board.setStone(new int[]{i, 1}, Color.BLACK);
    }
    board.setStone(new int[]{8, 5}, Color.WHITE);
    board.setStone(new int[]{3, 5}, Color.BLACK);
    board.setStone(new int[]{4, 4}, Color.WHITE);
    board.setStone(new int[]{5, 7}, Color.WHITE);
    board.setStone(new int[]{6, 7}, Color.WHITE);
    board.setStone(new int[]{6, 8}, Color.WHITE);
    board.setStone(new int[]{7, 5}, Color.WHITE);

    System.out.println(board.toString());

    assertSame(board.getWinner(), Color.BLACK);
    assertEquals(22, board.getStonesWithThisColor(Color.BLACK).size());
    assertEquals(21, board.getStonesWithThisColor(Color.WHITE).size());
    System.out.println(board.toString());

  }
}
