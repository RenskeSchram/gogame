package gogame;

public abstract class Player {
    Color color;
    String userName;
    public Game game;

    /**
     * Send move to board and add this ServerPlayer as parameter.
     * @param location
     */
    public void doMove(int[] location, Color color) {
        game.doMove(location, this.color);
    }

    /**
     * Send pass to the board and add this ServerPlayer as parameter.
     */
    public void doPass() {
        game.doPass(this.color);
    }

    /**
     * Send pass to the board and add this ServerPlayer as parameter.
     */
    public void doResign() {
        game.doResign(color);
    }

    /**
     * Set assigned color of this ServerPlayer.
     *
     * @param color assigned color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Return color of this ServerPlayer.
     *
     * @return color of this ServerPlayer
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Set username of this ServerPlayer.
     *
     * @param userName assigned color
     */
    public void setUsername(String userName) {
        this.userName = userName;
    }

    /**
     * Return username of this Player.
     *
     * @return username of this Player
     */
    public String getUsername() {
        return this.userName;
    }

    public abstract SocketConnection getConnection();

    public abstract void passGameUpdate(String gameUpdate);

    public abstract void lookAtBoard();

    public void quitGame(){
        this.game = null;
    }
}
