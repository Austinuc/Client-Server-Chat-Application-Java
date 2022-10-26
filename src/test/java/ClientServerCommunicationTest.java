import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientServerCommunicationTest {

    private static Socket clientSocket;
    private static InputStreamReader responseFromServer;
    private static PrintWriter sendMessageToServer;
    private static Socket serverSocket;
    private static final int PORT_NUMBER = 8854;

    @BeforeClass
    public static void init() throws IOException {
        ServerSocket sSocket = new ServerSocket(PORT_NUMBER);
        clientSocket = new Socket("localhost", PORT_NUMBER);
        responseFromServer = new InputStreamReader(clientSocket.getInputStream());

        serverSocket = sSocket.accept();
        OutputStream os = serverSocket.getOutputStream();
        sendMessageToServer = new PrintWriter(os, true);

    }

    @Test
    public void testSendingMessageToServerAndPrintingTheResponseFromServer() throws IOException {

        String[] pb = {"Hello Server!","Are you listening to me?","I want to speak with you"};

        for (String lines : pb) {
            System.out.println(lines);
            sendMessageToServer.write(lines);
        }
        sendMessageToServer.flush();
        System.out.println((responseFromServer));

//        assertEquals(responseFromServer,"Hello");

//        while (responseFromServer.available() > 0) {
//            System.out.println(Arrays.toString(responseFromServer.readAllBytes()));
//        }
    }

    @After
    public void cleanup() throws IOException {
        responseFromServer.close();
        sendMessageToServer.close();
        clientSocket.close();
        serverSocket.close();
    }
}
