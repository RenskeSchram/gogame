package gogame.player.strategy;

import gogame.player.OnlinePlayer;

public class HumanStrategy implements Strategy {
    OnlinePlayer player;

    HumanStrategy(OnlinePlayer player){
        this.player = player;
    }
    @Override
    public void getUsername() {
        //player.receiveMessage("Send a username using: LOGIN~<username>");

    }

    @Override
    public void determineMove() {
        player.receiveMessage("Send a new move using: MOVE~<int> or MOVE~<int, int>");
    }

    @Override
    public void sendQueue() {
        //player.receiveMessage("You are queued, if you want to leave the queue, send: QUEUE");

    }

    @Override
    public void sendHello() {
        player.receiveMessage("Send HELLO to verify the connection");
    }
}
