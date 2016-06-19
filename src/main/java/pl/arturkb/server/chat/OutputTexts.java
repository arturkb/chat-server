package pl.arturkb.server.chat;

/**
 * Class that renders output texts:
 */
public class OutputTexts {

    public static final String ENTER_YOUR_NAME = "Enter your name." ;

    public static final String WRONG_USER_NAME = "Assigning user name unsuccessful. Closing connection.";

    public static String getTheNameShouldNotContain(char shouldNotContain) {
        StringBuilder text = new StringBuilder("The name should not contain ");
        text.append(shouldNotContain);
        text.append(" character");
        return text.toString();
    }
    
    public static String userNameIsAlreadyUsed(String userName) {
        StringBuilder text = new StringBuilder("The user name :");
        text.append(userName).append(" is already used.");
        return text.toString();
    }

    public static String welcomeMessageForGivenUser(String userName) {
        StringBuilder text = new StringBuilder("Welcome ");
        text.append(userName);
        text.append(" to our chat room.\nTo leave enter /quit in a new line.");
        return text.toString();
    }

    public static String newUserEnteredChatMessage(String userName) {
        StringBuilder text = new StringBuilder("*** A new user ");
        text.append(userName).append(" entered the chat room !!! ***");
        return text.toString();
    }

    public static String userIsLeavingTheChat(String userName) {
        StringBuilder text = new StringBuilder("*** The user ");
        text.append(userName).append(" is leaving the chat room !!! ***");
        return  text.toString();
    }

    public static String printUsage() {
        StringBuilder text = new StringBuilder("Usage:\n\n");
        text.append(ServerConstants.ALL).append(" to send message to all users.\n");
        text.append(ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN).append("user name").append(" to send message to given user. Private message\n");
        text.append(ServerConstants.QUIT).append(" to exit from chat\n");
        text.append(ServerConstants.HELP).append(" to print this help info\n");

        return text.toString();
    }

    public static String printInactiveMessage(Long seconds) {
        StringBuilder text = new StringBuilder("You have been inactive for :");
        text.append(seconds).append(" and maximum allowed is ");
        text.append(ServerConstants.MAX_INACTIVE_SECONDS).append(" seconds.\n");
        text.append("Closing connection to chat server");

        return text.toString();
    }

    public static String printInactiveInfoMessage(Long seconds, String userName) {
        StringBuilder text = new StringBuilder(userName);
        text.append(" have been inactive for :");
        text.append(seconds).append(" and maximum allowed is ");
        text.append(ServerConstants.MAX_INACTIVE_SECONDS).append(" seconds.\n");
        text.append("Closing connection to chat server");

        return text.toString();
    }

}
