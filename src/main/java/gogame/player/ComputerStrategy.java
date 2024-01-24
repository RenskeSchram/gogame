package gogame.player;

import gogame.Player;
import gogame.Protocol;

public class ComputerStrategy implements Strategy {
    Player player;

    ComputerStrategy(OnlinePlayer player){
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
        //automated move determination...

        //player.getConnection().sendOutput(Protocol.MOVE + Protocol.SEPARATOR + "-1");

    }
}
