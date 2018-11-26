

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ChatServer - Project 4
 *
 * @author Muhammad Raziq Raif Ramli, mramli@purdue.edu
 * @author Amar Zarif Azamin, aazamin@purdue.edu
 * @version 11/26/2018
 */
final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;


    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server;
        if (args.length == 1)
            server =  new ChatServer(Integer.parseInt(args[0]));
        else
            server = new ChatServer(1500);
        server.start();
    }

    synchronized private void broadcast(String message) {
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        message = message + " - " + time.format(new Date());

        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).writeMessage(message + '\n');
        }
        System.out.println(message);
    }

    synchronized private void remove(int id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id == id)
                clients.remove(i);
        }
    }

    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean writeMessage(String msg) {
            if (!socket.isConnected()) {
                System.out.println("Not connected");
                return false;
            }
            try {
                sOutput.writeObject(new ChatMessage(0, msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //            System.out.println(username + ": Ping");
                if (cm.getType() == 0) {
                    broadcast(username + ": " + cm.getMessage());
                } else {
//                    broadcast(username + ": " + cm.getMessage());
                    remove(this.id);
                    close();
                    break;
                }
            }
        }

        private void close() {
            try {
                socket.close();
                sInput.close();
                sOutput.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
