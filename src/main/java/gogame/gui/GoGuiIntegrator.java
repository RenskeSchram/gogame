package gogame.gui;

import javafx.application.Platform;

public class GoGuiIntegrator implements GoGui {

  private GoGuiImpl wrappee;

  /**
   * Creates a GoGUIIntegrator that is capable of configuring and controlling the
   * GO GUI.
   *
   * @param showStartupAnimation if true then a startup animation will be shown
   *                             when the GO GUI is started.
   * @param mode3D               if true then the stones will be shown in 3D.
   *                             Otherwise a 2D representation will be used.
   * @param boardSize            the desired initial board size.
   */
  public GoGuiIntegrator(boolean showStartupAnimation, boolean mode3D, int boardSize) {
    createWrappedObject();
    wrappee.setShowStartupAnimation(showStartupAnimation);
    wrappee.setMode3D(mode3D);
    wrappee.setInitialBoardSize(boardSize);
  }

  @Override
  public synchronized void setBoardSize(int size) {
    Platform.runLater(() -> wrappee.setBoardSize(size));
  }

  public synchronized int getBoardSize() {
    return wrappee.getBoardSize();
  }

  @Override
  public synchronized void addStone(int x, int y, boolean white) {
    Platform.runLater(() -> {
      try {
        wrappee.addStone(x, y, white);
      } catch (InvalidCoordinateException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public synchronized void removeStone(int x, int y) {
    Platform.runLater(() -> {
      try {
        wrappee.removeStone(x, y);
      } catch (InvalidCoordinateException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public synchronized void addAreaIndicator(int x, int y, boolean white) {
    Platform.runLater(() -> {
      try {
        wrappee.addAreaIndicator(x, y, white);
      } catch (InvalidCoordinateException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public synchronized void addHintIndicator(int x, int y) {
    Platform.runLater(() -> {
      try {
        wrappee.addHintIndicator(x, y);
      } catch (InvalidCoordinateException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public synchronized void removeHintIdicator() {
    Platform.runLater(() -> wrappee.removeHintIndicator());
  }

  @Override
  public synchronized void clearBoard() {
    Platform.runLater(() -> wrappee.clearBoard());
  }

  @Override
  public synchronized void startGUI() {
    startJavaFX();
    wrappee.waitForInitializationLatch();
    System.out.println("GO GUI was successfully started!");
  }

  @Override
  public synchronized void stopGUI() {
    // Not implemented yet
    System.out.println("Stopping GUI");
    wrappee.clearBoard();
  }

  private void createWrappedObject() {
    if (wrappee == null) {
      GoGuiImpl.startGUI();

      while (!GoGuiImpl.isInstanceAvailable()) {
        try {
          Thread.sleep(20);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      wrappee = GoGuiImpl.getInstance();
    }
  }

  private void startJavaFX() {
    createWrappedObject();
    wrappee.countDownConfigurationLatch();
  }
}
