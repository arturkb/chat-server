package pl.arturkb.server.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The chat client thread.
 */
public class ClientThread extends Thread {

    // Client threads
    private final ClientThread[] threads;

    // Client socket
    private final Socket clientSocket;

    // The maximum clients that is counted.
    private final int maxClientsCount;

    // Input stream
    private BufferedReader dataInputStream = null;

    // Output stream
    private PrintStream printStream = null;

    private String clientName;

    private String userName;

    // Logger;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    /**
     * The constructor
     *
     * @param clientSocket the socket
     * @param threads the array with chat threads.
     */
    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    /**
     * The main method for the thread that runs it.
     */
    public void run() {

        try {
            createInputAndOutputStreams();
            userName = receiveUserName();
            if (getUserName().equals(ServerConstants.DEFAULT_NAME)) {
                sendMessage(OutputTexts.WRONG_USER_NAME);
                LOGGER.info(OutputTexts.WRONG_USER_NAME);
            } else {
                sendMessage(OutputTexts.welcomeMessageForGivenUser(getUserName()));
                registerClientName(userName);
                broadcastMessage(OutputTexts.newUserEnteredChatMessage(getUserName()));
                mainLoop();
            }
            closeTheOutputInputSocket();
        } catch (IOException e) {
            LOGGER.severe("IOExceptions " + e.getMessage());
        }
    }

    /**
     * The main loop of chat.
     */
    private void mainLoop() throws IOException {
        boolean loop = true;

        while (loop) {
            long startTime = System.currentTimeMillis();
            Map<String, String> mapLine = parseLine(readMessage());
            String command = getCommand(mapLine);
            String clientName = getClientName(mapLine);
            String msg = getMsg(mapLine);

            switch (command) {

                case ServerConstants.WHO:
                    sendMessage(getListOfActiveUsers());
                    break;

                case ServerConstants.PRIVATE:
                    sendMessage(joinUserWithMessage(msg), clientName);
                    break;

                case ServerConstants.ALL:
                    broadcastMessage(joinUserWithMessage(msg));
                    break;

                case ServerConstants.HELP:
                    sendMessage(OutputTexts.printUsage());
                    break;

                case ServerConstants.QUIT:
                    broadcastMessage(OutputTexts.userIsLeavingTheChat(getUserName()));
                    unRegisterClient();
                    loop = false;
                    break;
            }
            long stopTime = System.currentTimeMillis();
            Long inactiveTime = (stopTime - startTime) / ServerConstants.MILLISECONDS_TO_SECONDS;
            if(inactiveTime >= ServerConstants.MAX_INACTIVE_SECONDS) {
                sendMessage(OutputTexts.printInactiveMessage(inactiveTime));
                broadcastMessage(OutputTexts.userIsLeavingTheChat(getUserName()));
                unRegisterClient();
                loop = false;
                LOGGER.info(OutputTexts.printInactiveInfoMessage(inactiveTime, getUserName()));
            }

        }
    }


    /**
     * Return a list of all active users in the system.
     *
     * @return the list as a string of all active users.
     */
    private String getListOfActiveUsers() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i].getUserName() != null) {
                stringBuilder.append(threads[i].getUserName()).append('\n');
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Join userName with message.
     *
     * @param message the message to be joined.
     * @return joined userName with the message.
     */
    private String joinUserWithMessage(String message) {
        return "<" + getUserName() + "> " +
                message;
    }

    /**
     * Gets command from the maoLine.
     *
     * @param mapLine the mapLine to get command from.
     * @return the command if exists, EMPTY command otherwise.
     */
    private String getCommand(Map<String, String> mapLine) {
        String command = mapLine.get(ServerConstants.COMMAND);
        return command == null ? ServerConstants.EMPTY : command;
    }

    private String getClientName(Map<String, String> mapLine) {
        String clientName = mapLine.get(ServerConstants.CLIENT_NAME);
        return clientName == null ? ServerConstants.EMPTY : clientName;
    }

    /**
     * Gets message if exists, empty string otherwise.
     *
     * @param mapLine the line map to get message from.
     * @return message if exists, empty string otherwise.
     */
    private String getMsg(Map<String, String> mapLine) {
        String command = mapLine.get(ServerConstants.MSG);
        return command == null ? ServerConstants.EMPTY : command;
    }

    /**
     * Pars line
     *
     * @param line the line to pars.
     * @return maps
     */
    public Map<String, String> parseLine(String line) {
        Map<String, String> result = new HashMap<>(3);

        if (line.startsWith(String.valueOf(ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN))) {
            String[] words = line.split("\\s", 2);
            if (words.length > 1 && words[1] != null && words[0] != null) {
                result.put(ServerConstants.COMMAND, ServerConstants.PRIVATE);
                result.put(ServerConstants.MSG, words[1]);
                result.put(ServerConstants.CLIENT_NAME, words[0]);
            }

        } else {
            String[] words = line.split("\\s", 2);
            if (words.length == 2) {
                result.put(ServerConstants.COMMAND, words[0]);
                result.put(ServerConstants.MSG, words[1]);
            } else if (words.length == 1) {
                result.put(ServerConstants.COMMAND, words[0]);
            }
        }
        return result;
    }

    /**
     * Create inout and output streams for the client.
     *
     * @throws IOException If an I/O error occurs
     */
    private void createInputAndOutputStreams() throws IOException {
        dataInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        printStream = new PrintStream(clientSocket.getOutputStream());
    }

    /**
     * Close the output, inout stream. Close the socket.
     *
     * @throws IOException IOException If an I/O error occurs
     */
    private void closeTheOutputInputSocket() throws IOException {
        dataInputStream.close();
        printStream.close();
        clientSocket.close();
    }

    /**
     * Register client name in the system.
     *
     * @param userName the user name to be registered.
     */
    public synchronized void registerClientName(String userName) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] == this) {
                clientName = ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN + userName;
                break;
            }

        }
    }

    /**
     * Unregister client from the system.
     */
    public synchronized void unRegisterClient() {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] == this) {
                threads[i] = null;
            }
        }
    }

    /**
     * Broadcast messages to all connected clients.
     *
     * @param message the message to broadcast.
     */
    private synchronized void broadcastMessage(String message) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] != this && threads[i].getClientName() != null) {
                threads[i].sendMessage(message);
            }
        }
    }

    /**
     * Getter for userName
     *
     * @return the user name;
     */
    private String getUserName() {
        return userName;
    }

    /**
     * Getter for client name;
     *
     * @return the client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Setter for client name.
     *
     * @param clientName the client name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


    /**
     * Send message to assigned client.
     *
     * @param message the message to send.
     */
    private void sendMessage(String message) {
        getPrintStream().println(message);
    }

    /**
     * Send the message to the clientName ane echo the message to sending client.
     *
     * @param message    the message to be send.
     * @param clientName the client to whom send the message.
     */
    private synchronized void sendMessage(String message, String clientName) {
        if (message == null || clientName == null) {
            return;
        }
        if (message.isEmpty() || clientName.isEmpty()) {
            return;
        }

        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] != this && threads[i].getClientName() != null && threads[i].getClientName().equals(clientName)) {
                threads[i].sendMessage(message);

                //Echo this message to let the client know the private message was sent
                sendMessage(message);
                break;
            }
        }
    }


    /**
     * Read the message from dataInoutStream.
     *
     * @return message
     * @throws IOException If an I/O error occurs
     */
    private String readMessage() throws IOException {
        return getDataInputStream().readLine();
    }


    /**
     * Gets user name;
     *
     * @return String with user name.
     * @throws IOException If an I/O error occurs
     */
    public String receiveUserName() throws IOException {
        String userName = ServerConstants.DEFAULT_NAME;
        int numberOfTries = 0;

        while (numberOfTries < ServerConstants.MAX_NUMBER_OF_TRIES) {
            numberOfTries++;
            sendMessage(OutputTexts.ENTER_YOUR_NAME);
            userName = readMessage();
            if (!userName.equals(ServerConstants.DEFAULT_NAME)) {
                userName = userName.trim();
            }
            if (userName.indexOf(ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN) != -1) {
                sendMessage(OutputTexts.getTheNameShouldNotContain(ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN));
                userName = ServerConstants.DEFAULT_NAME;
            } else {
                String clientName = ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN + userName;
                if (isClientNameRegistered(clientName)) {
                    sendMessage(OutputTexts.userNameIsAlreadyUsed(userName));
                    userName = ServerConstants.DEFAULT_NAME;
                } else {
                    return userName;
                }
            }


        }
        return userName;
    }

    /**
     * Check that given clientName is registered in the system.
     *
     * @param clientName the given clientName to check for.
     * @return true if given clientName is registered, false otherwise.
     */
    private synchronized boolean isClientNameRegistered(String clientName) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i].getClientName() != null && threads[i].getClientName().equals(clientName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Getter for printStream
     *
     * @return the printStream
     */
    private PrintStream getPrintStream() {
        return printStream;
    }

    /**
     * Setter for printStream
     *
     * @param printStream the printStream
     */
    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * Setter for dataInoutStream
     *
     * @param dataInputStream the dataInputStream
     */
    public void setDataInputStream(BufferedReader dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    /**
     * Getter for dataInputStream
     *
     * @return the dataInputStream
     */
    private BufferedReader getDataInputStream() {
        return dataInputStream;
    }

}
