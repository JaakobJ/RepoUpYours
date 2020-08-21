package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public Settings(final Statement statement, final JFrame frame) throws SQLException {
        uploadPathTextField.setEditable(false); // Disables Upload Path Text field editing

        // Get Upload path, User ID, API Token and imgbb API Token from database
        ResultSet rs = statement.executeQuery("select * from settings where id=0");
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
                    statement.executeUpdate("UPDATE settings SET path=" + "'" + uploadPathTextField.getText().replaceAll("'", "''") + "',"
                            + "user_id=" + "'" + userIDtextField.getText().replaceAll("'", "''") + "',"
                            + "api_token=" + "'" + apiTokenTextField.getText().replaceAll("'", "''") + "',"
                            + "imgbb_api_token=" + "'" + imgbbApiTokenTextField.getText().replaceAll("'", "''") + "'"
                            + "WHERE id=0");
                    frame.dispose();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}
