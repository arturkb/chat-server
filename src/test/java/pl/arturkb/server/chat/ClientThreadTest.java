package pl.arturkb.server.chat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Junit test class.
 */
public class ClientThreadTest {


    private Socket clientSocketMocked;
    private ClientThread[] threadsMocked;
    private PrintStream printStream;
    private BufferedReader bufferedReader;

    @Before
    public void setUp() {
        threadsMocked = new ClientThread[10];
        clientSocketMocked = mock(Socket.class);
        printStream = mock(PrintStream.class);
        bufferedReader = mock(BufferedReader.class);
    }

    @Test
    public void testReceiveUserNameWithCharThatIsNotAllowed() throws IOException {
        String stringWithCharThaIsNoAllowed = "Artur@Home";
        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);

        when(bufferedReader.readLine()).thenReturn(stringWithCharThaIsNoAllowed);

        classUnderTest.setPrintStream(printStream);
        classUnderTest.setDataInputStream(bufferedReader);
        String actual = classUnderTest.receiveUserName();
        Assert.assertEquals("The name should be " + ServerConstants.DEFAULT_NAME, ServerConstants.DEFAULT_NAME, actual);

    }

    @Test
    public void testReceiveUserNameWithCharThatIsAllowed() throws IOException {
        String stringWithoutCharThaIsNoAllowed = "Artur";
        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);

        when(bufferedReader.readLine()).thenReturn(stringWithoutCharThaIsNoAllowed);

        classUnderTest.setPrintStream(printStream);
        classUnderTest.setDataInputStream(bufferedReader);
        String actual = classUnderTest.receiveUserName();
        Assert.assertEquals("The name should be " + stringWithoutCharThaIsNoAllowed, stringWithoutCharThaIsNoAllowed, actual);
    }

    @Test
    public void testReceiveUserNameTrimmed() throws IOException {
        String stringWithoutCharThaiIsNoAllowedNotTrimmed = " Artur ";
        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);

        when(bufferedReader.readLine()).thenReturn(stringWithoutCharThaiIsNoAllowedNotTrimmed);

        classUnderTest.setPrintStream(printStream);
        classUnderTest.setDataInputStream(bufferedReader);
        String actual = classUnderTest.receiveUserName();
        Assert.assertEquals("The name should be " + stringWithoutCharThaiIsNoAllowedNotTrimmed.trim(), stringWithoutCharThaiIsNoAllowedNotTrimmed.trim(), actual);
    }

    @Test
    public void testReceiveUserNameThatIsAlreadyUsed() throws IOException {
        String stringWithCharThaIsAllowed = "Artur";

        ClientThread classFixture = new ClientThread(clientSocketMocked, threadsMocked);
        classFixture.setClientName(ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN + stringWithCharThaIsAllowed);
        threadsMocked[0] = classFixture;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);

        when(bufferedReader.readLine()).thenReturn(stringWithCharThaIsAllowed);

        classUnderTest.setPrintStream(printStream);
        classUnderTest.setDataInputStream(bufferedReader);
        String actual = classUnderTest.receiveUserName();
        Assert.assertEquals(ServerConstants.DEFAULT_NAME, ServerConstants.DEFAULT_NAME, actual);
    }


    @Test
    public void testRegisterClientName() {
        String clientName1 = "Artur";
        String clientName2 = "Carol";

        ClientThread classUnderTest1 = new ClientThread(clientSocketMocked, threadsMocked);
        ClientThread classUnderTest2 = new ClientThread(clientSocketMocked, threadsMocked);

        threadsMocked[0] = classUnderTest1;
        threadsMocked[1] = classUnderTest2;
        classUnderTest1.registerClientName(clientName1);
        classUnderTest2.registerClientName(clientName2);

        Assert.assertEquals(threadsMocked[0].getClientName(), ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN + clientName1);
        Assert.assertEquals(threadsMocked[1].getClientName(), ServerConstants.CHAR_THAT_NAME_CAN_NOT_CONTAIN + clientName2);
    }


    @Test
    public void testUnRegisterClient() {

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        threadsMocked[0] = classUnderTest;

        classUnderTest.unRegisterClient();
        Assert.assertEquals(threadsMocked[0], null);
    }

    @Test
    public void testParseLineHappyDay() {
        String commandWithMessage = "/all this is test message";
        Map<String, String> result;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        result = classUnderTest.parseLine(commandWithMessage);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("/all", result.get(ServerConstants.COMMAND));
        Assert.assertEquals("this is test message", result.get(ServerConstants.MSG));
    }

    @Test
    public void testParseLineWithPrivateMessageHappyDay() {
        String commandWithMessage = "@Artur this is test message";
        Map<String, String> result;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        result = classUnderTest.parseLine(commandWithMessage);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(ServerConstants.PRIVATE, result.get(ServerConstants.COMMAND));
        Assert.assertEquals("@Artur", result.get(ServerConstants.CLIENT_NAME));
        Assert.assertEquals("this is test message", result.get(ServerConstants.MSG));
    }

    @Test
    public void testParseLineWithEmptyPrivateMes() {
        String commandWithOutMessage = "@Artur";
        Map<String, String> result;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        result = classUnderTest.parseLine(commandWithOutMessage);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testParseLineWithEmptyMsg() {
        String commandWithMessage = "/all";
        Map<String, String> result;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        result = classUnderTest.parseLine(commandWithMessage);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("/all", result.get(ServerConstants.COMMAND));
    }

    @Test
    public void testParseEmptyLine() {
        String commandWithMessage = ServerConstants.EMPTY;
        Map<String, String> result;

        ClientThread classUnderTest = new ClientThread(clientSocketMocked, threadsMocked);
        result = classUnderTest.parseLine(commandWithMessage);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.get(ServerConstants.COMMAND), ServerConstants.EMPTY);
    }
}
