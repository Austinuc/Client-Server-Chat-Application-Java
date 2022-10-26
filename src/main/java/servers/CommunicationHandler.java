package servers;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

@Setter
@Getter
public class CommunicationHandler implements Runnable {

    private int messageCount = 0;
    public static Vector<CommunicationHandler> loggedInClients = new Vector<>();

    private boolean loggedInStatus = true;
    private String name = null;
    public BufferedReader clientInputStream = null;
    public PrintWriter clientOutputStream = null;
    private final Socket clientSocket;

    public CommunicationHandler(Socket clientSocket) {
        this.name = "Client";
        this.clientSocket = clientSocket;
        loggedInClients.add(this);

        try {
            this.clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientOutputStream = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {

        String inputMessageFromClient = "", recipientName = "", messageToReceiver = "";

        while (true) {
            try {
                inputMessageFromClient = clientInputStream.readLine();
                if (inputMessageFromClient == null) {
                    loggedInStatus = false;
                    break;
                }
                // Split message into tokens
                StringTokenizer messageContent = tokenizeMessageFromSender(this, inputMessageFromClient);

                if (messageContent == null) {
                    continue;
                }
                //Get sender message
                StringTokenizer messageTokens = new StringTokenizer(messageContent.nextToken(), "#:");

                //Get receiver name
                recipientName = messageTokens.nextToken().trim();

                if ((messageToReceiver = validateMessageContent(messageTokens,this)) == null) {
                    continue;
                }

                //Get the receiver object
                CommunicationHandler receiver = getTheReceiverObject(recipientName);

                //And send message to the receiver
                sendMessageToRecipient(receiver,messageToReceiver, this);


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchElementException ignored) {

            }
        }

        System.out.println(name +" has disconnected. Was handled by "+Thread.currentThread().getName());
        loggedInClients.remove(this);

        try {
            clientSocket.close();
            clientInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String validateMessageContent(StringTokenizer messageContent, CommunicationHandler sender) {
        String messageToReceiver = "";

        if (messageContent.hasMoreElements()) {
            //Get the message for the receiver
            messageToReceiver = messageContent.nextToken();
        } else {
            System.out.println(sender.getName()+" sent a message without a receiver attached");
            sender.getClientOutputStream().println("?-> Message format error: <receiver name> : <your message>");
            sender.getClientOutputStream().println("?-> Current logged in users: "+ loggedInClients);
            sender.getClientOutputStream().flush();
            return null;
        }
        return messageToReceiver;
    }

    public static void sendMessageToRecipient(CommunicationHandler receiver, String messageToReceiver, CommunicationHandler sender) {

        if (receiver == null) {
            System.out.println(receiver.getName()+" is unknown. Please check and try again");
            sender.getClientOutputStream().println("?-> "+receiver.getName()+" is unknown. Please check and try again");
            sender.getClientOutputStream().flush();
            return;
        }
        receiver.clientOutputStream.println("From "+ sender.getName()+ ": " + messageToReceiver);
        receiver.clientOutputStream.flush();

        sender.clientOutputStream.println(">> Your message was sent successfully to " + receiver.getName());
        sender.clientOutputStream.flush();

        System.out.println("message delivered to " + receiver.getName());
    }

    public static CommunicationHandler getTheReceiverObject(String recipient) {
        CommunicationHandler receiver = null;
        for (CommunicationHandler e : loggedInClients) {
            if (e.name.equalsIgnoreCase(recipient)) {
                receiver = e;
                break;
            }
        }
        return receiver;
    }

    public static StringTokenizer tokenizeMessageFromSender(CommunicationHandler sender, String inputMessageFromClient) {
        //Get sender name
        StringTokenizer senderMessage = new StringTokenizer(inputMessageFromClient, "-");
        sender.name = senderMessage.nextToken().trim();

        if (sender.messageCount++ == 0) {
            System.out.println(sender.name + " is ready for chat");
            sender.clientOutputStream.println(">> This is the message format: <receiver name> : <your message>");
            sender.getClientOutputStream().println("?-> Current logged in users: "+ loggedInClients);
            sender.clientOutputStream.flush();
            return null;
        }
        return senderMessage;

    }

    @Override
    public String toString() {
        return name;
    }
}
