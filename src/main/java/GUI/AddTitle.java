package GUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// The class which is opened when Add Series button is pressed in GUI
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class AddTitle {
    public JPanel addTitlePanel;
    private JTable titleJTable;
    private JTextField addTitleTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JScrollPane titleJScrollPane;
    private JTextField addExtensionTextField;
    private JButton copyAndAddButton;

    private DefaultTableModel tableModel;

    public AddTitle(final DataHelper show, final Statement statement) throws SQLException {
        // Fill the Name and Extension text fields
        addTitleTextField.setText(show.getFileName());
        addExtensionTextField.setText(show.getExtension());

        // Set the caret position of Name and Extension text fields to the beginning
        addTitleTextField.setCaretPosition(0);
        addExtensionTextField.setCaretPosition(0);

        // Choose a tableModel
        String[][] data = {};
        String[] columnNames = {"Show name", "Extension"};
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        titleJTable.setModel(tableModel);
        titleJTable.getTableHeader().setReorderingAllowed(false);

        // Fill the table
        updateTable(statement);

        titleJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (titleJTable.getSelectedRow() != -1) {
                    // If a show is selected in the table, change the Name and Extension text fields to match the selected show
                    addTitleTextField.setText(titleJTable.getValueAt(titleJTable.getSelectedRow(), 0).toString());
                    addExtensionTextField.setText(titleJTable.getValueAt(titleJTable.getSelectedRow(), 1).toString());
                    // Set the caret position of Name and Extension text fields to the beginning
                    addTitleTextField.setCaretPosition(0);
                    addExtensionTextField.setCaretPosition(0);
                }
            }
        });

        // Add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    // If name and extension are not empty strings or just spaces
                    try {
                        // Add the series (information from DataHelper) to database
                        statement.executeUpdate("INSERT INTO shows (showname) VALUES('" +
                                addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText().replaceAll("'", "''") + "');");

                        int anonymous = show.isAnonymous() ? 1 : 0;
                        int stream = show.isStreamOptimized() ? 1 : 0;
                        int sd = show.isSdContent() ? 1 : 0;
                        statement.executeUpdate("UPDATE shows SET description=" + "'" + show.getDescription().replaceAll("'", "''") + "',"
                                + "category_id=" + "'" + show.getCategory() + "',"
                                + "type_id=" + "'" + show.getType() + "',"
                                + "resolution_id=" + "'" + show.getResolution() + "',"
                                + "tmdb=" + Integer.parseInt(show.getTmdbID()) + ","
                                + "imdb=" + Integer.parseInt(show.getImdbID()) + ","
                                + "tvdb=" + Integer.parseInt(show.getTvdbID()) + ","
                                + "mal=" + Integer.parseInt(show.getMalID()) + ","
                                + "anonymous=" + anonymous + ","
                                + "stream=" + stream + ","
                                + "sd=" + sd + ","
                                + "name=" + "'" + show.getTitle().replaceAll("'", "''") + "' "

                                + "WHERE showname=" + "'" + addTitleTextField.getText().replaceAll("'", "''") + "."
                                + addExtensionTextField.getText().replaceAll("'", "''") + "';");

                        // Update table, so the new addition can be seen
                        updateTable(statement);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        deleteButton.setToolTipText("Select the show in the table you wish to delete.");

        // Delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    // If name and extension are not empty strings or just spaces
                    try {
                        // Delete the show from database
                        statement.executeUpdate("DELETE FROM shows WHERE showname=="
                                + "'" + addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText().replaceAll("'", "''") + "';");
                        // Update table, so the new the changes can be seen
                        updateTable(statement);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        copyAndAddButton.setToolTipText("Select the show in the table you wish to copy. Then add a new name and/or extension.");

        // Copy/Add button
        copyAndAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    // If name and extension are not empty strings or just spaces
                    if (titleJTable.getSelectedRow() != -1) {
                        // When a show is selected in table
                        try {
                            // Create the new show and copy the selected shows information into the new show
                            statement.executeUpdate("INSERT INTO shows (showname, description, category_id, type_id, resolution_id, tmdb, imdb, tvdb, mal, igdb, anonymous, stream, sd, internal, thumbnail, screenshots, name) "
                                    + "SELECT '" + addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText().replaceAll("'", "''") + "', description, category_id, type_id, resolution_id, tmdb, imdb, tvdb, mal, igdb, anonymous, stream, sd, internal, thumbnail, screenshots, name "
                                    + "FROM shows WHERE showname='" + titleJTable.getValueAt(titleJTable.getSelectedRow(), 0).toString().replaceAll("'", "''") + "." + titleJTable.getValueAt(titleJTable.getSelectedRow(), 1).toString().replaceAll("'", "''") + "';");
                            // Update table, so the new addition can be seen
                            updateTable(statement);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    // Method to update the table
    private void updateTable(Statement statement) throws SQLException {
        tableModel.setRowCount(0); // Empties table

        Object rowData[] = new Object[2];

        // List all the show names from database and add them to the table
        ResultSet rs = statement.executeQuery("select showname from shows");
        while(rs.next())
        {
            String[] tokens = rs.getString("showname").split("\\.(?=[^\\.]+$)");
            rowData[0] = tokens[0];
            rowData[1] = tokens[1];
            tableModel.addRow(rowData);
        }
    }

}
