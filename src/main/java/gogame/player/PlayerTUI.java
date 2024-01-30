package gogame.player;


import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PlayerTUI {
  boolean run = true;

  public void runTUI() {
    Scanner scanner = new Scanner(System.in);

    InetAddress server = null;
    boolean validServer = false;
    while (!validServer) {
      System.out.print("server:     \n");
      try {
        server = InetAddress.getByName(scanner.nextLine());
        validServer = true;
      } catch (UnknownHostException e) {
        System.err.println("Invalid server address. Please enter a valid address.");
      }
    }

    int PORT = 0;
    boolean validPort = false;
    while (!validPort) {
      System.out.print("port:       \n");
      try {
        PORT = scanner.nextInt();
        validPort = true;
      } catch (InputMismatchException e) {
        System.err.println("Invalid port. Please enter a valid port.");
        scanner.nextLine();
      }
    }

    OnlinePlayer onlinePlayer = new OnlinePlayer();

    System.out.print("strategy:    \n");
    String strategy = scanner.next();
    onlinePlayer.tui = this;
    onlinePlayer.setStrategy(strategy);

    boolean connection = false;
    while (!connection) {
      try {
        onlinePlayer.makeConnection(new Socket(server, PORT));
        connection = true;
      } catch (ConnectException e) {
        System.err.println("Connection failed. Retrying.");
      } catch (IOException e) {
        System.err.println("An IOException error occurred. Please retry.");
      }
    }

    while (run) {
      String message = scanner.nextLine();
      onlinePlayer.playerConnection.sendOutput(message);
    }
  }

  /**
   * Print Message.
   */
  public void printMessage(String message) {
    System.out.println(message);
  }

  public static void main(String[] args) {
    PlayerTUI playerTUI = new PlayerTUI();
    playerTUI.runTUI();
  }
}