package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

// The class which is opened when Settings button is pressed in GUI
// The class is used by App.java
public class Settings {
    private JTextField userIDtextField;
    private JTextField apiTokenTextField;
    public JPanel settingsPanel;
    private JLabel userIDLabel;
    private JLabel apiTokenLabel;
    private JButton saveButton;
    private JTextField uploadPathTextField;
    private JButton browseButton;
    private JTextField imgbbApiTokenTextField;
    private JFileChooser fc;

    public Settings(final Connection connection, final JFrame frame) throws SQLException {
        uploadPathTextField.setEditable(false); // Disables uploadPathTextField editing

        // Get Upload path, User ID, API Token and imgbb API Token from database
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM settings WHERE id = 0");
        uploadPathTextField.setText(rs.getString("path"));
        userIDtextField.setText(rs.getString("user_id"));
        apiTokenTextField.setText(rs.getString("api_token"));
        imgbbApiTokenTextField.setText(rs.getString("imgbb_api_token"));

        // Browse button
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                if (!Files.exists(Paths.get(uploadPathTextField.getText())) || uploadPathTextField.getText().isEmpty()) {
                    // If the selected Upload path directory does not exist or Upload path is empty,
                    // open the current folder with Browse button
                    fc.setCurrentDirectory(new java.io.File("."));
                }
                else {
                    // Else, open the already selected Upload path
                    fc.setCurrentDirectory(new java.io.File(uploadPathTextField.getText()));
                }
                // Allow only directories
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fc.showOpenDialog(null);
                switch (result) {
                    case JFileChooser.APPROVE_OPTION:
                        // If Open is pressed in Browse window, change Upload path to the selected directory
                        uploadPathTextField.setText(fc.getSelectedFile().toString());
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.ERROR_OPTION:
                        break;
                }
            }
        });

        // Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Save currently selected Upload path, User ID, API Token and imgbb API Token to database
                    String updateSettings = "UPDATE settings SET path = ?, user_id = ?, api_token = ?, imgbb_api_token = ? "
                            + "WHERE id = 0";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateSettings);
                    preparedStatement.setString(1, uploadPathTextField.getText());
                    preparedStatement.setString(2, userIDtextField.getText());
                    preparedStatement.setString(3, apiTokenTextField.getText());
                    preparedStatement.setString(4, imgbbApiTokenTextField.getText());
                    preparedStatement.executeUpdate();

                    frame.dispose();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}
