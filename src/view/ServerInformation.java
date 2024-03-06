// ServerInformation.java
package view;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerInformation extends JFrame {
    private JTextArea serverLog;

    public ServerInformation() {
        super("Suika Server Information");
        initComponents();
    }

    private void initComponents() {
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon("src/assets/icon.png");
        setIconImage(icon.getImage());

        serverLog = new JTextArea();
        serverLog.setLineWrap(true);
        serverLog.setEditable(false);

        // Set background color to black
        serverLog.setBackground(Color.BLACK);

        // Set text color to green
        serverLog.setForeground(Color.GREEN);

        // Set font to monospaced
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        serverLog.setFont(font);

        add(new JPanel(), BorderLayout.NORTH);  // Placeholder for potential additional components
        add(serverLog, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    public JTextArea getServerLog() {
        return serverLog;
    }

    public static void main(String[] args) {
        new ServerInformation();
    }
}
