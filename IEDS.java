import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Base64;

public class EncryptionDecryptionSystem {

    private static String encryptMessage(String text, String key) {
        // Encrypt the key by adding 2 to each character's ASCII value
        StringBuilder encryptedKeyBuilder = new StringBuilder();
        for (char c : key.toCharArray()) {
            encryptedKeyBuilder.append((char) (c + 2));
        }
        String encryptedKey = encryptedKeyBuilder.toString();

        // Encrypt the message using XOR and the original key
        StringBuilder encryptedMessageBuilder = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            encryptedMessageBuilder.append((char) (text.charAt(index) ^ key.charAt(index % key.length())));
        }
        String encryptedMessage = encryptedMessageBuilder.toString();

        // Embed the encrypted key within the encrypted message, separated by a "-"
        return Base64.getEncoder().encodeToString((encryptedMessage + "-" + encryptedKey).getBytes());
    }

    private static String decryptMessage(String text, String key) {
        // Decode the base64 encoded string
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        String decodedBase64Message = new String(decodedBytes);

        // Separate the encrypted message and the encrypted key
        String[] parts = decodedBase64Message.split("-");
        if (parts.length != 2) {
            return null; // Invalid format
        }
        String encryptedMessage = parts[0];
        String encryptedKey = parts[1];

        // Decrypt the embedded key by subtracting 2 from each character's ASCII value
        StringBuilder decryptedKeyBuilder = new StringBuilder();
        for (char c : encryptedKey.toCharArray()) {
            decryptedKeyBuilder.append((char) (c - 2));
        }
        String decryptedKey = decryptedKeyBuilder.toString();

        // Compare the decrypted key with the provided key
        if (!decryptedKey.equals(key)) {
            return null; // Key mismatch
        }

        // Decrypt the message using XOR and the original key
        StringBuilder decryptedMessageBuilder = new StringBuilder();
        for (int i = 0; i < encryptedMessage.length(); i++) {
            decryptedMessageBuilder.append((char) (encryptedMessage.charAt(i) ^ key.charAt(i % key.length())));
        }
        return decryptedMessageBuilder.toString();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Encryption & Decryption");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Welcome text at the top
        JLabel welcomeLabel = new JLabel("Welcome", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Center panel with text fields and labels
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Message label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Message:"), gbc);
        JTextField messageField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(messageField, gbc);

        // Key label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("Key:"), gbc);
        JTextField keyField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(keyField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // 3 buttons with gaps
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton copyButton = new JButton("Copy");
        copyButton.setEnabled(false); // Initially disabled

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(copyButton);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST; // Align buttons to the right
        centerPanel.add(buttonPanel, gbc);

        // Label to show the encrypted/decrypted message
        JLabel resultLabel = new JLabel("", JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(resultLabel, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true); // TODO

        // Encryption button functionality
        encryptButton.addActionListener((_) -> {// Lambda function
            String text = messageField.getText();
            String key = keyField.getText();
            if (!text.isEmpty() && !key.isEmpty()) {
                String encrypted = encryptMessage(text, key);
                resultLabel.setText("Encrypted text: " + encrypted);
                copyButton.setEnabled(true);
            }
        });

        // Decryption button functionality
        decryptButton.addActionListener((_) -> {
            String encrypted = messageField.getText();
            String key = keyField.getText();
            if (!encrypted.isEmpty() && !key.isEmpty()) {
                String decrypted = decryptMessage(encrypted, key);
                if (decrypted == null) {
                    JOptionPane.showMessageDialog(frame, "Decryption failed! Incorrect key.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    resultLabel.setText("Decrypted text: " + decrypted);
                    copyButton.setEnabled(true);
                }
            }
        });

        // Copy button functionality
        copyButton.addActionListener((_) -> {
            StringSelection selection = new StringSelection(resultLabel.getText().replace("Encrypted text: ", "").replace("Decrypted text: ", ""));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
    }
}
