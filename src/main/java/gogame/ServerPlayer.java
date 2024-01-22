package gogame;

import connection.GameServer;
import connection.ServerConnection;

/**
 * Server implementation of player in a game.
 */
public class ServerPlayer {
    protected Color color;
    protected Game game;
    protected String userName;
    public GameServer gameServer;


    public ServerPlayer() {
    }

    /**
     * Send move to board and add this ServerPlayer as parameter.
     *
     * @param location
     */
    public void doMove(int location) {
        game.doMove(location, color);
    }

    /**
     * Send pass to the board and add this ServerPlayer as parameter.
     */
    public void doPass() {
        game.doPass(color);
    }

    /**
     * Set assigned color of this ServerPlayer.
     *
     * @param color assigned color
     */
    protected void setColor(Color color) {
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

}
