package GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetMediainfo {

    public void addMediainfoToDataHelper(DataHelper show) throws IOException {
        String videoFilePath = show.getAbsolutePath();
        if (show.isFolder()) {
            videoFilePath = show.getInsideFolderVideoFile().getAbsolutePath();
        }

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("\""+ System.getProperty("user.dir") + "\\lib\\MediaInfo.exe\" \"" + videoFilePath + "\"");

        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        StringBuilder mediainfo = new StringBuilder();
        String line;
        while((line=input.readLine()) != null) {
            mediainfo.append(line + "\n");
        }
        show.setMediainfo(mediainfo.toString());
    }

    /*public static void main(String[] args) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("\"C:\\Users\\Kasutaja\\Downloads\\SQLite\\MediaInfo_CLI_20.08_Windows_x64\\MediaInfo.exe\" \"C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\***.mp4\"");

        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        StringBuilder mediainfo = new StringBuilder();

        String line;

        while((line=input.readLine()) != null) {
            mediainfo.append(line + "\n");
        }

        System.out.println(mediainfo);
    }*/
}
