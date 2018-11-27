import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
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
    private String username;
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
        do {
            try {
                socket = new Socket(server, port);
            } catch (ConnectException ce) {
                System.out.println("Waiting for connection at port " + port + "...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (socket == null);

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

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        t.start();

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (SocketException se) {
            System.out.println("Socket was closed. Exiting...");
            System.exit(0);
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
            if (!args[0].matches("\\w")) {
                System.out.println("You can only use alphanumeric characters for your username. Program exiting...");
                return;
            }
            client = new ChatClient(args[2], Integer.parseInt(args[1]), args[0]);
        } else if (args.length == 2) {
            if (!args[0].matches("\\w")) {
                System.out.println("You can only use alphanumeric characters for your username. Program exiting...");
                return;
            }
            client = new ChatClient("localhost", Integer.parseInt(args[1]), args[0]);
        } else if (args.length == 1) {
            if (!args[0].matches("\\w")) {
                System.out.println("You can only use alphanumeric characters for your username. Program exiting...");
                return;
            }
            client = new ChatClient("localhost", 1500, args[0]);
        } else {
            client = new ChatClient("localhost", 1500, "Anonymous_");
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
                client.sendMessage(new ChatMessage(2,message));

            } else if (message.length() > 3 && message.substring(0, 4).toLowerCase().equals("/msg")) {
                String[] splittedMessage = message.split(" ");
                String recipient = splittedMessage[1];

                if (recipient.equals(client.username)) {
                    System.out.println("You cannot send a direct message to yourself.");
                    continue;
                }
                if (splittedMessage.length == 2) {
                    System.out.println("You entered an empty text for a direct message.");
                    continue;
                }
                String directMessage = "";
                for (int i = 2; i < splittedMessage.length; i++) {
                    directMessage += splittedMessage[i] + " ";
                }
                client.sendMessage(new ChatMessage(3, directMessage, recipient));
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
            String testMessage = "";
            try {
                testMessage = ((ChatMessage) sInput.readObject()).getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
//            System.out.println(username);
//            System.out.println(testMessage);
            if (username.equals("Anonymous_"))
                username = "Anonymous" + testMessage;
//            System.out.println(username);
            else if (testMessage.equals("DuplicateUsername")) {
                try {
                    socket.close();
                    sInput.close();
                    sOutput.close();
                    System.out.println("You entered an existing username. Program exiting...");
                    System.exit(0);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
//            System.out.println(username);
            while (true) {
                if (socket.isClosed()) {
                    System.out.println("socket closed");
                    return;
                }
                try {
                    String msg = ((ChatMessage) sInput.readObject()).getMessage();
                    System.out.print(msg);
                } catch (SocketException se) {
                    break;
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
