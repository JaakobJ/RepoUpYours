package GUI;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendToIMGBB {

    public String send(DataHelper show, String imgbb_api) {
        StringBuilder messageToReturn = new StringBuilder();
        int counter = 0;
        while (!show.getThumbnailFile().exists()) {
            try {
                if (counter == 100) {
                    // If 30 seconds have been waited -> break
                    messageToReturn.append("ERROR! Unable to locate thumbnail picture, will not attempt to send it to imgbb.");
                    return messageToReturn.toString();
                }
                Thread.sleep(300);
                counter++;
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        Unirest.config().reset();
        Unirest.config().socketTimeout(2000).connectTimeout(5000);

        HttpResponse<String> response = Unirest.post("https://api.imgbb.com/1/upload?key=" + imgbb_api)
                .field("image", show.getThumbnailFile()).asString();

        messageToReturn.append(response.getBody() + "\n");

        String sone = response.getBody();
        Pattern pattern = Pattern.compile("(?<=\"url\":\")https:.+?(?=\")");
        Matcher matcher = pattern.matcher(sone);

        if (matcher.find()) {
            String oldlink = matcher.group(0);
            String newlink = oldlink.replaceAll("\\\\", "");
            show.setThumbnailLink(newlink);
        }
        return messageToReturn.toString();
    }

    /*public static void main(String[] args) {
        HttpResponse<String> response = Unirest.post("https://api.imgbb.com/1/upload?key=****")
                .field("image", new File("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\*****_s.jpg")).asString();

        System.out.println(response.getBody());

        String sone = response.getBody();
        Pattern pattern = Pattern.compile("(?<=\"url\":\")https:.+?(?=\")");
        Matcher matcher = pattern.matcher(sone);

        if (matcher.find()) {
            String oldlink = matcher.group(0);
            String newlink = oldlink.replaceAll("\\\\", "");
            System.out.println(newlink);
        }


    }*/

}
