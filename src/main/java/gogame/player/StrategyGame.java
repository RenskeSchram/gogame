package gogame.player;

import gogame.Game;
import gogame.Player;

public class StrategyGame extends Game {
    /**
     * Constructor for new Game object with players and a new Board.
     *
     * @param firstPlayer
     * @param secondPlayer
     * @param DIM
     */
    public StrategyGame(Player firstPlayer, Player secondPlayer, int DIM) {
        super(firstPlayer, secondPlayer, DIM);
    }
    @Override
    protected void startTimer() {
        //do nothing
    }

    @Override
    protected void stopTimer() {
        //do nothing
    }
    @Override
    protected void resetTimer() {
        //do nothing

    }
}
