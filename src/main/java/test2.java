import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test2 {

    public static void main(String[] args) {
        //String sone = "{\"success\":true,\"data\":\"",\"message\":\"Torrent uploaded successfully.\"}";

        //Pattern pattern = Pattern.compile("https:.+?(?=\")");
        //Matcher matcher = pattern.matcher(sone);
        //matcher.find();
        //System.out.println(matcher.group());

        //String oldlink = "";
        //String newlink = oldlink.replaceAll("\\\\", "");
        //System.out.println(newlink);

        String sone = "John's Car";

        System.out.println(sone.replaceAll("'", "''"));

        Pattern pattern = Pattern.compile("((\\d){8}|(\\d{6})|(\\d{4}-\\d{2}-\\d{2})|(\\d{4}\\.\\d{2}\\.\\d{2}))");
        Matcher matcher = pattern.matcher("text 20200815 text text 2020-08-15 text.mp4");
        matcher.find();
        System.out.println(matcher.group());

        Pattern pattern2 = Pattern.compile("((S\\d+E\\d+)|(E[Pp]\\d+)|(E\\d+))");
        Matcher matcher2 = pattern2.matcher("text text text text S01E01 text text.mp4");
        matcher2.find();
        System.out.println(matcher2.group());

        System.out.println("Working Directory = " + System.getProperty("user.dir"));


    }
}
