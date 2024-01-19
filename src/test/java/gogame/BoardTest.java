package gogame;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;

    @BeforeEach
    public void setUp(){
        board = new Board();
    }

    @Test
    public void testIsField() {
        assertFalse(board.isField(-1, 1));
        assertTrue(board.isField(0, 0));
        assertTrue(board.isField(Board.DIM-1,  Board.DIM - 1));
        assertFalse(board.isField(Board.DIM, Board.DIM));
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
        assertTrue(board.isValid(0, 0, Color.WHITE));
        board.setField(0, 0, Color.WHITE);
        assertFalse(board.isValid(0, 0, Color.BLACK));
        assertFalse(board.isValid(Board.DIM, Board.DIM, Color.BLACK));
    }

    @Test
    public void testHasLiberty() {
        //Test without edge
        board.setField(Board.DIM - 2, Board.DIM - 2, Color.BLACK);
        assertTrue(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));
        board.setField(Board.DIM - 2, Board.DIM - 1, Color.WHITE);
        board.setField(Board.DIM - 3, Board.DIM - 2, Color.WHITE);
        board.setField(Board.DIM - 2, Board.DIM - 3, Color.WHITE);
        assertTrue(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));
        board.setField(Board.DIM - 1, Board.DIM - 2, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));
        board.setField(Board.DIM - 1, Board.DIM - 2, Color.BLACK);
        assertFalse(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));

        //Test with edge
        assertTrue(board.hasLiberty(Board.DIM - 1, Board.DIM - 2));
        board.setField(Board.DIM - 1, Board.DIM - 1, Color.WHITE);
        assertTrue(board.hasLiberty(Board.DIM - 1, Board.DIM - 2));
        board.setField(Board.DIM - 1, Board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(Board.DIM - 1, Board.DIM - 2));
    }


    @Test
    public void testGetGroup() {
        board.setField(Board.DIM - 2, Board.DIM - 2, Color.WHITE);
        board.setField(Board.DIM - 2, Board.DIM - 1, Color.WHITE);
        board.setField(Board.DIM - 3, Board.DIM - 2, Color.WHITE);
        board.setField(Board.DIM - 2, Board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());
        assertEquals(board.getGroup(Board.DIM - 2, Board.DIM - 1).size(), 4);

        board.setField(Board.DIM - 2, Board.DIM - 4, Color.WHITE);
        board.setField(Board.DIM - 2, Board.DIM - 5, Color.WHITE);
        board.setField(Board.DIM - 3, Board.DIM - 6, Color.WHITE);
        System.out.println(board.toString());
        assertEquals(board.getGroup(Board.DIM - 2, Board.DIM - 1).size(), 6);
    }

    @Test
    public void testGetCaptured() {
        //Test without edge
        board.setField(Board.DIM - 2, Board.DIM - 2, Color.BLACK);
        board.setField(Board.DIM - 2, Board.DIM - 1, Color.WHITE);
        board.setField(Board.DIM - 3, Board.DIM - 2, Color.WHITE);
        board.setField(Board.DIM - 2, Board.DIM - 3, Color.WHITE);
        assertTrue(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));
        board.setField(Board.DIM - 1, Board.DIM - 2, Color.WHITE);
        System.out.println(board.toString());
        assertFalse(board.hasLiberty(Board.DIM - 2, Board.DIM - 2));

        for (int[] stone : board.getCaptured(Board.DIM - 1, Board.DIM - 2)){
            System.out.println(Arrays.toString(stone));
        }
        assertEquals(board.getCaptured(Board.DIM - 1, Board.DIM - 2).size(), 1);
        board.removeCaptured(board.getCaptured(Board.DIM - 1, Board.DIM - 2));
        System.out.println(board.toString());

        board.setField(Board.DIM - 1, Board.DIM - 1, Color.WHITE);
        board.setField(Board.DIM - 1, Board.DIM - 2, Color.BLACK);
        board.setField(Board.DIM - 2, Board.DIM - 2, Color.BLACK);
        board.setField(Board.DIM - 1, Board.DIM - 3, Color.WHITE);
        System.out.println(board.toString());

        for (int[] stone : board.getCaptured(Board.DIM - 3, Board.DIM - 2)){
            System.out.println(Arrays.toString(stone));
        }

        assertEquals(board.getCaptured(Board.DIM - 3, Board.DIM - 2).size(), 2);
        board.removeCaptured(board.getCaptured(Board.DIM - 3, Board.DIM - 2));
        System.out.println(board.toString());

    }
}
