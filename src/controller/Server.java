
package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.ServerInformation;

// The Server class responsible for managing connections and coordinating the game.
public class Server {

    // ServerSocket for handling client connections.
    private ServerSocket server = null;

    // Socket for individual client connections.
    private Socket socket;

    // Port number for the server.
    private final int PORT = 12345;

    // Maximum number of connected users allowed.
    private final int MAX_CONNECTED_USERS = 2;

    // GUI component for displaying server information.
    private ServerInformation serverInformation;

    // HashMap to store ObjectOutputStreams associated with client IDs.
    private HashMap<Integer, ObjectOutputStream> objectList;

    // Constructor for the Server class.
    public Server() {
        // Initialize server information GUI and components.
        serverInformation = new ServerInformation();

        // Start the server and wait for connections.
        startServer();
    }

    // Method to start the server and handle client connections.
    private void startServer() {
        try {
            // Initialize objectList to store client ObjectOutputStreams.
            objectList = new HashMap<>();

            // Log server start-up information to the server information window.
            serverInformation.getServerLog().append("Server has started.\n");
            serverInformation.getServerLog().append("Awaiting connections.\n");

            // Create ServerSocket and accept connections from clients.
            server = new ServerSocket(PORT);

            // For each user, accept the connection and get the host's name.
            for (int i = 1; i <= MAX_CONNECTED_USERS; i++) {
                socket = server.accept();
                String name = socket.getInetAddress().getHostName();
                serverInformation.getServerLog().append("Player " + i + " has just connected on " + name + ".\n");

                // Create ObjectOutputStream for the client and send player ID.
                objectList.put(i, new ObjectOutputStream(socket.getOutputStream()));
                objectList.get(i).writeObject("id:" + i);

                // Start a separate thread for each connected client.
                new ServerThread(i).start();
            }

            // Initialize and schedule game-related tasks.
            new GameTasks();

            // Log maximum connected players reached
            serverInformation.getServerLog().append("Maximum connected players reached.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class representing a server thread for individual clients.
    private class ServerThread extends Thread {
        // The number representing the player.
        private int id;

        // Constructor for the ServerThread.
        public ServerThread(int id) {
            this.id = id;
        }

        // Run method for the server thread.
        @Override
        public void run() {
            Object data;
            try {
                // Create ObjectInputStream for reading client data.
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                // Listen for client messages and broadcast to other clients.
                while ((data = input.readObject()) != null) {
                    for (int i : objectList.keySet()) {
                        if (i != id && objectList.containsKey(i)) {
                            objectList.get(i).writeObject(data);
                            objectList.get(i).flush();
                        }
                    }
                }

                // Clean up resources when a client disconnects.
                input.reset();
                input.close();
            } catch (Exception ex) {
                // When any exception is caught and the client disconnects, print information to the
                // server information window.
                serverInformation.getServerLog().append("Player " + id + " has disconnected.\n");
                disconnect();
            }
        }

        // Method to handle disconnection of a client.
        private void disconnect() {
            try {
                // Close ObjectOutputStream for the disconnected client.
                objectList.get(id).close();
            } catch (IOException ex) {
                // Print the information to the server information window.
                serverInformation.getServerLog().append(ex.getMessage() + "\n");
            }

            // Remove the disconnected client from the objectList.
            objectList.remove(id);
        }
    }

    // Inner class representing game-related tasks.
    private class GameTasks {
        // Random object for generating random X coordinates.
        private Random random = new Random();

        // Constants for task timing fruit creation.
        private final int defaultDelay = 2000;
        private final int fruitInterval = 600;

        // Counter and speed variables for controlling fruit creation.
        private int fruitTimeCounter = 0;
        private int fruitSpeedY = 5;

        // Constructor for GameTasks.
        public GameTasks() {
            // Initialize and schedule a task for creating fruits on a Timer object, set with the constants.
            Timer fruitTimer = new Timer("FruitTask");
            fruitTimer.scheduleAtFixedRate(createFruitTask(), fruitInterval, defaultDelay);
        }

        // Method to create and send fruits to connected clients.
        private TimerTask createFruitTask() {
            return new TimerTask() {
                @Override
                public void run() {
                    // Update the fruit time counter.
                    fruitTimeCounter += fruitInterval;

                    // Increase fruit speed at intervals.
                    if (fruitTimeCounter >= 3000) {
                        fruitSpeedY++;
                        fruitTimeCounter = 0;
                    }

                    // Format and send fruit creation command to all clients.
                    String command = "fruit:%d,%d,%d,%d,%d";
                    // Random X coordinate.
                    int x = random.nextInt(760) + 20;
                    // Random radius (fruit) between 3 options.
                    int radius = random.nextInt(3);

                    // Iterate through connected clients and send fruit creation command.
                    for (int i : objectList.keySet()) {
                        try {
                            // Send the fruit creation command to the client.
                            objectList.get(i).writeObject(String.format(command, x, 10, 0, fruitSpeedY, radius));
                            objectList.get(i).flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
        }
    }


    // The main method to start the server
    public static void main(String args[]) {
        new Server();
    }
}
