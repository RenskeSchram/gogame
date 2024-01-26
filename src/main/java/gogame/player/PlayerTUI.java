package gogame.player;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class PlayerTUI {

  OnlinePlayer onlinePlayer;
  boolean run = true;

  public void runTUI() throws IOException {
    // Scanner to retrieve server, port and username.
    Scanner scanner = new Scanner(System.in);
    System.out.print("server:     ");
    InetAddress server = InetAddress.getByName(scanner.nextLine());
    System.out.print("port:       ");
    int PORT = scanner.nextInt();
    onlinePlayer = new OnlinePlayer();
    System.out.print("strategy:    ");
    String name = scanner.next();
    onlinePlayer.tui = this;
    onlinePlayer.setStrategy(name);
    onlinePlayer.makeConnection(new Socket(server, PORT));

    while (run) {
      String message = scanner.nextLine();
      onlinePlayer.playerConnection.sendOutput(message);
    }

  }

  public static void main(String[] args) throws IOException {
    PlayerTUI playerTUI = new PlayerTUI();
    playerTUI.runTUI();
  }

  /**
   * Print Message.
   */
  public void printMessage(String message) {
    System.out.println(message);
  }

  public void printBoard(String board) { System.out.println( "\n" + board); }
}