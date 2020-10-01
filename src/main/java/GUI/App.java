package GUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.io.StringWriter;
import java.io.PrintWriter;

// The main GUI class for Up Yours upload tool.
// The class is using classes AddTitle.java, CreateThumbnail.java, CreateTorrent.java, DataHelper.java,
// GetMediainfo.java, GettingFiles.java, PopUpMessage.java, SendToIMGBB.java, SentToSite.java and Settings.java
public class App {
    private JPanel mainPanel;
    private JButton settingsButton;
    private JButton uploadButton;
    private JTable fileTable;
    private JScrollPane smthJScrollPane;
    private JTextField titleTextField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> resolutionComboBox;
    private JTextField tmdbTextField;
    private JTextField imdbTextField;
    private JTextField tvdbTextField;
    private JTextField malTextField;
    private JComboBox<String> typeComboBox;
    private JScrollPane descriptionScrollPane;
    private JTextArea descriptionTextArea;
    private JRadioButton anonymousYesRadioButton;
    private JRadioButton anonymousNoRadioButton;
    private JRadioButton streamYesRadioButton;
    private JRadioButton streamNoRadioButton;
    private JRadioButton sdYesRadioButton;
    private JRadioButton sdNoRadioButton;
    private JButton saveButton;
    private JButton addTitleButton;
    private JButton updateButton;
    private JProgressBar progressBar;
    private JButton logButton;
    private JRadioButton internalYesRadioButton;
    private JRadioButton internalNoRadioButton;

    private DefaultTableModel tableModel;

    private List<DataHelper> list;

    private final String[] categoryList = {"TS Firehose", "TV", "Movies", "Anime Movies", "Anime Shows", "Music"};
    private final String[] typeList = {"BluRay", "DVD", "HDTV", "WEB-DL", "WebRip", "TS (Raw)", "Re-encode"};
    private final String[] resolutionList = {"4320p", "2160p", "1080p", "1080i", "720p", "576p", "576i", "480p", "480i", "Other"};

    private ButtonGroup anonymousGroup = new ButtonGroup();
    private ButtonGroup streamOptimizedGroup = new ButtonGroup();
    private ButtonGroup sdContentGroup = new ButtonGroup();
    private ButtonGroup internalGroup = new ButtonGroup();

    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;

    // To remember which show is currently selected
    private DataHelper currentlySelectedShow = null;

    private CreateTorrent torrent = new CreateTorrent();
    private GetMediainfo getMediainfo = new GetMediainfo();
    private SendToSite sendToSite = new SendToSite();
    private CreateThumbnail createThumbnail = new CreateThumbnail();
    private SendToIMGBB sendToIMGBB = new SendToIMGBB();

    private StringBuilder messageBoxMessage;

    public App() throws SQLException {
        // Stores the message to a user which is displayed when clicked on the Log button
        messageBoxMessage = new StringBuilder();

        anonymousGroup.add(anonymousYesRadioButton);
        anonymousGroup.add(anonymousNoRadioButton);
        streamOptimizedGroup.add(streamYesRadioButton);
        streamOptimizedGroup.add(streamNoRadioButton);
        sdContentGroup.add(sdYesRadioButton);
        sdContentGroup.add(sdNoRadioButton);
        internalGroup.add(internalYesRadioButton);
        internalGroup.add(internalNoRadioButton);

        // Create Database connection
        createDBConnection();

        // Choose a tableModel
        String[][] data = {};
        String[] columnNames = {"File Name", "Series Name", "Extension"};
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disables cell editing.
                return false;
            }
        };

        // https://stackoverflow.com/questions/10128064/jtable-selected-row-click-event
        // File Table Action Listener
        fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (fileTable.getSelectedRow() != -1) {
                    // When another show is selected in File Table
                    // Save the information of previously selected show
                    saveTempInfoIntoDataHelper();
                    for (DataHelper show : list) {
                        // Finds the DataHelper which was selected in File Table
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            // Display the selected show's information
                            showInfo(show);
                            // Change the currently selected show
                            currentlySelectedShow = show;
                            break;
                        }
                    }
                }
            }
        });

        fileTable.setModel(tableModel);
        fileTable.getTableHeader().setReorderingAllowed(false);
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(125);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        // Fill the File Table
        updateFileTable(getSourceFolderPathFromDB());

        // Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileTable.getSelectedRow() != -1) {
                    // If a show is currently selected in File Table
                    for (DataHelper show : list) {
                        // Finds the DataHelper which is currently selected in File Table
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            try {
                                // Save the updated info into the DataHelper and database
                                updateShow(show);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                throwables.printStackTrace(pw);
                                messageBoxMessage.append(sw.toString());
                            }
                            break;
                        }
                    }
                }
            }
        });

        // Settings button (src/main/java/GUI/Settings.java)
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Settings");
                try {
                    frame.setContentPane(new Settings(connection, frame).settingsPanel);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwables.printStackTrace(pw);
                    messageBoxMessage.append(sw.toString());
                }
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });

        // Add Series button (src/main/java/GUI/AddTitle.java)
        addTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save the information of previously selected show
                saveTempInfoIntoDataHelper();
                JFrame frame = new JFrame("Add Series");
                if (fileTable.getSelectedRow() == -1) {
                    // Only when no shows in File Table
                    DataHelper atarashii = new DataHelper("", "", "", new File(""));
                    try {
                        frame.setContentPane(new AddTitle(atarashii, connection).addTitlePanel);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        throwables.printStackTrace(pw);
                        messageBoxMessage.append(sw.toString());
                    }
                }
                else {
                    for (DataHelper show : list) {
                        // Finds the DataHelper which is currently selected in File Table
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            try {
                                frame.setContentPane(new AddTitle(show, connection).addTitlePanel);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                throwables.printStackTrace(pw);
                                messageBoxMessage.append(sw.toString());
                            }
                            break;
                        }
                    }
                }
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });

        updateButton.setToolTipText("Pressing Reload deletes unfinished information if you haven't pressed Save.");

        // Reload button
        updateButton.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Update File Table
                    updateFileTable(getSourceFolderPathFromDB());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwables.printStackTrace(pw);
                    messageBoxMessage.append(sw.toString());
                }
            }
        });

        // Upload All button
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save the information of previously selected show
                saveTempInfoIntoDataHelper();

                // Check if all shows have TMDB ID
                if (checkForTmdbEntry()) {
                    // If yes, for every show in File Table:
                    for (DataHelper show : list) {
                        try {
                            messageBoxMessage.append("Creating torrent for file " + show.getFileName() + "." + show.getExtension() + "\n");
                            // Create torrent file
                            torrent.createTorrent(show);
                            messageBoxMessage.append("Torrent created for file " + show.getFileName() + "." + show.getExtension() + "\n");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ioException.printStackTrace(pw);
                            messageBoxMessage.append(sw.toString());
                        }
                        try {
                            messageBoxMessage.append("Creating mediainfo for file " + show.getFileName() + "." + show.getExtension() + "\n");
                            // Get mediainfo
                            getMediainfo.addMediainfoToDataHelper(show);
                            messageBoxMessage.append("Mediainfo created for file " + show.getFileName() + "." + show.getExtension() + "\n");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ioException.printStackTrace(pw);
                            messageBoxMessage.append(sw.toString());
                        }
                        try {
                            messageBoxMessage.append("Creating thumbnail for file " + show.getFileName() + "." + show.getExtension() + "\n");
                            // Create thumbnail
                            createThumbnail.create(show);
                        } catch (IOException | InterruptedException ioException) {
                            ioException.printStackTrace();
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ioException.printStackTrace(pw);
                            messageBoxMessage.append(sw.toString());
                        }
                        try {
                            ResultSet rs = statement.executeQuery("SELECT * FROM settings WHERE id=0");
                            // Upload the thumbnail to imgbb and receive a link
                            messageBoxMessage.append(sendToIMGBB.send(show, rs.getString("imgbb_api_token")));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            throwables.printStackTrace(pw);
                            messageBoxMessage.append(sw.toString());
                        }
                        try {
                            ResultSet rs = statement.executeQuery("SELECT * FROM settings WHERE id=0");
                            // Upload the torrent along with all the information to "a site"
                            // This is currently disabled as it's not possible to test this without having an account on that site
                            //messageBoxMessage.append(sendToSite.send(show, rs.getString("user_id"), rs.getString("api_token")));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            throwables.printStackTrace(pw);
                            messageBoxMessage.append(sw.toString());
                        }
                    }
                    // Updates Progress Bar
                    progressBar.setStringPainted(true);
                    if (messageBoxMessage.toString().contains("ERROR")
                            || messageBoxMessage.toString().contains("\"success\":false")
                            || messageBoxMessage.toString().contains("Exception")) {
                        progressBar.setValue(0);
                        progressBar.setString("SOMETHING WENT WRONG, CHECK LOG");
                    }
                    else {
                        progressBar.setValue(100);
                        progressBar.setString("DONE");
                    }
                }
            }
        });

        // Log button (src/main/java/GUI/PopUpMessage.java)
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display the message compiled with StringBuilder messageBoxMessage
                JFrame frame = new JFrame("Log");
                frame.setContentPane(new PopUpMessage(messageBoxMessage.toString()).popUpPanel);
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });
    }

    // Method to update File Table
    private void updateFileTable(String path) throws SQLException {

        tableModel.setRowCount(0); // Empties table

        if (!Files.exists(Paths.get(path)) || path.isEmpty()) {
            // If path to the source folder does not exist
            return;
        }

        // Get all the files in the source folder as empty DataHelpers (with no title, description, etc.)
        GettingFiles folderFiles = new GettingFiles(path);
        list = folderFiles.getNames();

        // Fill File Table
        Object rowData[] = new Object[3];
        for (DataHelper show : list) {
            rowData[0] = show.getFileName();

            // Get all the series names from database and add them to File Table
            ResultSet rs = statement.executeQuery("SELECT showname FROM shows");
            String showname = "";
            String extension = "";
            while(rs.next())
            {
                String[] tokens = rs.getString("showname").split("\\.(?=[^\\.]+$)");
                showname = tokens[0];
                extension = tokens[1];
                if (show.getFileName().toLowerCase().contains(showname.toLowerCase())
                        && show.getExtension().toLowerCase().equals(extension.toLowerCase())) {
                    // If file name contains a series name, set that series name as that DataHelper's series name
                    show.setShortName(showname);
                    // Fill that DataHelper with the information stored in database about that series
                    fillDBInfo(show);
                    break;
                }
            }
            rowData[1] = show.getShortName();
            rowData[2] = show.getExtension();
            tableModel.addRow(rowData);

        }
        if (!list.isEmpty()) {
            // If File Table is not empty, set the first show in File Table default selected
            fileTable.changeSelection(0, 0, false, false);
        }
    }

    // Method to fill a DataHelper with the information stored in database
    private void fillDBInfo(DataHelper show) throws SQLException {
        String selectShow = "SELECT * FROM shows WHERE showname = ?";
        preparedStatement = connection.prepareStatement(selectShow);
        preparedStatement.setString(1, show.getShortName() + "." + show.getExtension());
        ResultSet rs = preparedStatement.executeQuery();

        show.setDescription(rs.getString("description"));
        show.setCategory(rs.getString("category_id"));
        show.setType(rs.getString("type_id"));
        show.setResolution(rs.getString("resolution_id"));
        show.setTmdbID(rs.getString("tmdb"));
        show.setImdbID(rs.getString("imdb"));
        show.setTvdbID(rs.getString("tvdb"));
        show.setMalID(rs.getString("mal"));
        show.setAnonymous(rs.getString("anonymous").equals("1"));
        show.setStreamOptimized(rs.getString("stream").equals("1"));
        show.setSdContent(rs.getString("sd").equals("1"));
        show.setInternal(rs.getString("internal").equals("1"));
        show.setThumbnail(rs.getString("thumbnail").equals("1"));
        show.setScreenshots(rs.getInt("screenshots"));
        show.setTitle(rs.getString("name"));
    }

    // Method to display the information of a selected show
    private void showInfo(DataHelper show) {
        titleTextField.setText(show.getTitle());
        categoryComboBox.setSelectedIndex(Arrays.asList(categoryList).indexOf(show.getCategory()));
        typeComboBox.setSelectedIndex(Arrays.asList(typeList).indexOf(show.getType()));
        resolutionComboBox.setSelectedIndex(Arrays.asList(resolutionList).indexOf(show.getResolution()));
        tmdbTextField.setText(show.getTmdbID());
        imdbTextField.setText(show.getImdbID());
        tvdbTextField.setText(show.getTvdbID());
        malTextField.setText(show.getMalID());
        descriptionTextArea.setText(show.getDescription());
        anonymousGroup.clearSelection();
        anonymousYesRadioButton.setSelected(show.isAnonymous());
        anonymousNoRadioButton.setSelected(!show.isAnonymous());
        streamOptimizedGroup.clearSelection();
        streamYesRadioButton.setSelected(show.isStreamOptimized());
        streamNoRadioButton.setSelected(!show.isStreamOptimized());
        sdContentGroup.clearSelection();
        sdYesRadioButton.setSelected(show.isSdContent());
        sdNoRadioButton.setSelected(!show.isSdContent());
        internalGroup.clearSelection();
        internalYesRadioButton.setSelected(show.isInternal());
        internalNoRadioButton.setSelected(!show.isInternal());

        // Set the caret position of Title and Description field to the beginning
        titleTextField.setCaretPosition(0);
        descriptionTextArea.setCaretPosition(0);
    }

    // Method to save the updated info into DataHelper and database
    private void updateShow(DataHelper show) throws SQLException {
        if (!show.getShortName().isEmpty()) {
            // If a show has a Series Name, update DataHelper and database
            updateDataHelper(show);
            updateDBShow(show);
        }
    }

    // Method to update DataHelper with updated info
    private void updateDataHelper(DataHelper show) {
        show.setTitle(titleTextField.getText());
        show.setCategory(categoryComboBox.getSelectedItem().toString());
        show.setType(typeComboBox.getSelectedItem().toString());
        show.setResolution(resolutionComboBox.getSelectedItem().toString());
        show.setTmdbID(tmdbTextField.getText());
        show.setImdbID(imdbTextField.getText());
        show.setTvdbID(tvdbTextField.getText());
        show.setMalID(malTextField.getText());
        show.setDescription(descriptionTextArea.getText());
        show.setAnonymous(anonymousYesRadioButton.isSelected());
        show.setStreamOptimized(streamYesRadioButton.isSelected());
        show.setSdContent(sdYesRadioButton.isSelected());
        show.setInternal(internalYesRadioButton.isSelected());

        // TMDB, IMDB, TVDB & MAL IDs are integers. If user inserted something other than an integer, the ID is reset to 0
        try { Integer.parseInt(tmdbTextField.getText()); } catch (NumberFormatException e) {
            show.setTmdbID("0");
        }
        try { Integer.parseInt(imdbTextField.getText()); } catch (NumberFormatException e) {
            show.setImdbID("0");
        }
        try { Integer.parseInt(tvdbTextField.getText()); } catch (NumberFormatException e) {
            show.setTvdbID("0");
        }
        try { Integer.parseInt(malTextField.getText()); } catch (NumberFormatException e) {
            show.setMalID("0");
        }
    }

    // Method to update database with updated info
    private void updateDBShow(DataHelper show) throws SQLException {
        // anonymous, stream optimized, sd content and internal are stored in database as 1 (true) and 0 (false)
        int anonymous = show.isAnonymous() ? 1 : 0;
        int stream = show.isStreamOptimized() ? 1 : 0;
        int sd = show.isSdContent() ? 1 : 0;

        String updateShow = "UPDATE shows SET description = ?, category_id = ?, type_id = ?, resolution_id = ?, " +
                "tmdb = ?, imdb = ?, tvdb = ?, mal = ?, anonymous = ?, stream = ?, sd = ?, name = ? " +
                "WHERE showname = ?;";
        preparedStatement = connection.prepareStatement(updateShow);
        preparedStatement.setString(1, descriptionTextArea.getText());
        preparedStatement.setString(2, categoryComboBox.getSelectedItem().toString());
        preparedStatement.setString(3, typeComboBox.getSelectedItem().toString());
        preparedStatement.setString(4, resolutionComboBox.getSelectedItem().toString());
        preparedStatement.setInt(5, Integer.parseInt(show.getTmdbID()));
        preparedStatement.setInt(6, Integer.parseInt(show.getImdbID()));
        preparedStatement.setInt(7, Integer.parseInt(show.getTvdbID()));
        preparedStatement.setInt(8, Integer.parseInt(show.getMalID()));
        preparedStatement.setInt(9, anonymous);
        preparedStatement.setInt(10, stream);
        preparedStatement.setInt(11, sd);
        preparedStatement.setString(12, titleTextField.getText());
        preparedStatement.setString(13, show.getShortName() + "." + show.getExtension());
        preparedStatement.executeUpdate();
    }

    // Method to save the information of previously selected show
    private void saveTempInfoIntoDataHelper() {
        if (currentlySelectedShow != null) {
            // If there is a currentlySelectedShow, save the left information into that DataHelper
            updateDataHelper(currentlySelectedShow);
        }
    }

    // Method to create database connection
    private void createDBConnection() {
        try {
            // Create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(5);
        } catch (SQLException e) {
            // If the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            messageBoxMessage.append(sw.toString());
        }
    }

    // Method to get the source folder (the folder which is specified in Settings) path from database
    private String getSourceFolderPathFromDB() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT path FROM settings WHERE id = 0");
        return rs.getString("path");
    }

    // Method to check whether all shows have TMDB ID
    // If all shows do, return true ; If all shows do not, return false
    private boolean checkForTmdbEntry() {
        List<DataHelper> faultyShows = new ArrayList<>();

        for (DataHelper show : list ) {
            if (show.getTmdbID().equals("0")) {
                faultyShows.add(show);
            }
        }

        if (!faultyShows.isEmpty()) {
            StringBuilder message = new StringBuilder("UPLOAD DENIED\n\nThe following entries are missing a TMDB ID:\n");

            for (DataHelper show : faultyShows) {
                message.append(show.getFileName() + "." + show.getExtension() + "\n");
            }

            // PopUpMessage (src/main/java/GUI/PopUpMessage.java)
            JFrame frame = new JFrame("Shows missing TMDB ID number.");
            frame.setContentPane(new PopUpMessage(message.toString()).popUpPanel);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        }

        return faultyShows.isEmpty();
    }

    // The main class which opens the GUI
    public static void main(String[] args) throws SQLException {
        JFrame frame = new JFrame("Up Yours");
        frame.setContentPane(new App().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        // Fills combo box selections
        categoryComboBox = new JComboBox<>(categoryList);

        typeComboBox = new JComboBox<>(typeList);

        resolutionComboBox = new JComboBox<>(resolutionList);

    }
}
