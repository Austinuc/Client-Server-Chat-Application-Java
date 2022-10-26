package clients;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.*;

public class Client implements Runnable{
    private String clientName = null;
    private Socket socket = null;
    private Scanner inputStream = null;
    private PrintWriter outputStream = null;
    private BufferedReader response = null;
//    private BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(fileName)));
//    private OutputStream writer = new BufferedOutputStream(Files.newOutputStream(fileName2, CREATE, APPEND));

    public Client(String hostName, int portNumber, String clientName) throws IOException {

        this.clientName = clientName;

        try {
            socket = new Socket(hostName, portNumber);

            System.out.println(clientName+", you're now connected!");
            System.out.println("Just say Hello! and start chatting...");

            inputStream = new Scanner(System.in);
            outputStream = new PrintWriter(socket.getOutputStream());

            createLogBook(clientName);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        Path fileName = Paths.get(clientName);
//        BufferedReader readFromLog = new BufferedReader(new InputStreamReader(Files.newInputStream(fileName)));
//        OutputStream writeToLog = new BufferedOutputStream(Files.newOutputStream(fileName, TRUNCATE_EXISTING));
        try (BufferedReader readFromLog = new BufferedReader(new InputStreamReader(Files.newInputStream(fileName)));
        OutputStream writeToLog = new BufferedOutputStream(Files.newOutputStream(fileName, TRUNCATE_EXISTING))) {


            //Sender
            Thread sender = new Thread(() -> {
                String line = "";
                while (true) {

                    line = inputStream.nextLine();
                    if (line.equalsIgnoreCase("bye")) {
                        inputStream.close();
                        outputStream.close();
                        System.exit(-1);
                        Thread.currentThread().interrupt();
                    }
                    outputStream.println(clientName+"-"+line+"\n");
                    outputStream.flush();
                }
            });

            try {
                response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Receiver
            Thread receiver = new Thread(() -> {
                String line = "";
                while (true) {

                    try {
                        line = response.readLine();
                        if (line == null)
                            break;

                        if (line.charAt(0) != '?' && line.charAt(0) != '>') {
                            System.out.println(line);
                            line +="\n";
                            writeToLog.write(line.getBytes(StandardCharsets.UTF_8));
                            writeToLog.flush();
                        } else
                            System.out.println(line);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });

            sender.start();
            receiver.start();

            try {
                //Wait until sender & receiver thread is done
                sender.join();
                receiver.join();
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {

        }
    }

    public void createLogBook(String clientName) {
        Path fileName2 = Paths.get(clientName.toLowerCase());
        try {
            OutputStream createLog = new BufferedOutputStream(Files.newOutputStream(fileName2, CREATE, TRUNCATE_EXISTING));
            createLog.close();
//
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) throws IOException {
//
//        System.out.print("Pls, enter your name here: ");
//        Scanner in = new Scanner(System.in);
//        String userName = in.nextLine();
//        in.reset();
//
//        Thread cl = new Thread(new Client("localhost", 8888, userName));
//        cl.start();
//
//    }
}
