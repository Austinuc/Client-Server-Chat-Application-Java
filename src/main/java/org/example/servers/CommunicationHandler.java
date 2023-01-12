package org.example.servers;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
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
    private Socket clientSocket;

    private CommunicationHandler(String name, boolean loggedInStatus) {
        this.name = name;
        this.loggedInStatus = loggedInStatus;
    }

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

        System.out.println(name +" has disconnected. Was handled by "+Thread.currentThread().getName() + " --"+LocalDateTime.now());
        loggedInClients.remove(this);

        try {
            clientSocket.close();
            clientInputStream.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensures that message was sent in the correct format
     * @param messageContent: sender message token
     * @param sender: current instance of this thread
     * @return the message sent by a sender
     */
    public static String validateMessageContent(StringTokenizer messageContent, CommunicationHandler sender) {
        String messageToReceiver = "";

        if (messageContent.hasMoreElements()) {
            //Get the message for the receiver
            messageToReceiver = messageContent.nextToken();
        } else {
            System.out.println(sender.getName()+" sent a message without a receiver attached. --" + LocalDateTime.now());
            sender.getClientOutputStream().println("?-> Message format error: <receiver name> : <your message>");
            sender.getClientOutputStream().println("?-> Current logged in users: "+ ((loggedInClients.size() == 1) ? "You're the only one online. You can run multiple clients": loggedInClients));
            sender.getClientOutputStream().println("?-> Type 'bye' to exit");
            sender.getClientOutputStream().flush();
            return null;
        }
        return messageToReceiver;
    }

    public static void sendMessageToRecipient(CommunicationHandler receiver, String messageToReceiver, CommunicationHandler sender) {

        if (!receiver.loggedInStatus) {
            System.out.println("Recipient not online! "+sender.getName() + " attempted to send message to " + receiver.getName() +": --"+ LocalDateTime.now());
            sender.getClientOutputStream().println("?-> "+receiver.getName()+" is not currently online. Please list of current online users below");
            sender.getClientOutputStream().println("?-> Current logged in users: "+ ((loggedInClients.size() == 1) ? "You're the only one online. You can run multiple clients": loggedInClients));
            sender.getClientOutputStream().flush();
            return;
        }
        receiver.clientOutputStream.println("From "+ sender.getName()+ ": " + messageToReceiver);
        receiver.clientOutputStream.flush();

        sender.clientOutputStream.println(">> Your message was sent successfully to " + receiver.getName());
        sender.clientOutputStream.flush();

        System.out.println("message delivered to " + receiver.getName());
    }

    /**
     * Scans through a list of users currently logged in or active on chat
     * @param recipient: name of the receiver
     * @return a handler for the recipient
     */
    public static CommunicationHandler getTheReceiverObject(String recipient) {
        CommunicationHandler receiver = new CommunicationHandler(recipient, false);
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
            sender.clientOutputStream.println("?-> Current logged in users: "+ ((loggedInClients.size() == 1) ? "You're the only one online. You can run multiple clients": loggedInClients));
            sender.clientOutputStream.println("?-> Type 'bye' to exit");
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
