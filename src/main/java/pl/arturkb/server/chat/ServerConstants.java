package pl.arturkb.server.chat;

/**
 * Class that holds server side constants.
 */
public class ServerConstants {

    // The number of tries to assign name;
    static final int MAX_NUMBER_OF_TRIES = 10;

    static final char CHAR_THAT_NAME_CAN_NOT_CONTAIN = '@';

    // Default name for user.
    static final String DEFAULT_NAME = "NN";

    // Quit command
    static final String QUIT = "/quit";

    static final String ALL = "/all";

    static final String HELP = "/help";

    static final String WHO = "/who";

    static final String CLIENT_NAME = "user";

    static final String PRIVATE = "private";

    static final String EMPTY = "";

    static final String COMMAND = "command";

    static final String MSG = "msg";

    static final int MILLISECONDS_TO_SECONDS = 1000;

    static final int MAX_INACTIVE_SECONDS = 60;

    static final int DEFAULT_PORT = 2222;

}
