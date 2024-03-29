package gogame;

/**
 * GoGame player.
 */
public abstract class Player {

  private Color color;
  private String userName;
  public Game game;

  /**
   * Send move to board.
   * @param intersection location of stone to be placed.
   */
  public void doMove(int[] intersection, Color color) {
    game.doMove(intersection, this.color);
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
   * @param color assigned color
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * Return color of this ServerPlayer.
   * @return color of this ServerPlayer
   */
  public Color getColor() {
    return this.color;
  }

  /**
   * Set username of this ServerPlayer.
   * @param userName assigned color
   */
  public void setUsername(String userName) {
    this.userName = userName;
  }

  /**
   * Return username of this Player.
   * @return username of this Player
   */
  public String getUsername() {
    return this.userName;
  }

  protected void quitGame() {
    this.game = null;
  }

  @Override
  public String toString() {
    return userName;
  }

  public abstract SocketConnection getConnection();

  protected abstract void passGameUpdate(String gameUpdate);

}
