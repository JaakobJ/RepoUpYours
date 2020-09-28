package GUI;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// The class for sending thumbnail to imgbb image hosting site
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class SendToIMGBB {

    // Method which sends the DataHelper's thumbnail to your imgbb account (imgbb_api) and stores the link
    // to the imgbb image in DataHelper
    // The method returns the message received from imgbb
    public String send(DataHelper show, String imgbb_api) {
        // The message which is returned when method is completed/failed
        StringBuilder messageToReturn = new StringBuilder();

        int counter = 0;
        while (!show.getThumbnailFile().exists()) {
            // In case the method is called before CreateThumbnail.java class has been able to finish the thumbnail
            // and store it in DataHelper, wait for 300 milliseconds and check again!
            try {
                if (counter == 100) {
                    // If 30 seconds have been waited, something must have gone wrong -> break from method
                    messageToReturn.append("ERROR! Unable to locate thumbnail picture, will not attempt to send it to imgbb.\n");
                    return messageToReturn.toString();
                }
                Thread.sleep(300);
                counter++;
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        messageToReturn.append("Thumbnail created for file " + show.getFileName() + "." + show.getExtension() + "\n");

        Unirest.config().reset();
        Unirest.config().socketTimeout(2000).connectTimeout(5000);

        // Send thumbnail image to imgbb
        HttpResponse<String> response = Unirest.post("https://api.imgbb.com/1/upload?key=" + imgbb_api)
                .field("image", show.getThumbnailFile()).asString();

        messageToReturn.append(response.getBody() + "\n");

        // Parse the response with regex to find direct image link
        String sone = response.getBody();
        Pattern pattern = Pattern.compile("(?<=\"url\":\")https:.+?(?=\")");
        Matcher matcher = pattern.matcher(sone);

        if (matcher.find()) {
            // Change the link into readable format
            String oldlink = matcher.group(0);
            String newlink = oldlink.replaceAll("\\\\", "");
            show.setThumbnailLink(newlink);
        }
        return messageToReturn.toString();
    }
}
