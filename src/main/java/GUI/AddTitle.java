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
        this.addTitleTextField.setText(show.getFileName());
        this.addExtensionTextField.setText(show.getExtension());

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

        UpdateTable(statement);

        titleJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                if (titleJTable.getSelectedRow() != -1) {
                    addTitleTextField.setText(titleJTable.getValueAt(titleJTable.getSelectedRow(), 0).toString());
                    addExtensionTextField.setText(titleJTable.getValueAt(titleJTable.getSelectedRow(), 1).toString());
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    try {
                        statement.executeUpdate("INSERT INTO shows (showname) VALUES('" +
                                addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText() + "');");

                        int anonymous = show.isAnonymous() ? 1 : 0;
                        int stream = show.isSteamOptimized() ? 1 : 0;
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
                                + "name=" + "'" + show.getTitle().replaceAll("'", "''") + "'"

                                + "WHERE showname=" + "'" + addTitleTextField.getText() + "."
                                + addExtensionTextField.getText() + "';");

                        UpdateTable(statement);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        deleteButton.setToolTipText("Select the show in the table you wish to delete.");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    try {
                        statement.executeUpdate("DELETE FROM shows WHERE showname=="
                                + "'" + addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText() + "';");
                        UpdateTable(statement);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        copyAndAddButton.setToolTipText("Select the show in the table you wish to copy. Then add a new name and/or extension.");

        copyAndAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addTitleTextField.getText().trim().length() > 0 && addExtensionTextField.getText().trim().length() > 0) {
                    if (titleJTable.getSelectedRow() != -1) {
                        try {
                            statement.executeUpdate("INSERT INTO shows (showname, description, category_id, type_id, resolution_id, tmdb, imdb, tvdb, mal, igdb, anonymous, stream, sd, internal, thumbnail, screenshots, name) "
                                    + "SELECT '" + addTitleTextField.getText().replaceAll("'", "''") + "." + addExtensionTextField.getText() + "', description, category_id, type_id, resolution_id, tmdb, imdb, tvdb, mal, igdb, anonymous, stream, sd, internal, thumbnail, screenshots, name "
                                    + "FROM shows WHERE showname='" + titleJTable.getValueAt(titleJTable.getSelectedRow(), 0).toString().replaceAll("'", "''") + "." + titleJTable.getValueAt(titleJTable.getSelectedRow(), 1).toString() + "';");
                            UpdateTable(statement);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void UpdateTable(Statement statement) throws SQLException {
        tableModel.setRowCount(0); // Empties table
        ResultSet rs = statement.executeQuery("select count(showname) from shows");
        int size = ((Number) rs.getObject(1)).intValue();
        Object rowData[] = new Object[2];

        rs = statement.executeQuery("select showname from shows");
        while(rs.next())
        {
            String[] tokens = rs.getString("showname").split("\\.(?=[^\\.]+$)");
            rowData[0] = tokens[0];
            rowData[1] = tokens[1];
            tableModel.addRow(rowData);
        }
    }

}
