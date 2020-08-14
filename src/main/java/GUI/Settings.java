package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        uploadPathTextField.setEditable(false);

        ResultSet rs = statement.executeQuery("select * from settings where id=0");
        uploadPathTextField.setText(rs.getString("path"));
        userIDtextField.setText(rs.getString("user_id"));
        apiTokenTextField.setText(rs.getString("api_token"));
        imgbbApiTokenTextField.setText(rs.getString("imgbb_api_token"));

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                if (!Files.exists(Paths.get(uploadPathTextField.getText())) || uploadPathTextField.getText().isEmpty()) {
                    fc.setCurrentDirectory(new java.io.File("."));
                }
                else {
                    fc.setCurrentDirectory(new java.io.File(uploadPathTextField.getText()));
                }
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fc.showOpenDialog(null);
                switch (result) {
                    case JFileChooser.APPROVE_OPTION:
                        uploadPathTextField.setText(fc.getSelectedFile().toString());
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.ERROR_OPTION:
                        break;
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    statement.executeUpdate("UPDATE settings SET path=" + "'" + uploadPathTextField.getText() + "',"
                            + "user_id=" + "'" + userIDtextField.getText() + "',"
                            + "api_token=" + "'" + apiTokenTextField.getText() + "',"
                            + "imgbb_api_token=" + "'" + imgbbApiTokenTextField.getText() + "'"
                            + "WHERE id=0");
                    frame.dispose();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}
