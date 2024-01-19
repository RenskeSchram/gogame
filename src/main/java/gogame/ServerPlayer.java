package gogame;

public class ServerPlayer {
    protected Color color;
    protected Game game;


    public ServerPlayer() {
    }

    private void doMove(int location) {
        game.doMove(location, color);
    }

    private void doPass() {
        game.doPass(color);
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

}
