import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.io.*;
import java.net.*;

public class ClientServerCommunicationTest {

    private static Socket clientSocket;
    private static BufferedReader responseFromServer;
    private static PrintWriter sendMessageToServer;
    private static Socket serverSocket;
    private static final int PORT_NUMBER = 8854;
    String[] messageToServer;


    @Before
    public void init() throws IOException {
        messageToServer = new String[]{"Hello Server!", "Are you listening to me?", "I want to speak with you"};

        ServerSocket sSocket = new ServerSocket(PORT_NUMBER);
        clientSocket = new Socket("localhost", PORT_NUMBER);
        responseFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        serverSocket = sSocket.accept();
        OutputStream os = serverSocket.getOutputStream();
        sendMessageToServer = new PrintWriter(os, true);

    }

    @Test
    public void testSendingMessageToServerAndPrintingTheResponseFromServer() throws IOException {
        for (String lines : messageToServer) {
            sendMessageToServer.println(lines);
        }
        sendMessageToServer.flush();

        String response = "";
        int lineNumbers = 0;
        for (; lineNumbers < messageToServer.length; lineNumbers++) {
            response = responseFromServer.readLine();

            //validate that each response from the server corresponds to the input line
            assertThat(response, is(equalTo(messageToServer[lineNumbers])));

            System.out.println(response);
        }
        assertThat(lineNumbers,is(greaterThan(messageToServer.length)));
    }

    @After
    public void cleanup() throws IOException {
        responseFromServer.close();
        sendMessageToServer.close();
        clientSocket.close();
        serverSocket.close();
    }
}
