package gogame.server;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameServerTUI {

  GameServer gameServer;

  boolean runTui = true;

  public void runTUI() throws IOException {
    Scanner scanner = new Scanner(System.in);

    int port = 0;
    boolean validPort = false;
    while (!validPort) {
      System.out.print("server port number: \n");
      try {
        port = scanner.nextInt();
        validPort = true;
      } catch (InputMismatchException e) {
        System.err.println("Invalid port number. Please enter a valid port.");
        scanner.nextLine();
      }
    }

    gameServer = new GameServer(port);
    System.out.println(String.format("%-20s", "[SERVER STARTED]") + String.format("%-20s", "PORT: "+ port));

    while (runTui) {
      String systemTuiInput = scanner.nextLine();
      handleGameServerInput(systemTuiInput);
    }
  }

  private void handleGameServerInput(String tuiInput) {
    String[] splittedTuiInput = tuiInput.split(" ");

    switch (splittedTuiInput[0]) {

      case "exit":
        gameServer.stopServer();
        runTui = false;
        break;

      case "print":
        System.out.println(gameServer.toString());
        break;

      case "setdim":
        try {
          gameServer.setServerBoardDIM(Integer.parseInt(splittedTuiInput[1]));
        } catch (NumberFormatException e) {
          System.err.println("Could not set new DIM");
        }
        System.out.println("Current board DIM = " + gameServer.getServerBoardDIM());
        break;

      case "getdim":
        System.out.println("Current board DIM = " + gameServer.getServerBoardDIM());
        break;

      case "help":
        System.out.println(this);
        break;

      default:
        //ignore message
        break;
    }
  }

  public static void main(String[] args) throws IOException {
    GameServerTUI gameServerTUI = new GameServerTUI();
    gameServerTUI.runTUI();
  }

  @Override
  public String toString() {
    return
        "Renske's GameServer TUI commands:\n" +
            "   print ...................... print current state of GameServer \n" +
            "   setdim <int> ............... set board DIM for new Games \n" +
            "   getdim ..................... get currently applied board DIM \n" +
            "   help ....................... help (this menu) \n" +
            "   exit ....................... exit TUI and stop GameServer \n";
  }
}
