package GUI;

import java.io.File;
import java.io.IOException;

// The class for creating thumbnail out of a video file
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class CreateThumbnail {

    // Method which creates a thumbnail out of a video file and stores the thumbnail file in DataHelper
    public void create(DataHelper show) throws IOException, InterruptedException {
        String videoFilePath = show.getAbsolutePath();
        String videoFileName = show.getFileName();

        String extension = show.getExtension();

        if (show.isFolder()) {
            // If show is folder, get the path to the video file inside that folder
            videoFilePath = show.getInsideFolderVideoFile().getAbsolutePath();
            String nameWithExtension = show.getInsideFolderVideoFile().getName();
            String[] tokens = nameWithExtension.split("\\.(?=[^\\.]+$)");
            videoFileName = tokens[0];
            extension = tokens[1];
        }

        // Thumbnail information font size
        String fontSize = "12";
        if (extension.equals("ts")) {
            // ts video files have more information displayed in the thumbnail and hence need a smaller font size
            // to display all that information
            fontSize = "10";
        }

        Runtime rt = Runtime.getRuntime();
        // Command Line command to create a thumbnail out of a video file with Movie Thumbnailer (mtn)
        Process pr = rt.exec("\"" +  System.getProperty("user.dir") + "\\lib\\mtn-win32\\bin\\mtn.exe\" -f \"" + System.getProperty("user.dir") + "\\lib\\mtn-win32\\bin\\meiryo.ttc\" -F FFFFFF:" + fontSize + ":meiryo.ttc:FFFFFF:000000:11 -k 272727 -L 4:2 -c 4 -r 4 -h 140 -g 5 -P -O \"" + show.getActualThisFile().getParent() + "\" \"" + videoFilePath + "\"");

        pr.waitFor();

        show.setThumbnailFile(new File(show.getActualThisFile().getParent() + "\\" + videoFileName + "_s.jpg"));
    }
}
