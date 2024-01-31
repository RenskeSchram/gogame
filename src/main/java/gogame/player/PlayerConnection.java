package gogame.player;


import gogame.Protocol;
import gogame.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class PlayerConnection extends SocketConnection {

  OnlinePlayer player;
  private StrategyGame game;

  public PlayerConnection(Socket socket, OnlinePlayer player) throws IOException {
    super(socket);
    this.start();
    this.player = player;
  }

  @Override
  public void handleInput(String input) {
    String[] protocol = input.split(Protocol.SEPARATOR);

    switch (protocol[0]) {
      case Protocol.HELLO:
        handleHello(protocol);
        break;

      case Protocol.ACCEPTED:
        handleAccepted(protocol);
        break;

      case Protocol.REJECTED:
        handleRejected(protocol);
        break;

      case Protocol.QUEUED:
        handleQueued(protocol);
        break;

      case Protocol.GAMESTARTED:
        handleGameStarted(protocol);
        break;

      case Protocol.MOVE:
        handleMove(protocol);
        break;

      case Protocol.PASS:
        handlePass(protocol);
        break;

      case Protocol.MAKEMOVE:
        player.strategy.determineMove();
        break;

      case Protocol.GAMEOVER:
        handleGameOver(protocol);
        break;

      case Protocol.ERROR:
        System.err.println(String.format("%-20s", "[RECEIVED ERROR]") + protocol[1]);
        break;

      case Protocol.PRINT:
        player.receiveMessage(game.board.toString());
        break;

      default:
        handleDefault(protocol);
    }
  }

  private void handleHello(String[] protocol) {
    System.out.println(printProtocolMessage(protocol));
    player.sendUsername();

  }

  private void handleAccepted(String[] protocol) {
    if (protocol.length < 2) {
      sendError("did not receive username");
    } else {
      String acceptedMessage = "\nif you want to queue send: QUEUE";
      player.setUsername(protocol[1]);
      player.receiveMessage(printProtocolMessage(protocol) + acceptedMessage);
      player.sendQueue();
    }
  }

  private void handleRejected(String[] protocol) {
    if (protocol.length < 2) {
      sendError("did not receive username");
    } else {
      String rejectedMessage = "\nsend a new username using: LOGIN-<username>";
      player.receiveMessage(printProtocolMessage(protocol) + rejectedMessage);
      player.sendUsername();
    }
  }

  private void handleQueued(String[] protocol) {
    String queuedMessage = "\nif you want to leave the queue, send: QUEUE";
    player.receiveMessage(printProtocolMessage(protocol) + queuedMessage);
  }

  private void handleGameStarted(String[] protocol) {
    if (game == null) {
      if (protocol.length < 3) {
        sendError("could not handle GAME STARTED, too little inputs received");
      } else {
        player.receiveMessage(printProtocolMessage(protocol));
        initializeStrategyGame(protocol);
      }
    } else {
      sendError("could not handle START GAME, player is already in a game");
    }
  }
  private void handleGameOver(String[] protocol) {
    player.receiveMessage(printProtocolMessage(protocol));
    game.board.getFilledBoard();
    System.out.println(game.board.toString());
  }

  private void handleMove(String[] protocol) {
    if (protocol.length >= 3) {
      game.doMove(getLocationArray(protocol[1], game.board.getDIM()), getColor(protocol[2]));
    } else {
      sendError("could not handle MOVE, too little inputs received");
    }
  }

  private void handlePass(String[] protocol) {
    if (protocol.length >= 2) {
      game.doPass(getColor(protocol[1]));
    } else {
      sendError("could not handle PASS, no color received");
    }
  }

  private void handleDefault(String[] protocol) {
    sendError("could not handle the input using the set protocol");
    if (protocol.length >= 2) {
      System.err.println(String.format("%-20s", "[UNHANDLED MESSAGE]") + protocol[1]);
    } else {
      System.err.printf("%-20s%n", "[UNHANDLED MESSAGE]");

    }
  }

  private void initializeStrategyGame(String[] protocol) {
    int DIM = Integer.parseInt(protocol[2]);
    String[] playerNames = protocol[1].split(",");
    if (playerNames[0].equals(player.getUsername())) {
      game = new StrategyGame(player, new OnlinePlayer(), DIM);
      player.receiveMessage("Playing with BLACK");
    } else {
      game = new StrategyGame(new OnlinePlayer(), player, DIM);
      player.receiveMessage("Playing with WHITE");
    }

    player.strategy.setGame(game);
  }


  private String printProtocolMessage(String[] protocol) {
    StringBuilder message = new StringBuilder();
    for (String protocolSnippet : protocol) {
      message.append(protocolSnippet).append(" ");
    }
    return message.toString();
  }

  @Override
  protected void handleDisconnect() {
    System.out.println(String.format("%-20s", "[DISCONNECTED FROM SERVER]"));
  }
}

