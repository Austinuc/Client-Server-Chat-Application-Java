package servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
//    private BufferedReader inputStream = null;
//    private DataOutputStream outputStream = null;

    public ChatServer(int portNUmber) throws IOException {

        try {
            serverSocket = new ServerSocket(portNUmber);
            System.out.println("Server started!");

            System.out.println("Waiting for a client...");

            while (true) {
                socket = serverSocket.accept();
                System.out.println("New Client connected.");
                System.out.println("---------------------");

                //Thread to handle client messages
                Thread client = new Thread(new CommunicationHandler(socket));

                client.start();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

//    public static void main(String[] args) throws  IOException {
//
//        ChatServer server = new ChatServer(8888);
//
//    }
}
