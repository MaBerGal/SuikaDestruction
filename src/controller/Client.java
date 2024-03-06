package controller;

import view.GamePanel;
import view.LoginDialog;

import javax.swing.*;
import java.awt.BorderLayout;

// The main entry point for the client application.
// I want to consider this a controller class since it helps establish a Client-Server connection by calling
// the different frames in order.
public class Client {

    // The main method that starts the client application.
    public static void main(String...args) {
        // Step 1: Create the main JFrame for the application.
        JFrame frame = createMainFrame();

        // Step 2: Display the login dialog and obtain server connection details and player name.
        showLoginFrame(frame);

        // Step 3: Display the main game frame with the initialized GamePanel.
        showMainFrame(frame);
    }

    // Creates the main JFrame for the application.
    private static JFrame createMainFrame() {

        JFrame frame = new JFrame();

        // Set JFrame properties.
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Set application icon.
        ImageIcon icon = new ImageIcon("src/assets/icon2.png");
        frame.setIconImage(icon.getImage());

        // Return the created JFrame.
        return frame;
    }

    // Displays the login dialog, obtains server connection details and player name,
    // and initializes the GamePanel with these details.
    private static void showLoginFrame(JFrame frame) {
        // Create a new LoginDialog attached to the main JFrame.
        LoginDialog loginDialog = new LoginDialog(frame);

        // Configure the layout of the main JFrame.
        frame.setLayout(new BorderLayout());

        // Add the initialized GamePanel to the center of the main JFrame.
        frame.add(new GamePanel(loginDialog.getServerIpAddress(), loginDialog.getPlayerName()), BorderLayout.CENTER);
    }

    // Displays the main game frame after login.
    private static void showMainFrame(JFrame frame) {
        // Set the title of the main JFrame.
        frame.setTitle("Suika Destruction");

        // Adjust the size of the main JFrame based on its contents.
        frame.pack();

        // Make the main JFrame visible to the user.
        frame.setVisible(true);
    }
}
