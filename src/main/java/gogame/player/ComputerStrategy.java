package gogame.player;

import gogame.Protocol;
import java.util.List;
import java.util.Random;

public class ComputerStrategy implements Strategy {
    OnlinePlayer player;

    public ComputerStrategy(OnlinePlayer player){
        this.player = player;
    }
    @Override
    public void getUsername() {
        if (player.getUsername() == null) {
            player.setUsername("Computer");
        } else {
            player.setUsername(player.getUsername() + "r");
        }
        player.getConnection().sendOutput(Protocol.LOGIN + Protocol.SEPARATOR + player.getUsername());
    }

    @Override
    public void determineMove() {
        player.getConnection().sendOutput(Protocol.MOVE + Protocol.SEPARATOR + gogame.Move.intersectionLocationToString(getRandomValidMove()));
    }

    public int[] getRandomValidMove() {
        List<int[]> validMoves = player.game.getValidMoves();
        return validMoves.get(new Random().nextInt(validMoves.size()));
    }

}
