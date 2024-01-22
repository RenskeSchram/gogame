package gogame;

import connection.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    //private GameServer gameServer = new GameServer(8080);


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
        assertTrue(game.isValid(6, Color.BLACK));
        assertFalse(game.isValid(Board.DIM*Board.DIM+1, Color.BLACK));
        assertFalse(game.isValid(6, Color.WHITE));
    }

    @Test
    public void testDoMove() {
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.EMPTY);
        game.doMove(6, Color.BLACK);
        assertSame(game.board.getField(game.getCoordinate(6)[0], game.getCoordinate(6)[1]), Color.BLACK);

        // Correct player

        // Ko-rule
        game.board.setField(Board.DIM - 2, Board.DIM - 1, Color.WHITE);
        game.board.setField(Board.DIM - 1, Board.DIM - 2, Color.WHITE);
        game.board.setField(Board.DIM - 2, Board.DIM - 2, Color.BLACK);
        game.board.setField(Board.DIM - 1, Board.DIM - 3, Color.BLACK);
        game.board.setField(Board.DIM - 3, Board.DIM - 3, Color.BLACK);

        game.doMove(Board.DIM*(Board.DIM-3) + Board.DIM-2 , Color.WHITE);
        game.doMove(Board.DIM*(Board.DIM-2) + Board.DIM-4 , Color.BLACK);
        game.doMove(Board.DIM*(Board.DIM-2) + Board.DIM-3 , Color.WHITE);
        assertTrue(game.isKoFight(Board.DIM*(Board.DIM-2) + Board.DIM-2 , Color.BLACK));
        game.doMove(Board.DIM*(Board.DIM-2) + Board.DIM-2 , Color.BLACK);
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
