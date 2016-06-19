package pl.arturkb.server.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Class for a chat server that delivers public and private messages.
 */
public class MultiThreadChatServerSync {

    // The server socket.
    private static ServerSocket serverSocket;


    // Max connections that server will accept.
    private static final int MAX_CLIENT_COUNT = 10;

    // Logger;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // Array of client threads
    private static final ClientThread[] CLIENT_THREADS = new ClientThread[MAX_CLIENT_COUNT];

    /**
     * The main method.
     *
     * @param args the arguments to the program.
     */
    public static void main(String args[]) {

        int portNumber = ServerConstants.DEFAULT_PORT;

        if (args.length < 1) {
            LOGGER.info("Staring chat server on default port: " + portNumber);
        } else {
            LOGGER.info("Staring chat server on given port: " + args[0]);
            portNumber = Integer.parseInt(args[0]);
        }

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (NumberFormatException e) {
            LOGGER.severe("Given port number is not numeric. Stopping server");
            System.exit(1);
        } catch (IOException e) {
            LOGGER.severe("Can't bind socket to the given port " + e.getMessage());
            System.exit(1);
        }

        // Create a client socket for each connection and pass it to a new client thread.
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Accepted connection on socket " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort());
                int i;
                for (i = 0; i < MAX_CLIENT_COUNT; i++) {
                    if (CLIENT_THREADS[i] == null) {
                        CLIENT_THREADS[i] = new ClientThread(clientSocket, CLIENT_THREADS);
                        CLIENT_THREADS[i].start();
                        break;
                    }
                }
                if (i == MAX_CLIENT_COUNT) {
                    clientSocket.close();
                    LOGGER.info("Max connections that server will accept is reached, try later.");
                }
            } catch (IOException e) {
                LOGGER.severe("I/O error occurs when waiting for a connection " + e.getMessage());
            }
        }

    }
}
