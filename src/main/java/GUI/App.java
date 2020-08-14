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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.StringWriter;
import java.io.PrintWriter;

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
    private JRadioButton yesRadioButton;
    private JRadioButton noRadioButton;
    private JRadioButton yesRadioButton1;
    private JRadioButton noRadioButton1;
    private JRadioButton yesRadioButton2;
    private JRadioButton noRadioButton2;
    private JButton saveButton;
    private JButton addTitleButton;
    private JButton updateButton;
    private JProgressBar progressBar;
    private JButton logButton;

    private DefaultTableModel tableModel;

    private List<DataHelper> list;

    private final String[] categoryList = {"TS Firehose", "TV", "Movies", "Anime Movies", "Anime Shows", "Music"};
    private final String[] typeList = {"BluRay", "DVD", "HDTV", "WEB-DL", "WebRip", "TS (Raw)", "Re-encode"};
    private final String[] resolutionList = {"4320p", "2160p", "1080p", "1080i", "720p", "576p", "576i", "480p", "480i", "Other"};

    private ButtonGroup anonymousGroup = new ButtonGroup();
    private ButtonGroup streamOptimizedGroup = new ButtonGroup();
    private ButtonGroup sdContentGroup = new ButtonGroup();

    private Connection connection = null;
    private Statement statement = null;

    private DataHelper currentlySelectedShow = null;

    private CreateTorrent torrent = new CreateTorrent();
    private GetMediainfo getMediainfo = new GetMediainfo();
    private SendToSite sendToSite = new SendToSite();
    private CreateThumbnail createThumbnail = new CreateThumbnail();
    private SendToIMGBB sendToIMGBB = new SendToIMGBB();

    private StringBuilder messageBoxMessage;

    public App() throws SQLException {
        messageBoxMessage = new StringBuilder();

        anonymousGroup.add(yesRadioButton);
        anonymousGroup.add(noRadioButton);
        streamOptimizedGroup.add(yesRadioButton1);
        streamOptimizedGroup.add(noRadioButton1);
        sdContentGroup.add(yesRadioButton2);
        sdContentGroup.add(noRadioButton2);

        createDBConnection();

        String[][] data = {};
        String[] columnNames = {"File Name", "Series Name", "Extension"};
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        // https://stackoverflow.com/questions/10128064/jtable-selected-row-click-event
        fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (fileTable.getSelectedRow() != -1) {
                    SaveTempInfoIntoDataHelper();

                    for (DataHelper show : list) {
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            ShowInfo(show);
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
        UpdateFileTable(getSourceFolderPathFromDB());

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileTable.getSelectedRow() != -1) {
                    for (DataHelper show : list) {
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            try {
                                UpdateShow(show);
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

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Settings");
                try {
                    frame.setContentPane(new Settings(statement, frame).settingsPanel);
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

        addTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveTempInfoIntoDataHelper();
                JFrame frame = new JFrame("Add Title");
                if (fileTable.getSelectedRow() == -1) {
                    // Only when no titles in the table
                    DataHelper atarashii = new DataHelper("", "", "", new File(""));
                    try {
                        frame.setContentPane(new AddTitle(atarashii, statement).addTitlePanel);
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
                        if (Objects.equals(show.getFileName(), fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString())
                                && Objects.equals(show.getExtension(), fileTable.getValueAt(fileTable.getSelectedRow(), 2).toString())) {
                            try {
                                frame.setContentPane(new AddTitle(show, statement).addTitlePanel);
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

        updateButton.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UpdateFileTable(getSourceFolderPathFromDB()); // Need to change where the path comes!
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwables.printStackTrace(pw);
                    messageBoxMessage.append(sw.toString());
                }
            }
        });

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveTempInfoIntoDataHelper();

                for (DataHelper show : list) {
                    try {
                        messageBoxMessage.append("Creating thumbnail for file " + show.getFileName() + "." + show.getExtension() + "\n");
                        createThumbnail.create(show);
                        messageBoxMessage.append("Thumbnail created for file " + show.getFileName() + "." + show.getExtension() + "\n");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ioException.printStackTrace(pw);
                        messageBoxMessage.append(sw.toString());
                    }
                    try {
                        messageBoxMessage.append("Creating torrent for file " + show.getFileName() + "." + show.getExtension() + "\n");
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
                        getMediainfo.addMediainfoToDataHelper(show);
                        messageBoxMessage.append("Got mediainfo for file " + show.getFileName() + "." + show.getExtension() + "\n");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ioException.printStackTrace(pw);
                        messageBoxMessage.append(sw.toString());
                    }

                    while(!show.getThumbnailFile().exists()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    try {
                        ResultSet rs = statement.executeQuery("select * from settings where id=0");
                        messageBoxMessage.append(sendToIMGBB.send(show, rs.getString("imgbb_api_token")));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        throwables.printStackTrace(pw);
                        messageBoxMessage.append(sw.toString());
                    }

                    try {
                        ResultSet rs = statement.executeQuery("select * from settings where id=0");
                        messageBoxMessage.append(sendToSite.send(show, rs.getString("user_id"), rs.getString("api_token")));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        throwables.printStackTrace(pw);
                        messageBoxMessage.append(sw.toString());
                    }

                }
                progressBar.setStringPainted(true);
                progressBar.setValue(100);
                progressBar.setString("DONE");
            }
        });

        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Log");
                frame.setContentPane(new PopUpMessage(messageBoxMessage.toString()).popUpPanel);
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });
    }

    private void UpdateFileTable(String path) throws SQLException {

        tableModel.setRowCount(0); // Empties table

        if (!Files.exists(Paths.get(path)) || path.isEmpty()) {
            return;
        }

        GettingFiles folderFiles = new GettingFiles(path);
        list = folderFiles.getNames();

        Object rowData[] = new Object[3];
        for (DataHelper show : list) {
            rowData[0] = show.getFileName();

            ResultSet rs = statement.executeQuery("select showname from shows");
            String showname = "";
            String extension = "";
            while(rs.next())
            {
                String[] tokens = rs.getString("showname").split("\\.(?=[^\\.]+$)");
                showname = tokens[0];
                extension = tokens[1];
                if (show.getFileName().toLowerCase().contains(showname.toLowerCase())
                        && show.getExtension().toLowerCase().equals(extension.toLowerCase())) {
                    show.setShortName(showname);
                    FillDBInfo(show);
                    break;
                }
            }
            rowData[1] = show.getShortName();
            rowData[2] = show.getExtension();
            tableModel.addRow(rowData);

        }
        if (!list.isEmpty()) {
            fileTable.changeSelection(0, 0, false, false);
        }
    }

    private void FillDBInfo(DataHelper show) throws SQLException {
        ResultSet rs = statement.executeQuery("select * from shows where showname = "
                + "'" + show.getShortName() + "." + show.getExtension() + "'");
        show.setDescription(rs.getString("description"));
        show.setCategory(rs.getString("category_id"));
        show.setType(rs.getString("type_id"));
        show.setResolution(rs.getString("resolution_id"));
        show.setTmdbID(rs.getString("tmdb"));
        show.setImdbID(rs.getString("imdb"));
        show.setTvdbID(rs.getString("tvdb"));
        show.setMalID(rs.getString("mal"));
        show.setAnonymous(rs.getString("anonymous").equals("1"));
        show.setSteamOptimized(rs.getString("stream").equals("1"));
        show.setSdContent(rs.getString("sd").equals("1"));
        show.setInternal(rs.getString("internal").equals("1"));
        show.setThumbnail(rs.getString("thumbnail").equals("1"));
        show.setScreenshots(rs.getInt("screenshots"));
        show.setTitle(rs.getString("name"));
    }

    private void ShowInfo(DataHelper show) {
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
        yesRadioButton.setSelected(show.isAnonymous());
        noRadioButton.setSelected(!show.isAnonymous());
        streamOptimizedGroup.clearSelection();
        yesRadioButton1.setSelected(show.isSteamOptimized());
        noRadioButton1.setSelected(!show.isSteamOptimized());
        sdContentGroup.clearSelection();
        yesRadioButton2.setSelected(show.isSdContent());
        noRadioButton2.setSelected(!show.isSdContent());
    }

    private void UpdateShow(DataHelper show) throws SQLException {
        if (!show.getShortName().isEmpty()) {
            UpdateDataHelper(show);
            UpdateDBShow(show);
        }
    }

    private void UpdateDataHelper(DataHelper show) {
        show.setTitle(titleTextField.getText());
        show.setCategory(categoryComboBox.getSelectedItem().toString());
        show.setType(typeComboBox.getSelectedItem().toString());
        show.setResolution(resolutionComboBox.getSelectedItem().toString());
        show.setTmdbID(tmdbTextField.getText());
        show.setImdbID(imdbTextField.getText());
        show.setTvdbID(tvdbTextField.getText());
        show.setMalID(malTextField.getText());
        show.setDescription(descriptionTextArea.getText());
        show.setAnonymous(yesRadioButton.isSelected());
        show.setSteamOptimized(yesRadioButton1.isSelected());
        show.setSdContent(yesRadioButton2.isSelected());

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

    private void UpdateDBShow(DataHelper show) throws SQLException {
        int anonymous = show.isAnonymous() ? 1 : 0;
        int stream = show.isSteamOptimized() ? 1 : 0;
        int sd = show.isSdContent() ? 1 : 0;

        statement.executeUpdate("UPDATE shows SET description=" + "'" + descriptionTextArea.getText().replaceAll("'", "''") + "',"
                + "category_id=" + "'" + categoryComboBox.getSelectedItem().toString() + "',"
                + "type_id=" + "'" + typeComboBox.getSelectedItem().toString() + "',"
                + "resolution_id=" + "'" + resolutionComboBox.getSelectedItem().toString() + "',"
                + "tmdb=" + Integer.parseInt(show.getTmdbID()) + ","
                + "imdb=" + Integer.parseInt(show.getImdbID()) + ","
                + "tvdb=" + Integer.parseInt(show.getTvdbID()) + ","
                + "mal=" + Integer.parseInt(show.getMalID()) + ","
                + "anonymous=" + anonymous + ","
                + "stream=" + stream + ","
                + "sd=" + sd + ","
                + "name=" + "'" + titleTextField.getText().replaceAll("'", "''") + "'"

                + "WHERE showname=" + "'" + show.getShortName() + "." + show.getExtension() + "'");
    }

    private void SaveTempInfoIntoDataHelper() {
        if (currentlySelectedShow != null) {
            // Saves the inserted data into the left Data Helper
            UpdateDataHelper(currentlySelectedShow);
        }
    }

    private void createDBConnection() {
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Kasutaja\\Downloads\\SQLite\\upyours.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(5);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            messageBoxMessage.append(sw.toString());
        }
    }

    private String getSourceFolderPathFromDB() throws SQLException {
        ResultSet rs = statement.executeQuery("select path from settings where id=0");
        return rs.getString("path");
    }


    public static void main(String[] args) throws SQLException {
        JFrame frame = new JFrame("Up Yours");
        frame.setContentPane(new App().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        categoryComboBox = new JComboBox<>(categoryList);

        typeComboBox = new JComboBox<>(typeList);

        resolutionComboBox = new JComboBox<>(resolutionList);

    }
}