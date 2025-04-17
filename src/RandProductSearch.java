import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {
    private static final int RECORD_SIZE = 232; // 6*2 + 35*2 + 75*2 + 8 = 232 bytes
    private static final int ID_LENGTH = 6;
    private static final int NAME_LENGTH = 35;
    private static final int DESCRIPTION_LENGTH = 75;

    private JTextField searchField;
    private JTextArea resultsArea;
    private JButton searchButton, clearButton, quitButton, returnToMainButton;

    private RandomAccessFile randomFile;

    public RandProductSearch() {
        super("Random Access Product Search");
        setupGUI();
        openFile();
    }

    private void setupGUI() {
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFile();
                dispose();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Enter partial product name:"), BorderLayout.WEST);
        searchField = new JTextField(20);
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Results area
        resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                resultsArea.setText("");
                searchField.requestFocus();
            }
        });
        buttonPanel.add(clearButton);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeFile();
                dispose();
            }
        });
        buttonPanel.add(quitButton);

        returnToMainButton = new JButton("Return to Main Menu");
        buttonPanel.add(returnToMainButton);

        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    public void addReturnToMainButton(ActionListener listener) {
        returnToMainButton.addActionListener(listener);
    }

    private void openFile() {
        try {
            randomFile = new RandomAccessFile("products.dat", "r");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error opening file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeFile() {
        try {
            if (randomFile != null) {
                randomFile.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error closing file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProducts() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search term",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultsArea.setText("");
        try {
            randomFile.seek(0);
            long fileLength = randomFile.length();
            int recordCount = (int) (fileLength / RECORD_SIZE);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < recordCount; i++) {
                // Position to the start of the record
                randomFile.seek(i * RECORD_SIZE);

                // Read the record
                String id = readFixedString(ID_LENGTH);
                String name = readFixedString(NAME_LENGTH);
                String description = readFixedString(DESCRIPTION_LENGTH);
                double cost = randomFile.readDouble();

                // Check if name contains search term
                if (name.toLowerCase().contains(searchTerm)) {
                    result.append("Record #").append(i + 1).append("\n");
                    result.append("ID: ").append(id.trim()).append("\n");
                    result.append("Name: ").append(name.trim()).append("\n");
                    result.append("Description: ").append(description.trim()).append("\n");
                    result.append("Cost: $").append(String.format("%.2f", cost)).append("\n\n");
                }
            }

            if (result.length() > 0) {
                resultsArea.setText(result.toString());
            } else {
                resultsArea.setText("No matching products found.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching products: " + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFixedString(int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(randomFile.readChar());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RandProductSearch().setVisible(true);
            }
        });
    }
}  