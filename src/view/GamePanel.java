package view;

import model.Fire;
import model.Fruit;
import model.Player;
import model.Sprite;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;


// This class represents the main game panel where the gameplay occurs.
public class GamePanel extends JPanel implements Runnable {

    // The player's name.
    private String playerName;
    // Thread for game loop.
    private Thread thread;
    // List to store fruits.
    private LinkedList<Fruit> fruits;
    // List to store fire objects.
    private LinkedList<Fire> fires;
    // Local player object.
    private Player localPlayer;
    // Remote player object.
    private Player remotePlayer;
    // Total player score.
    private int score = 0;
    // Background image.
    private static BufferedImage backgroundImage;
    // Image for the purple player (local).
    private static BufferedImage purplePlayerImage;
    // Image for the blue player (remote).
    private static BufferedImage bluePlayerImage;
    // Array of fruit images.
    private static BufferedImage[] fruitImages = new BufferedImage[3];
    // Image for fire objects.
    private static BufferedImage fireImage;
    // Flag to indicate if the game is paused.
    private boolean isPaused = true;
    // Timer for fruit creation.
    private int fruitTimer = 0;
    // Frames per second.
    private float fps;
    // Initial x-coordinate for fruits.
    private int xiFruit = 20;
    // Current x-coordinate for fruits.
    private int xFruit;
    // Initial y-coordinate for fruits.
    private int yiFruit = 10;
    // Server IP address.
    private String serverIpAdress;
    // Server port number.
    private int PORT = 12345;
    // Socket for communication with the server.
    private Socket socket;
    // Object input stream for receiving server messages.
    private ObjectInputStream ois;
    // Object output stream for sending messages to the server.
    private ObjectOutputStream oos;

    // Constructor for the view.GamePanel class
    public GamePanel(String serverIpAdress, String playerName) {
        // Initialize member variables and call the resource loading, player positioning and connection methods.
        this.serverIpAdress = serverIpAdress;
        this.playerName = playerName;
        fruits = new LinkedList<>();
        fires = new LinkedList<>();
        setFocusable(true);
        setVisible(true);
        setPreferredSize(new Dimension(800, 600));
        addKeyboardListener();
        loadResources();
        setUpPlayers();
        fps = 60;
        connectToServer();
    }

    // Method to load image resources.
    private void loadResources() {
        backgroundImage = loadImage("background.png");
        purplePlayerImage = loadImage("player2.png");
        bluePlayerImage = loadImage("player1.png");
        fruitImages[0] = loadImage("suika2.png");
        fruitImages[1] = loadImage("suika3.png");
        fruitImages[2] = loadImage("suika4.png");
        fireImage = loadImage("fire1.png");
    }

    // Method to load a single image.
    private BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(getClass().getResourceAsStream("/assets/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to set up player objects on the screen.
    private void setUpPlayers() {
        localPlayer = new Player(new Point(), new Dimension(bluePlayerImage.getWidth(), bluePlayerImage.getHeight()), playerName);
        remotePlayer = new Player(new Point(), new Dimension(purplePlayerImage.getWidth(), purplePlayerImage.getHeight()), "Player2");
    }

    // Method to add an input listener for handling key events.
    private void addKeyboardListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }

    // Method to handle key pressed events. When the game isn't paused and the player is alive, allow movement.
    private void handleKeyPressed(KeyEvent e) {
        if (!isPaused && localPlayer.isAlive()) {
            handleGameKeys(e);
        }
    }

    // Method to handle key events during active gameplay.
    private void handleGameKeys(KeyEvent e) {
        // Move to the left.
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (localPlayer.getPosition().x > 0) {
                // Update the position.
                localPlayer.getPosition().x -= 10;
                sendMessage("xy:" + localPlayer.getPosition().x + "," + localPlayer.getPosition().y);
            }
        // Move to the right.
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (localPlayer.getPosition().x + localPlayer.getSize().width < getWidth()) {
                // Update the position.
                localPlayer.getPosition().x += 10;
                sendMessage("xy:" + localPlayer.getPosition().x + "," + localPlayer.getPosition().y);
            }
        // Open fire.
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Generate a new spawn point for a fire.
            Point firePoint = new Point(localPlayer.getPosition().x + localPlayer.getSize().width / 2,
                    localPlayer.getPosition().y);
            fire(firePoint);
            sendMessage("fire:" + firePoint.x + "," + firePoint.y);
        }
    }

    // Method to start the game.
    private void startGame() {
        // Set initial positions for players based on their IDs. Player 1 to the left, Player 2 to the right.
        if (localPlayer.getId() == 1) {
            localPlayer.setPosition(new Point(100, (int) this.getPreferredSize().getHeight() - localPlayer.getSize().height - 20));
            remotePlayer.setPosition(new Point((int) this.getPreferredSize().getWidth() - 100,
                    (int) this.getPreferredSize().getHeight() - remotePlayer.getSize().height - 20));
        } else {
            localPlayer.setPosition(new Point((int) this.getPreferredSize().getWidth() - 100,
                    (int) this.getPreferredSize().getHeight() - localPlayer.getSize().height - 20));
            remotePlayer.setPosition(new Point(100, (int) this.getPreferredSize().getHeight() - remotePlayer.getSize().height - 20));
        }

        // Game is no longer on hold since it has started.
        isPaused = false;

        // Start the game loop thread.
        if (thread == null) {
            thread = new Thread(this);
        }
        // Start it if's not already running.
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    // Method to create a new fruit and add it to the list.
    private void createFruit(Point pos, Point speed, int radius) {
        Dimension size = new Dimension(fruitImages[radius].getWidth(), fruitImages[radius].getHeight());
        fruits.addFirst(new Fruit(pos, speed, size, radius));
    }

    // Method to create a new bullet and add it to the list.
    private void fire(Point pos) {
        fires.add(new Fire(pos, new Dimension(fireImage.getWidth(), fireImage.getHeight()), localPlayer.getId()));
    }

    // Method to establish a connection to the server.
    private void connectToServer() {
        try {
            // Create a socket to connect to the specified server IP address and port.
            socket = new Socket(serverIpAdress, PORT);

            // Initialize object output and input streams for communication with the server.
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            // Start a separate thread to listen for messages from the server.
            startListeningServer();

            // Send an initial message to the server with the local player's ID.
            sendMessage("ok:" + localPlayer.getId());
        } catch (Exception e) {
            // Handle any exception that may occur during the connection process.
            handleConnectionError(e);
        }
    }

    // Method to start listening for messages from the server in a separate thread.
    private void startListeningServer() {
        new Thread(() -> {
            Object data;
            try {
                // Continuously listen for incoming messages from the server.
                while ((data = ois.readObject()) != null) {
                    // Process the received message.
                    messageReceived(data.toString());
                }
            } catch (Exception e) {
                // Handle any exception that may occur while listening for messages.
                handleConnectionError(e);
            }
        }).start();
    }

    // Method to process messages received from the server.
    private void messageReceived(String message) {
        // Debug.
        // System.out.println(message);

        // Split the message into parts based on the colon (:) separator.
        String[] parts = message.split(":");
        String[] subParts = null;

        // Switch statement to determine the type of message and take appropriate actions.
        switch (parts[0]) {
            case "id":
                // Set the local player's ID based on the received message.
                localPlayer.setId(Integer.parseInt(parts[1]));
                break;
            case "ok":
                // Set the remote player's ID based on the received message.
                remotePlayer.setId(Integer.parseInt(parts[1]));

                // If the game is paused, start the game and acknowledge the server.
                if (isPaused) {
                    startGame();
                    sendMessage("ok:" + localPlayer.getId());
                }
                break;
            case "xy":
                // Update the remote player's position based on the received message.
                subParts = parts[1].split(",");
                remotePlayer.setPosition(new Point(Integer.parseInt(subParts[0]),
                        Integer.parseInt(subParts[1])));
                break;
            case "fire":
                // Extract coordinates from the message and create a new fire at that position.
                subParts = parts[1].split(",");
                fire(new Point(Integer.parseInt(subParts[0]),
                        Integer.parseInt(subParts[1])));
                break;
            case "fruit":
                // Extract coordinates, speed, and radius from the message and create a new fruit.
                subParts = parts[1].split(",");
                createFruit(
                        new Point(Integer.parseInt(subParts[0]), Integer.parseInt(subParts[1])),
                        new Point(Integer.parseInt(subParts[2]), Integer.parseInt(subParts[3])),
                        Integer.parseInt(subParts[4])
                );
                break;
        }
    }

    // Method to send a message to the server.
    private void sendMessage(String message) {
        try {
            // Write the specified message to the object output stream for communication with the server.
            oos.writeObject(message);
        } catch (Exception e) {
            // Handle any exception that may occur while sending a message.
            e.printStackTrace();
        }
    }

    // Method to paint the game components on the panel.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw the background image.
        g2d.drawImage(backgroundImage, null, 0, 0);

        // Check if the game is paused and display the appropriate message.
        if (isPaused) {
            drawPausedMessage(g2d);
        } else {
            // Draw game objects and score if the game is active.
            drawGameObjects(g2d);
            drawScore(g2d);
        }
    }

    // Method to draw a message when the game is paused.
    private void drawPausedMessage(Graphics2D g2d) {
        // Set color and font for the paused message.
        g2d.setColor(Color.blue);
        g2d.setFont(new Font("Impact", Font.PLAIN, 36));

        // Draw the paused message at the center of the panel.
        String pausedMessage = "Waiting for a 2nd player";
        g2d.drawString(pausedMessage, getWidth() / 2 - 200, getHeight() / 2);
    }

    // Method to draw game objects on the panel.
    private void drawGameObjects(Graphics2D g2d) {
        // Draw fruits
        for (Fruit fruit : fruits) {
            g2d.drawImage(fruitImages[fruit.getRadius()], fruit.getPosition().x, fruit.getPosition().y, null);
        }

        // Draw fires.
        for (Fire fire : fires) {
            g2d.drawImage(fireImage, fire.getPosition().x, fire.getPosition().y, null);
        }

        // Draw the local player.
        if (localPlayer.isAlive()) {
            g2d.drawImage(bluePlayerImage, localPlayer.getPosition().x, localPlayer.getPosition().y, null);
        }

        // Draw the remote player.
        if (remotePlayer.isAlive()) {
            g2d.drawImage(purplePlayerImage, remotePlayer.getPosition().x, remotePlayer.getPosition().y, null);
        }
    }

    // Method to draw the score on the game panel.
    private void drawScore(Graphics2D g2d) {
        // Set color and font for total score.
        g2d.setColor(new Color(67, 255, 0));
        g2d.setFont(new Font("Impact", Font.PLAIN, 36));

        // Draw total score.
        g2d.drawString("Total Score: " + score, getWidth() - 250, 50);

        /* Set color and font for individual player scores.
        g2d.setColor(new Color(0, 119, 255));
        g2d.setFont(new Font("Impact", Font.PLAIN, 30));*/

        /* Draw Player 1 score
        g2d.drawString("Player 1 Score: " + localPlayer.getScore(), getWidth() - 250, 80);

        // Draw Player 2 score
        g2d.drawString("Player 2 Score: " + remotePlayer.getScore(), getWidth() - 250, 110);*/
    }

    // Override the run method of the Runnable interface.
    @Override
    public void run() {
        // Continue the game loop until it's paused.
        while (!isPaused) {
            try {
                // Pause the thread for the specified time to achieve the desired FPS.
                Thread.sleep((int) fps);

                // Update game objects and repaint the panel.
                updateGameObjects();
                repaint();
            } catch (Exception e) {
                // Handle any exceptions that may occur during the game loop.
                handleGameError(e);
            }
        }
    }

    private void updateGameObjects() {
        // Increment fruit timer.
        fruitTimer += fps;

        // Clone the lists of fruits and fires to avoid concurrent modification issues.
        LinkedList<Fruit> auxFruit = new LinkedList<>(fruits);
        LinkedList<Fire> auxFire = new LinkedList<>(fires);

        // Iterate through the fruits and update their positions.
        Iterator<Fruit> fruitIterator = auxFruit.iterator();
        while (fruitIterator.hasNext()) {
            Fruit a = fruitIterator.next();
            moveFruit(a);

            // Check for collisions with local player and update player's status.
            if (localPlayer.isAlive() && collisionDetection(a, localPlayer)) {
                localPlayer.setAlive(false);
            }

            // Check for collisions with remote player and update player's status.
            if (remotePlayer.isAlive() && collisionDetection(a, remotePlayer)) {
                remotePlayer.setAlive(false);
            }

            // Handle collisions between fires and fruits.
            handleBulletFruitCollision(auxFruit, auxFire, a);
        }

        // Update the non-auxiliary list with the auxiliary list.
        fruits = auxFruit;

        // Update positions of fires.
        updateBullets(auxFire);

        // Check if both players are dead and end the game if so.
        if (!localPlayer.isAlive() && !remotePlayer.isAlive()) {
            endGame();
        }
    }


    // Method to handle the end of the game.
    private void endGame() {
        // Calculate total score.
        int totalScore = localPlayer.getScore() + remotePlayer.getScore();
        //String winner;

        /* Determine the winner based on the scores
        if (localPlayer.getScore() > remotePlayer.getScore()) {
            winner = "Player 1 (" + localPlayer.getPlayerName() + ")";
        } else if (localPlayer.getScore() < remotePlayer.getScore()) {
            winner = "Player 2 (" + remotePlayer.getPlayerName() + ")";
        } else {
            winner = "It's a tie!";
        }*/

        // Display a dialog with game over message and total score.
        String message = "Game Over!\nTotal Score: " + totalScore;
                //+ "\nWinner: " + winner;
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // Exit the application with a status code.
        System.exit(2);

        /* Optionally, reset the game for a new round (I would need to implement a resetGame() method)
        // resetGame()*/
    }

    // Method to check for collisions between two sprites.
    private boolean collisionDetection(Sprite sprite1, Sprite sprite2) {
        // Create rectangles representing the sprites using their positions and sizes.
        Rectangle rectangle1 = new Rectangle(sprite1.getPosition().x, sprite1.getPosition().y,
                sprite1.getSize().width, sprite1.getSize().height);
        Rectangle rectangle2 = new Rectangle(sprite2.getPosition().x, sprite2.getPosition().y,
                sprite2.getSize().width, sprite2.getSize().height);

        // Check if the rectangles representing the sprites intersect, indicating a collision.
        return rectangle1.intersects(rectangle2);
    }

    // Method to update the position of a fruit in the game.
    private void moveFruit(Fruit fruit) {
        // Move the fruit down based on its speed.
        fruit.getPosition().y += fruit.getSpeed().y;

        // Remove the fruit if it goes beyond the panel height.
        if (fruit.getPosition().y > getHeight()) {
            fruits.remove(fruit);
        }
    }

    // Method to handle collisions between fires and fruits.
    private void handleBulletFruitCollision(LinkedList<Fruit> auxFruit,
                                            LinkedList<Fire> auxFire, Fruit fruit) {
        // Iterate through fires and check for collisions with the given fruit.
        for (Fire fire : fires) {
            if (collisionDetection(fruit, fire)) {
                // Remove the fruit and the bullet from their respective lists.
                auxFruit.remove(fruit);
                auxFire.remove(fire);

                // Increment the player's score and the total score.
                score += 10;

                // Determine which player fired the bullet.
                Player shootingPlayer = (fire.getShooterId() == localPlayer.getId()) ? localPlayer : remotePlayer;

                // Increment the score of the shooting player.
                shootingPlayer.incrementScore(10);
            }
        }
    }

    // Method to update the position of fires and remove fires that go off the screen.
    private void updateBullets(LinkedList<Fire> auxFire) {
        // Iterate through fires and update their positions.
        for (Fire fire : fires) {
            fire.getPosition().y -= fire.getSpeed().y;

            // Remove fires that go off the top of the panel.
            if (fire.getPosition().y < 0) {
                auxFire.remove(fire);
            }
        }
        // Update the fires list with the modified list.
        fires = auxFire;
    }

    // Method to handle connection errors with the server.
    private void handleConnectionError(Exception e) {
        System.out.println("Connection error!");
        // Print the stack trace for debugging purposes.
        e.printStackTrace();

        // Display an error message to the user.
        JOptionPane.showMessageDialog(this, "Error while connecting to the server: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Method to handle general game errors
    private void handleGameError(Exception e) {
        System.out.println("Game error! Probably a ConcurrentModificationException.");
        // Print the stack trace for debugging purposes.
        e.printStackTrace();

    }


}

