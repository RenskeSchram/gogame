package gogame;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(9);
    }

    @Test
    public void testIsField() {
        assertFalse(board.isField(-1, 1));
        assertTrue(board.isField(0, 0));
        assertTrue(board.isField(board.DIM - 1, board.DIM - 1));
        assertFalse(board.isField(board.DIM, board.DIM));
    }

    @Test
    public void testIsEmpty() {
        board.setField(0, 0, Color.WHITE);
        assertFalse(board.isEmpty(0, 0));
        assertTrue(board.isEmpty(0, 1));
    }

    @Test
    public void testSetField() {
        board.setField(0, 0, Color.WHITE);
        assertSame(board.getField(0, 0), Color.WHITE);
    }

    @Test
    public void testIsValid() {
        assertTrue(board.isValid(0, 0));
        board.setField(0, 0, Color.WHITE);
        assertFalse(board.isValid(0, 0));
        assertFalse(board.isValid(board.DIM, board.DIM));
    }

    @Test
    public void testHasLiberty() {
        //Test without edge
        board.setField(board.DIM - 2, board.DIM - 2, Color.BLACK);
        assertTrue(board.hasLiberty(board.DIM - 2, board.DIM - 2));
        board.setField(board.DIM - 2, board.DIM - 1, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 2, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 3, Color.WHITE);
        assertTrue(board.hasLiberty(board.DIM - 2, board.DIM - 2));
        board.setField(board.DIM - 1, board.DIM - 2, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(board.DIM - 2, board.DIM - 2));
        board.setField(board.DIM - 1, board.DIM - 2, Color.BLACK);
        assertFalse(board.hasLiberty(board.DIM - 2, board.DIM - 2));

        //Test with edge
        assertTrue(board.hasLiberty(board.DIM - 1, board.DIM - 2));
        board.setField(board.DIM - 1, board.DIM - 1, Color.WHITE);
        assertTrue(board.hasLiberty(board.DIM - 1, board.DIM - 2));
        board.setField(board.DIM - 1, board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(board.DIM - 1, board.DIM - 2));
    }


    @Test
    public void testGetGroup() {
        board.setField(board.DIM - 2, board.DIM - 2, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 1, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 2, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());
        assertEquals(board.getGroup(board.DIM - 2, board.DIM - 1, true).size(), 4);

        board.setField(board.DIM - 2, board.DIM - 4, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 5, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 6, Color.WHITE);
        System.out.println(board.toString());
        assertEquals(board.getGroup(board.DIM - 2, board.DIM - 1, true).size(), 6);
    }

    @Test
    public void testGetCaptured() {
        //Test without edge
        board.setField(board.DIM - 2, board.DIM - 2, Color.BLACK);
        board.setField(board.DIM - 2, board.DIM - 1, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 2, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 3, Color.WHITE);
        assertTrue(board.hasLiberty(board.DIM - 2, board.DIM - 2));
        board.setField(board.DIM - 1, board.DIM - 2, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(board.DIM - 2, board.DIM - 2));

        for (int[] stone : board.getCaptured(board.DIM - 1, board.DIM - 2)) {
            System.out.println(Arrays.toString(stone));
        }
        assertEquals(board.getCaptured(board.DIM - 1, board.DIM - 2).size(), 1);
        board.removeCaptured(board.getCaptured(board.DIM - 1, board.DIM - 2));
        System.out.println(board.toString());

        // test with edge
        board.setField(board.DIM - 1, board.DIM - 1, Color.WHITE);
        board.setField(board.DIM - 1, board.DIM - 2, Color.BLACK);
        board.setField(board.DIM - 2, board.DIM - 2, Color.BLACK);
        board.setField(board.DIM - 1, board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());

        for (int[] stone : board.getCaptured(board.DIM - 3, board.DIM - 2)) {
            System.out.println(Arrays.toString(stone));
        }

        assertEquals(board.getCaptured(board.DIM - 3, board.DIM - 2).size(), 2);
        board.removeCaptured(board.getCaptured(board.DIM - 3, board.DIM - 2));
        System.out.println(board.toString());

        board.getFilledBoard();
        System.out.println(board.toString());

        // test two captures at the same time

    }
    @Test
    public void testGetTerritory() {
        board.setField(board.DIM - 1, board.DIM - 1, Color.BLACK);
        board.setField(board.DIM - 1, board.DIM - 2, Color.BLACK);
        board.setField(board.DIM - 2, board.DIM - 2, Color.BLACK);
        board.setField(board.DIM - 1, board.DIM - 3, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 1, Color.BLACK);

        System.out.println(board.toString());

        board.getFilledBoard();
        System.out.println(board.toString());


        // filled edge case
        setUp();

        board.setField(board.DIM - 5, board.DIM - 4, Color.WHITE);
        board.setField(board.DIM - 4, board.DIM - 5, Color.WHITE);
        for (int i = 1; i <= board.DIM; i++) {
            board.setField(board.DIM - i, board.DIM - 2, Color.BLACK);
        }

        System.out.println(board.toString());

        board.getFilledBoard();
        System.out.println(board.toString());


        // centre situation
        setUp();

        board.setField(board.DIM - 5, board.DIM - 4, Color.WHITE);
        board.setField(board.DIM - 4, board.DIM - 5, Color.WHITE);
        board.setField(board.DIM - 6, board.DIM - 6, Color.WHITE);
        board.setField(board.DIM - 4, board.DIM - 7, Color.WHITE);
        board.setField(board.DIM - 6, board.DIM - 5, Color.WHITE);
        board.setField(board.DIM - 5, board.DIM - 7, Color.WHITE);
        board.setField(board.DIM - 3, board.DIM - 6, Color.WHITE);
        board.setField(board.DIM - 2, board.DIM - 6, Color.BLACK);
        board.setField(board.DIM - 5, board.DIM - 6, Color.BLACK);

        System.out.println(board.toString());
        board.getFilledBoard();
        System.out.println(board.toString());

    }


}
