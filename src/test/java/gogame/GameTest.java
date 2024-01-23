package gogame;

import gogame.server.ServerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp(){
        game = new Game(new ServerPlayer(), new ServerPlayer());
    }

    @Test
    public void testGetCoordinate() {
        assertArrayEquals(game.getCoordinate(Board.DIM+2), new int[]{1, 2});
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
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.EMPTY);
        game.doMove(game.getCoordinate(6), Color.BLACK);
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.BLACK);

        // Correct player

        // Ko-rule
        game.board.setField(Board.DIM - 2, Board.DIM - 1, Color.WHITE);
        game.board.setField(Board.DIM - 1, Board.DIM - 2, Color.WHITE);
        game.board.setField(Board.DIM - 2, Board.DIM - 2, Color.BLACK);
        game.board.setField(Board.DIM - 1, Board.DIM - 3, Color.BLACK);
        game.board.setField(Board.DIM - 3, Board.DIM - 3, Color.BLACK);
        System.out.println(game.board.toString());

        game.doMove(new int[]{Board.DIM-3, Board.DIM-2} , Color.WHITE);
        game.doMove(new int[]{Board.DIM-2, Board.DIM-4} , Color.BLACK);
        game.doMove(new int[]{Board.DIM-2, Board.DIM-3} , Color.WHITE);
        System.out.println(game.board.toString());
        assertTrue(game.isKoFight(new int[]{Board.DIM-2, Board.DIM-2} , Color.BLACK));
        game.doMove(new int[]{Board.DIM-2, Board.DIM-2}, Color.BLACK);
        assertSame(game.getTurn().color, Color.BLACK);
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
