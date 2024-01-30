package gogame.server;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameServerTUI {

  boolean run = true;

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

    GameServer gameServer = new GameServer(port);
    gameServer.acceptConnections();
    System.out.println(String.format("%-20s", "[SERVER STATED]") + String.format("%-20s", "port: "+ port));

    while (run) {
      String systemTuiInput = scanner.nextLine();
      handleGameServerInput(systemTuiInput);
    }
  }

  private void handleGameServerInput(String systemTuiInput) {
    switch (systemTuiInput) {
      case "exit":
        GameServer.stop();
        run = false;
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

}
