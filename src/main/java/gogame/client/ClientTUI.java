package gogame.client;


import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Client TUI to start a new Client side, create a player to play games and handle the Client side.
 */
public class ClientTUI {

  private boolean run = true;

  private void runTUI() {
    Scanner scanner = new Scanner(System.in);

    // retrieve server adres
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

    // retrieve port number
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

    // create a new client player based on strategy
    ClientPlayer onlinePlayer = new ClientPlayer();

    System.out.print("strategy:    \n");
    String strategy = scanner.next();
    onlinePlayer.tui = this;
    onlinePlayer.setStrategy(strategy);

    // make connection
    boolean connection = false;
    while (!connection) {
      try {
        onlinePlayer.makeConnection(new Socket(server, PORT));
        connection = true;
      } catch (ConnectException e) {
        System.err.println("Connecting failed.");
        break;
      } catch (IOException e) {
        System.err.println("An IOException error occurred while making a connection.");
        break;
      }
    }

    while (run) {
      String message = scanner.nextLine();

      // TUI options beside regular protocol
      if (message.equalsIgnoreCase("help")) {
        System.out.println(this);
      } else if (message.equalsIgnoreCase("disconnect")) {
        onlinePlayer.getConnection().close();
        run = false;
      } else {
        if (onlinePlayer.getConnection() != null) {
          onlinePlayer.getConnection().sendOutput(message);
        } else {
          System.err.println("No connection. Quitting the TUI, please restart.");
          run = false;
        }
      }
    }
  }

  protected void printMessage(String message) {
    System.out.println(message);
  }

  public static void main(String[] args) {
    ClientTUI playerTUI = new ClientTUI();
    playerTUI.runTUI();
  }

  @Override
  public String toString() {
    return
        "Renske's Player TUI commands:\n" +
            "   LOGIN~<username>............ print current state of GameServer \n" +
            "   QUEUE ...................... set board DIM for new Games \n" +
            "   MOVE~<int,int> of MOVE~<int> send a move \n" +
            "   MOVE........................ send a pass \n" +
            "   RESIGN ..................... resign from the Game \n" +
            "   PRINT ...................... print current state of Board \n" +
            "   DISCONNECT ..................disconnect and stop \n" +
            "   HELP ....................... help (this menu) \n";
  }
}