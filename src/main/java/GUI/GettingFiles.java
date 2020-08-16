package GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GettingFiles {
    private String path;

    private final String[] fileextensions = {"mp4", "mkv", "ts", "avi", "flv", "divx", "m2ts", "mov", "mpg", "mpeg",
            "wmv", "xvid"};

    List<DataHelper> answer = new ArrayList<>();

    public GettingFiles(String path) {
        this.path = path;
    }

    public List<DataHelper> getNames() {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                handleDirectory(listOfFiles[i]);
            }
            else if (listOfFiles[i].isFile()) {
                String[] tokens = listOfFiles[i].getName().split("\\.(?=[^\\.]+$)");
                if (Arrays.asList(fileextensions).contains(tokens[1])) {
                    answer.add(new DataHelper(tokens[0], tokens[1], listOfFiles[i].getAbsolutePath(), listOfFiles[i]));
                }
            }
        }
        return answer;
    }

    private void handleDirectory(File directoryFile) {
        File[] atarashiiListOfFiles = directoryFile.listFiles();
        for (int g = 0; g < atarashiiListOfFiles.length; g++) {
            String[] atarashiiTokens = atarashiiListOfFiles[g].getName().split("\\.(?=[^\\.]+$)");
            if (Arrays.asList(fileextensions).contains(atarashiiTokens[1])) {
                DataHelper atarashiiDataHelper = new DataHelper(directoryFile.getName(), "folder", directoryFile.getAbsolutePath(), directoryFile);
                atarashiiDataHelper.setFolder(true);
                atarashiiDataHelper.setInsideFolderVideoFile(atarashiiListOfFiles[g]);
                answer.add(atarashiiDataHelper);
                break;
            }
        }
    }


    /*public static void main(String[] args) {
        String[] fileextensions = {"mp4", "mkv", "ts", "avi", "flv", "divx", "m2ts", "mov", "mpg", "mpeg",
                "wmv", "xvid"};

        GettingFiles smth = new GettingFiles("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up");
        File[] listOfFiles = smth.getFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String[] tokens = listOfFiles[i].toString().split("\\.(?=[^\\.]+$)");
                if (Arrays.asList(fileextensions).contains(tokens[1]))
                System.out.println("File " + listOfFiles[i].getName());
            }
        }

    }*/
}
