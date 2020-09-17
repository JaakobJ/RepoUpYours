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

        /*String sone = "John's Car";

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
        */

        /*String word1 = "20200815";
        String word2 = "200815";
        String word3 = "2020-08-15";
        String word4 = "2020.08.15";

        System.out.println(formatDate(word1));
        System.out.println(formatDate(word2));
        System.out.println(formatDate(word3));
        System.out.println(formatDate(word4));*/

        String word1 = "EP123";
        String word2 = "Ep01";
        String word3 = "E22";

        System.out.println(formatEP(word1));
        System.out.println(formatEP(word2));
        System.out.println(formatEP(word3));

        String word4 = "S01E01";
        String word5 = "Season 11 EP01";
        String word6 = "Season 11 Ep01";
        String word7 = "Season 11 E01";

        System.out.println(formatSeasonEP(word4));
        System.out.println(formatSeasonEP(word5));
        System.out.println(formatSeasonEP(word6));
        System.out.println(formatSeasonEP(word7));
    }

    private static String formatDate(String filename) {
        Pattern pattern1 = Pattern.compile("(\\d){8}"); // 20200815
        Matcher matcher1 = pattern1.matcher(filename);
        if (matcher1.find()) {
            return matcher1.group().substring(0, 4) + "-" + matcher1.group().substring(4, 6) + "-"
                    + matcher1.group().substring(6, 8);
        }
        Pattern pattern2 = Pattern.compile("(\\d{6})"); // 200815
        Matcher matcher2 = pattern2.matcher(filename);
        if (matcher2.find()) {
            return "20" + matcher2.group().substring(0, 2) + "-" + matcher2.group().substring(2, 4) + "-"
                    + matcher2.group().substring(4, 6);
        }
        Pattern pattern3 = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})"); // 2020-08-15
        Matcher matcher3 = pattern3.matcher(filename);
        if (matcher3.find()) {
            return matcher3.group();
        }
        Pattern pattern4 = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2})"); // 2020.08.15
        Matcher matcher4 = pattern4.matcher(filename);
        if (matcher4.find()) {
            return matcher4.group().replaceAll("\\.", "-");
        }
        return "";
    }

    private static String formatEP(String filename) {
        Pattern pattern1 = Pattern.compile("(E[Pp]\\d+)"); // EP01 & Ep01
        Matcher matcher1 = pattern1.matcher(filename);
        if (matcher1.find()) {
            return "S01" + matcher1.group().replace("P", "").replace("p", "");
        }
        Pattern pattern2 = Pattern.compile("(E\\d+)"); // E01
        Matcher matcher2 = pattern2.matcher(filename);
        if (matcher2.find()) {
            return "S01" + matcher2.group();
        }
        return "";
    }

    private static String formatSeasonEP(String filename) {
        Pattern pattern1 = Pattern.compile("(S\\d+E\\d+)"); // S01E01
        Matcher matcher1 = pattern1.matcher(filename);
        if (matcher1.find()) {
            return matcher1.group();
        }
        Pattern pattern2 = Pattern.compile("Season \\d+ (E[Pp]\\d+)"); // Season 1 EP01 & Season 1 Ep01
        Matcher matcher2 = pattern2.matcher(filename);
        if (matcher2.find()) {
            String returnWord = matcher2.group();
            if (returnWord.charAt(8) == ' ') {
                returnWord = returnWord.replace("Season ", "S0");
            }
            else {
                returnWord = returnWord.replace("Season ", "S");
            }
            return returnWord.replace(" EP", "E").replace(" Ep", "E");
        }
        Pattern pattern3 = Pattern.compile("Season \\d+ (E\\d+)"); // Season 1 E01
        Matcher matcher3 = pattern3.matcher(filename);
        if (matcher3.find()) {
            String returnWord = matcher3.group();
            if (returnWord.charAt(8) == ' ') {
                returnWord = returnWord.replace("Season ", "S0");
            }
            else {
                returnWord = returnWord.replace("Season ", "S");
            }
            return returnWord.replace(" E", "E");
        }
        return "";
    }
}
