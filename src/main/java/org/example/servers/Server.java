package org.example.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Server {

    private final int portNumber;

    public Server(int portNUmber) throws IOException {
        this.portNumber = portNUmber;
    }

    public void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started!");

            System.out.println("Waiting for a client to connect...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New Client connected." + LocalDateTime.now());
                System.out.println("---------------------");

                //Thread to handle client messages
                Thread client = new Thread(new CommunicationHandler(socket));

                client.start();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws  IOException {
        System.out.print("Pls, enter a port number for this server or press ENTER to use the default port '8888': ");
        Scanner in = new Scanner(System.in);
        String portNumber = in.nextLine();
        in.reset();

        int port = 8888;
        try {
            port = Integer.parseInt(portNumber);
        } catch (Exception ignore) {
        }
        System.out.println("You can now connect to this server via this port "+ port+"\n");
        Server server = new Server(port);
        server.startServer();

    }
}
