package gogame;

import java.util.Timer;
import java.util.TimerTask;

public class MoveTimer extends Thread {
  private final Game game;
  private Timer timer;
  private TimerTask timeOutPass;
  private final int MOVE_DURATION = 2000;
  public boolean useTimer = true;

  public MoveTimer(Game game) {
    this.game = game;
  }

  protected synchronized void startTimer() {
    if (useTimer) {
      timer = new Timer();
      timeOutPass = new TimerTask() {
        @Override
        public void run() {
          synchronized (game) {
            game.getTurn().passGameUpdate(Protocol.ERROR + Protocol.SEPARATOR + "automated pass");
            game.doPass(game.getTurn().getColor());
          }
        }
      };

      timer.schedule(timeOutPass, MOVE_DURATION);
    }
  }

  protected synchronized void stopTimer() {
    if (useTimer && timer != null) {
      timer.cancel();
      timer = null;
    }

    if (useTimer && timeOutPass != null) {
      timeOutPass.cancel();
      timeOutPass = null;
    }
  }

  protected void resetTimer() {
    stopTimer();
    startTimer();
  }

  public Timer getTimer() {
    return timer;
  }

  public TimerTask getTimeOutPass() {
    return timeOutPass;
  }
}