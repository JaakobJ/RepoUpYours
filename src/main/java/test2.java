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



    }
}
