# Client-Server Chat Application (Java Socket Programming)

This is a simple interactive, multithreaded Client-Server chat application developed using Java. The application allows multiple clients to connect 
to a server and send messages to each other in real-time.

## Prerequisites
Java 8 or later must be installed on your system.

## Running the application

* Clone this repo to your local machine and open the project in your favourite IDE or simply navigate to the project folder from command line.
* Start the server by running the Server.java file and follow the prompt.
* Start the client by running the Client.java file. You can start as many clients as you want.
Enter the same port number of the server when prompted on the client side.

Once connected to the server, you can start sending messages to other connected clients.

### Note

This application is meant for use on a local network. It may not work properly over the internet.
If you change the server IP and port number, you will also have to update the corresponding fields in the Client.java file.
The project use java.io and java.net package for the socket connection

### Limitation

* Not Encrypted. 
* No Authentication

### Example

Run the Server file and run the Client.java file on two or more separate terminals.
>> This is the message format: `<receiver name> : <your message>`
> Type the recipient name first, followed by colon ':' then your message.
> `Austin: How are you doing today?`

You can chat with any other connected client privately.