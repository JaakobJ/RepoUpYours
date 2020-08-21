package GUI;

import javax.swing.*;

// The class which is opened when Log button is pressed in GUI
// Also used by checkForTmdbEntry() method in App.java
// The class is used by App.java
public class PopUpMessage {
    public JPanel popUpPanel;
    private JTextArea infoTextArea;
    private JScrollPane popUpScrollPane;

    // Display the given message
    public PopUpMessage(String message) {
        infoTextArea.setEditable(false);
        infoTextArea.setText(message);
    }
}
