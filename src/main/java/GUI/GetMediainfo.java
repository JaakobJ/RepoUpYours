package GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// The class for getting mediainfo about a video file
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class GetMediainfo {

    // Method gets mediainfo about a video file and adds that mediainfo into the DataHelper
    public void addMediainfoToDataHelper(DataHelper show) throws IOException {
        // Find the absolute path to the video file
        String videoFilePath = show.getAbsolutePath();
        if (show.isFolder()) {
            // If show is a folder, get the absolute path to the video file inside the folder
            videoFilePath = show.getInsideFolderVideoFile().getAbsolutePath();
        }

        Runtime rt = Runtime.getRuntime();
        // Command Line command for getting mediainfo about the video file with MediaInfo
        Process pr = rt.exec("\""+ System.getProperty("user.dir") + "\\lib\\MediaInfo.exe\" \"" + videoFilePath + "\"");

        // Buffered reader for reading/storing mediainfo
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder mediainfo = new StringBuilder();
        String line;
        while((line=input.readLine()) != null) {
            if (line.contains("Service name")) {
                // Some raw video files have "too much"/unnecessary information which upload sites do not want to parse (can cause errors).
                // If we have read all the important information and the video file contains that unnecessary information, we stop.
                break;
            }
            mediainfo.append(line + "\n");
        }
        show.setMediainfo(mediainfo.toString());
    }
}
