package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Class that represents a custom login dialog extending JDialog and implementing ActionListener.
public class LoginDialog extends JDialog implements ActionListener {
    // Text field for entering player name.
    private JTextField playerNameField;

    // Text field for entering server IP address.
    private JTextField serverIpField;

    // Button to start the game.
    private JButton startButton;

    // Button to exit the application.
    private JButton exitButton;

    // Variable to store entered player name.
    private String playerName;

    // Variable to store entered server IP address.
    private String serverIP;

    // Constructor for the view.LoginDialog class.
    public LoginDialog(JFrame frame) {
        // Set the dialog as modal.
        super(frame, true);

        // Set the size.
        setSize(400, 200);

        // Dialog title.
        setTitle("Client Validation");

        // Set icon for the dialog.
        ImageIcon icon = new ImageIcon("src/assets/icon.png");
        setIconImage(icon.getImage());

        // Create components.
        playerNameField = new JTextField(null, 10);
        serverIpField = new JTextField("localhost", 10);
        startButton = new JButton("Start");
        exitButton = new JButton("Exit");

        // Add action listeners to buttons.
        startButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Create and configure layout using a GridLayout.
        JPanel messagePanel = new JPanel();
        // Add the panel to the dialog's content pane.
        getContentPane().add(messagePanel);
        // GridLayout with 0 rows and 2 columns.
        messagePanel.setLayout(new GridLayout(0, 2));

        // Add components to the panel.
        // Label for player name.
        JLabel playerLabel = new JLabel("PLAYER: ");
        // Label properties.
        Font font = new Font(Font.MONOSPACED, Font.PLAIN,  20);
        playerLabel.setFont(font);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerLabel.setForeground(Color.GREEN);
        messagePanel.add(playerLabel);

        // Player name text field.
        messagePanel.add(playerNameField);

        // Label for server IP address.
        JLabel serverLabel = new JLabel("SERVER IP: ");
        // Label properties.
        serverLabel.setFont(font);
        serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        serverLabel.setForeground(Color.GREEN);
        messagePanel.add(serverLabel);

        // Server IP address text field.
        messagePanel.add(serverIpField);

        // Start button.
        // Button properties.
        startButton.setBackground(Color.BLUE);
        startButton.setForeground(Color.WHITE);
        messagePanel.add(startButton);

        // Exit button.
        // Button properties.
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        messagePanel.add(exitButton);

        // Set background color.
        messagePanel.setBackground(Color.BLACK);

        // Configure dialog properties.
        // Set dialog location relative to the main frame.
        setLocationRelativeTo(frame);
        // Pack components to adjust the dialog's size to it.
       // pack();
        // Make the dialog visible.
        setVisible(true);
    }

    // ActionListener implementation for handling button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        // If the start button is clicked.
        if (e.getSource() == startButton) {
            // Get the entered player name.
            playerName = playerNameField.getText();
            // Get the entered server IP address.
            serverIP = serverIpField.getText();
            // Close the dialog.
            dispose();
        }

        // If the exit button is clicked
        if (e.getSource() == exitButton) {
            // Exit the application
            System.exit(1);
        }
    }

    // Getter method to retrieve the entered player name.
    public String getPlayerName() {
        return playerName;
    }

    // Getter method to retrieve the entered server IP address.
    public String getServerIpAddress() {
        return serverIP;
    }
}
