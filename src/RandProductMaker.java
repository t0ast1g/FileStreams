import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame {
    private static final int RECORD_SIZE = 232; // 6*2 + 35*2 + 75*2 + 8 = 232 bytes

    private JTextField idField, nameField, descriptionField, costField, recordCountField;
    private JButton addButton, clearButton, quitButton, returnToMainButton;
    private JLabel statusLabel;

    private RandomAccessFile randomFile;
    private int recordCount = 0;

    public RandProductMaker() {
        super("Random Access Product Maker");
        setupGUI();
        openFile();
    }

    private void setupGUI() {
        // Set up the main panel with a grid layout
        setSize(600, 400);
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

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        formPanel.add(new JLabel("Product ID (6 chars):"));
        idField = new JTextField(10);
        formPanel.add(idField);

        formPanel.add(new JLabel("Product Name (up to 35 chars):"));
        nameField = new JTextField(35);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description (up to 75 chars):"));
        descriptionField = new JTextField(75);
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Cost:"));
        costField = new JTextField(10);
        formPanel.add(costField);

        formPanel.add(new JLabel("Record Count:"));
        recordCountField = new JTextField("0");
        recordCountField.setEditable(false);
        formPanel.add(recordCountField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        addButton = new JButton("Add Record");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });
        buttonPanel.add(addButton);

        clearButton = new JButton("Clear Form");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
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

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    public void addReturnToMainButton(ActionListener listener) {
        returnToMainButton.addActionListener(listener);
    }

    private void openFile() {
        try {
            randomFile = new RandomAccessFile("products.dat", "rw");
            countExistingRecords();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error opening file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void countExistingRecords() {
        try {
            long fileLength = randomFile.length();
            recordCount = (int) (fileLength / RECORD_SIZE);
            recordCountField.setText(String.valueOf(recordCount));
        } catch (IOException e) {
            showStatus("Error counting records: " + e.getMessage());
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

    private void addRecord() {
        if (!validateFields()) {
            return;
        }

        try {
            String id = idField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            double cost = Double.parseDouble(costField.getText());

            Product product = new Product(id, name, description, cost);

            // Position at the end of the file
            randomFile.seek(randomFile.length());

            // Write the fixed-length record
            writeFixedLengthRecord(product);

            recordCount++;
            recordCountField.setText(String.valueOf(recordCount));

            showStatus("Record added successfully!");
            clearForm();
        } catch (NumberFormatException e) {
            showStatus("Invalid cost format!");
        } catch (IllegalArgumentException e) {
            showStatus("Error: " + e.getMessage());
        } catch (IOException e) {
            showStatus("Error writing to file: " + e.getMessage());
        }
    }

    private void writeFixedLengthRecord(Product product) throws IOException {
        // Write ID (6 chars)
        writeFixedString(product.getID(), 6);

        // Write name (35 chars)
        writeFixedString(product.getName(), 35);

        // Write description (75 chars)
        writeFixedString(product.getDescription(), 75);

        // Write cost (8 bytes)
        randomFile.writeDouble(product.getCost());
    }

    private void writeFixedString(String s, int length) throws IOException {
        StringBuilder sb = new StringBuilder(s);
        sb.setLength(length);
        randomFile.writeChars(sb.toString());
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                nameField.getText().trim().isEmpty() ||
                descriptionField.getText().trim().isEmpty() ||
                costField.getText().trim().isEmpty()) {

            showStatus("All fields are required!");
            return false;
        }

        if (idField.getText().length() != 6) {
            showStatus("ID must be exactly 6 characters!");
            return false;
        }

        if (nameField.getText().length() > 35) {
            showStatus("Name cannot exceed 35 characters!");
            return false;
        }

        if (descriptionField.getText().length() > 75) {
            showStatus("Description cannot exceed 75 characters!");
            return false;
        }

        try {
            double cost = Double.parseDouble(costField.getText());
            if (cost < 0) {
                showStatus("Cost cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showStatus("Invalid cost format!");
            return false;
        }

        return true;
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        costField.setText("");
        idField.requestFocus();
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RandProductMaker().setVisible(true);
            }
        });
    }
}