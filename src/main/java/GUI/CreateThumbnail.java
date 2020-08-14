package GUI;

import java.io.File;
import java.io.IOException;

public class CreateThumbnail {

    public void create(DataHelper show) throws IOException {
        String fontSize = "14";
        if (show.getExtension().equals("ts")) {
            fontSize = "10";
        }

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("\"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn-win32\\bin\\mtn.exe\" -f \"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn-win32\\bin\\meiryo.ttc\" -F FFFFFF:" + fontSize + ":meiryo.ttc:FFFFFF:000000:13 -k 272727 -L 4:2 -c 4 -r 4 -h 140 -g 5 -P -O \"" + show.getActualThisFile().getParent() + "\" \"" + show.getAbsolutePath() + "\"");

        show.setThumbnailFile(new File(show.getActualThisFile().getParent() + "\\" + show.getFileName() + "_s.jpg"));
    }

    /*public static void main(String[] args) throws IOException {
        //Runtime rt = Runtime.getRuntime();
        //Process pr = rt.exec("\"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn\\mtn.exe\" -f \"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn\\meiryo.ttc\" -F FFFFFF:14 -k 272727 -c 4 -r 4 -h 140 -g 5 -P -O \"" + show + "\\\" \"" + "C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\***" + "\"");

    }*/

}
