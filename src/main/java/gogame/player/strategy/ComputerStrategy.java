package gogame.player.strategy;

import gogame.Protocol;
import gogame.player.OnlinePlayer;
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

    @Override
    public void sendQueue() {
        player.getConnection().sendOutput(Protocol.QUEUE);
    }

    @Override
    public void sendHello() {
        player.getConnection().sendOutput(Protocol.HELLO);
    }

    public int[] getRandomValidMove() {
        List<int[]> validMoves = player.game.getValidMoves();
        return validMoves.get(new Random().nextInt(validMoves.size()));
    }

}
