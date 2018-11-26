import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChatClient - Project 4
 *
 * @author Muhammad Raziq Raif Ramli, mramli@purdue.edu
 * @author Amar Zarif Azamin, aazamin@purdue.edu
 * @version 11/26/2018
 */
final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        ChatClient client;
        // Get proper arguments and override defaults
        if (args.length == 3) {
            // Create your client and start it
            client = new ChatClient(args[2], Integer.parseInt(args[1]), args[0]);
        } else if (args.length == 2) {
            client = new ChatClient("localhost", Integer.parseInt(args[1]), args[0]);
        } else if (args.length == 1) {
            client = new ChatClient("localhost", 1500, args[0]);
        } else {
            client = new ChatClient("localhost", 1500, "Anonymous");
        }
        client.start();
        // Send an empty message to the server
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if (message.toLowerCase().equals("/logout")) {
                client.sendMessage(new ChatMessage(1, message));
                try {
                    client.socket.close();
                    client.sInput.close();
                    client.sOutput.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                System.exit(0);
//                break;
            } else if (message.toLowerCase().equals("/list")) {
//                client.sendMessage(new ChatMessage());

            } else if (message.length() > 3 && message.substring(0, 4).toLowerCase().equals("/msg")) {

                String recipient = message.substring(message.indexOf(" ") + 1, message.lastIndexOf(" "));

                String directMessage = message.substring(message.lastIndexOf(" ") + 1);
                client.sendMessage(new ChatMessage(2, directMessage, recipient));
            } else {
                client.sendMessage(new ChatMessage(0, message));
            }
//            System.out.println(client.socket.isConnected());
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                if (socket.isClosed()) {
                    System.out.println("socket closed");
                    return;
                }
                try {
                    String msg = ((ChatMessage) sInput.readObject()).getMessage();
                    System.out.print(msg);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
