package GUI;

import java.io.File;
import java.io.IOException;

public class CreateThumbnail {

    public void create(DataHelper show) throws IOException {
        String videoFilePath = show.getAbsolutePath();
        String videoFileName = show.getFileName();

        String extension = show.getExtension();

        if (show.isFolder()) {
            videoFilePath = show.getInsideFolderVideoFile().getAbsolutePath();
            String nameWithExtension = show.getInsideFolderVideoFile().getName();
            String[] tokens = nameWithExtension.split("\\.(?=[^\\.]+$)");
            videoFileName = tokens[0];
            extension = tokens[1];
        }

        String fontSize = "12";
        if (extension.equals("ts")) {
            fontSize = "10";
        }

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("\"" +  System.getProperty("user.dir") + "\\lib\\mtn-win32\\bin\\mtn.exe\" -f \"" + System.getProperty("user.dir") + "\\lib\\mtn-win32\\bin\\meiryo.ttc\" -F FFFFFF:" + fontSize + ":meiryo.ttc:FFFFFF:000000:11 -k 272727 -L 4:2 -c 4 -r 4 -h 140 -g 5 -P -O \"" + show.getActualThisFile().getParent() + "\" \"" + videoFilePath + "\"");

        show.setThumbnailFile(new File(show.getActualThisFile().getParent() + "\\" + videoFileName + "_s.jpg"));
    }

    /*public static void main(String[] args) throws IOException {
        //Runtime rt = Runtime.getRuntime();
        //Process pr = rt.exec("\"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn\\mtn.exe\" -f \"C:\\Users\\Kasutaja\\Downloads\\SQLite\\mtn\\meiryo.ttc\" -F FFFFFF:14 -k 272727 -c 4 -r 4 -h 140 -g 5 -P -O \"" + show + "\\\" \"" + "C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\***" + "\"");

    }*/

}
