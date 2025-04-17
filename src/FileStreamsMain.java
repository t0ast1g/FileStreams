import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main class for the FileStreams project.
 * Provides a central menu to launch the different applications.
 */
public class FileStreamsMain extends JFrame {

    private static FileStreamsMain mainFrame;

    public FileStreamsMain() {
        super("File Streams Application");
        setupGUI();
    }

    private void setupGUI() {
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("File Streams Application", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton makerButton = new JButton("Product Maker (Random Access)");
        makerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchProductMaker();
            }
        });

        JButton searchButton = new JButton("Product Search (Random Access)");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchProductSearch();
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonsPanel.add(makerButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(exitButton);

        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Credits
        JLabel creditsLabel = new JLabel("FileStreams Project - Java II", JLabel.CENTER);
        creditsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        mainPanel.add(creditsLabel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void launchProductMaker() {
        setVisible(false);

        RandProductMaker maker = new RandProductMaker();
        // Add return to main menu button
        maker.addReturnToMainButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maker.dispose();
                setVisible(true);
            }
        });

        maker.setVisible(true);
    }

    private void launchProductSearch() {
        setVisible(false);

        RandProductSearch search = new RandProductSearch();
        // Add return to main menu button
        search.addReturnToMainButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search.dispose();
                setVisible(true);
            }
        });

        search.setVisible(true);
    }

    /**
     * Main method to start the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainFrame = new FileStreamsMain();
                mainFrame.setVisible(true);
            }
        });
    }
}  