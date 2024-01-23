package gogame.player;

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
}
