package gogame;

public class Protocol {
    public static final String HELLO = "HELLO";
    public static final String LOGIN = "LOGIN"; // <username>
    public static final String QUEUE = "QUEUE";

    public static final String MOVE = "MOVE"; // <N>, <X,Y>, <pass>
    public static final String ERROR = "ERROR"; // <error message>
    public static final String SEPARATOR = "~";

    public static final String ACCEPTED = "ACCEPTED"; // <username>
    public static final String REJECTED = "REJECTED"; // <username>
    public static final String QUEUED = "QUEUED";
    public static final String MAKEMOVE = "MAKE MOVE";
    public static final String GAMESTARTED = "GAME STARTED"; //<username, username>
    public static final String GAMEOVER = "GAME OVER"; //<message, username>

    public static final String PASS = "PASS" ;
    public static final String RESIGN = "RESIGN" ;

}
