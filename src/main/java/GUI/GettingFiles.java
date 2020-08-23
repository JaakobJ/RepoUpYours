package GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The class for getting the needed files from a selected folder
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class GettingFiles {
    private String path;

    // Currently allowed video file types:
    private final String[] fileextensions = {"mp4", "mkv", "ts", "avi", "flv", "divx", "m2ts", "mov", "mpg", "mpeg",
            "wmv", "xvid"};

    // The list of DataHelpers which is returned in getNames() method
    List<DataHelper> answer = new ArrayList<>();

    public GettingFiles(String path) {
        this.path = path;
    }

    // Method to get all the allowed files from a selected folder, turn them into DataHelpers and return the list of
    // those DataHelpers
    public List<DataHelper> getNames() {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                // If the file is a directory/folder
                // Check whether that folder contains any allowed video files
                handleDirectory(listOfFiles[i]);
            }
            else if (listOfFiles[i].isFile()) {
                // If the file is file
                String[] tokens = listOfFiles[i].getName().split("\\.(?=[^\\.]+$)");
                if (Arrays.asList(fileextensions).contains(tokens[1])) {
                    // If file is of allowed type, create a DataHelper out of it and add to the list
                    answer.add(new DataHelper(tokens[0], tokens[1], listOfFiles[i].getAbsolutePath(), listOfFiles[i]));
                }
            }
        }
        return answer;
    }

    // Method to check whether the directory contains a video file of th allowed type
    // If yes, add to list
    private void handleDirectory(File directoryFile) {
        File[] atarashiiListOfFiles = directoryFile.listFiles();
        for (int g = 0; g < atarashiiListOfFiles.length; g++) {
            // Check all the files in directory
            String[] atarashiiTokens = atarashiiListOfFiles[g].getName().split("\\.(?=[^\\.]+$)");
            if (Arrays.asList(fileextensions).contains(atarashiiTokens[1])) {
                // If the file in directory is of correct type, add the directory to the list as a DataHelper
                DataHelper atarashiiDataHelper = new DataHelper(directoryFile.getName(), "folder", directoryFile.getAbsolutePath(), directoryFile);
                // Make sure you can later identify the DataHelper as a folder
                atarashiiDataHelper.setFolder(true);
                // Make sure you can find the video file later inside the folder for thumbnail and mediainfo creation
                atarashiiDataHelper.setInsideFolderVideoFile(atarashiiListOfFiles[g]);
                answer.add(atarashiiDataHelper);
                break;
            }
        }
    }
}
