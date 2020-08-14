package GUI;

import javax.swing.*;

public class PopUpMessage {
    public JPanel popUpPanel;
    private JTextArea infoTextArea;
    private JScrollPane popUpScrollPane;

    public PopUpMessage(String message) {
        infoTextArea.setEditable(false);
        infoTextArea.setText(message);
    }
}
