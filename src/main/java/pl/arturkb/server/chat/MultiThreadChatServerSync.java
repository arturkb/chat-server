package pl.arturkb.server.chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by artur on 6/13/16.
 */
public class MultiThreadChatServerSync {

    /**
     * The server socket.
     */
    private static ServerSocket serverSocket;


    /**
     * The client socket.
     */
    private static Socket clientSocket;

    /**
     * Max connections that server will accept.
     */
    private static final int MAX_CLIENT_COUNT = 10;

    /**
     * Classname for the logger;
     */
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * The main method.
     *
     * @param args
     */
    public static void main(String args[]) {

        int portNumber = 2222;

        if (args.length < 1) {
            LOGGER.info("Staring chat server on default port: " + portNumber);
        } else {
            LOGGER.info("Staring chat server on given port: " + args[0]);
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.severe("Given port number is not numeric. Stopping server");
                System.exit(1);
            }

        }


    }
}
