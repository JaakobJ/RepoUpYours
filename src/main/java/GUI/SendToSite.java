package GUI;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendToSite {
    Map<String, String> categoryMap = new HashMap<String, String>() {{
        put("Movies", "1");
        put("TV", "2");
        put("TS Firehose", "6");
        put("Anime Movies", "7");
        put("Music", "8");
        put("Anime Shows", "9");
    }};

    Map<String, String> typeMap = new HashMap<String, String>() {{
        put("BluRay", "3");
        put("DVD", "1");
        put("HDTV", "6");
        put("WEB-DL", "4");
        put("WebRip", "5");
        put("TS (Raw)", "14");
        put("Re-encode", "15");
    }};

    Map<String, String> resolutionMap = new HashMap<String, String>() {{
        put("4320p", "1");
        put("2160p", "2");
        put("1080p", "3");
        put("1080i", "4");
        put("720p", "5");
        put("576p", "6");
        put("576i", "7");
        put("480p", "8");
        put("480i", "9");
        put("Other", "10");
    }};

    public String send(DataHelper show, String user_id, String api_token) {
        StringBuilder messageToReturn = new StringBuilder();
        int counter = 0;
        while (!show.getThumbnailLink().isEmpty() && !show.getTorrentFile().exists()) {
            try {
                if (counter == 100) {
                    // If 30 seconds have been waited -> break
                    messageToReturn.append("ERROR! Unable to locate thumbnail picture and/or torrent file, will not attempt to send them to *****.");
                    return messageToReturn.toString();
                }
                Thread.sleep(300);
                counter++;
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        String builtTitle = show.getTitle().replaceAll("\\$FILENAME", show.getFileName());
        String builtDescription = show.getDescription().replaceAll("\n", "<br>");

        builtDescription = builtDescription.replaceAll("\\$FILENAME", show.getFileName());

        // Matches first 20200815 ; 200815 ; 2020-08-15 ; 2020.08.15
        Pattern pattern1 = Pattern.compile("((\\d){8}|(\\d{6})|(\\d{4}-\\d{2}-\\d{2})|(\\d{4}\\.\\d{2}\\.\\d{2}))");
        Matcher matcher1 = pattern1.matcher(show.getFileName());
        if (matcher1.find()) {
            builtTitle = builtTitle.replaceAll("\\$DATE", matcher1.group());
            builtDescription = builtDescription.replaceAll("\\$DATE", matcher1.group());
        }

        // Matches first S01E01 ; EP01 ; Ep01 ; E01 (there can be however many numbers you want)
        Pattern pattern2 = Pattern.compile("((S\\d+E\\d+)|(E[Pp]\\d+)|(E\\d+))");
        Matcher matcher2 = pattern2.matcher(show.getFileName());
        if (matcher2.find()) {
            builtTitle = builtTitle.replaceAll("\\$EP", matcher2.group());
            builtDescription = builtDescription.replaceAll("\\$EP", matcher2.group());
        }

        // Matches #01 ; #001 (there can be however many numbers you want)
        Pattern pattern3 = Pattern.compile("#\\d+");
        Matcher matcher3 = pattern3.matcher(show.getFileName());
        if (matcher3.find()) {
            builtTitle = builtTitle.replaceAll("\\$#", matcher3.group());
            builtDescription = builtDescription.replaceAll("\\$#", matcher3.group());
        }

        if (!show.getThumbnailLink().isEmpty()) {
            builtDescription = builtDescription + "<br><br>[img]" + show.getThumbnailLink() + "[/img]";
        }

        Unirest.config().reset();
        Unirest.config().socketTimeout(2000).connectTimeout(5000);

        HttpResponse<String> response = Unirest.post("*****/api/torrents/upload?api_token=" + api_token)
                .field("torrent", show.getTorrentFile())
                .field("nfo", "")
                .field("name", builtTitle)
                .field("description", builtDescription)
                .field("mediainfo", show.getMediainfo())
                .field("category_id", categoryMap.get(show.getCategory()))
                .field("type_id", typeMap.get(show.getType()))
                .field("resolution_id", resolutionMap.get(show.getResolution()))
                .field("user_id", user_id)
                .field("tmdb", show.getTmdbID())
                .field("imdb", show.getImdbID())
                .field("tvdb", show.getTvdbID())
                .field("mal", show.getMalID())
                .field("igdb", "0")
                .field("anonymous", show.isAnonymous() ? "1" : "0")
                .field("stream", show.isStreamOptimized() ? "1" : "0")
                .field("sd", show.isSdContent() ? "1" : "0")
                .field("internal", show.isInternal() ? "1" : "0")
                .asString();

        messageToReturn.append(response.getBody() + "\n");

        String sone = response.getBody();
        Pattern pattern = Pattern.compile("https:.+?(?=\")");
        Matcher matcher = pattern.matcher(sone);

        if (matcher.find()) {
            String oldlink = matcher.group();
            String newlink = oldlink.replaceAll("\\\\", "");

            File result = Unirest.get(newlink)
                    .asFile(show.getTorrentFile().getParent() + "\\" + "[******]" + builtTitle.replaceAll(" ", ".") + ".torrent")
                    .getBody();
            messageToReturn.append("Torrent downloaded from *****" + "\n");
        }

        if (show.getTorrentFile().delete()) {
            messageToReturn.append("Old torrent file deleted" + "\n");
        }
        if (show.getThumbnailFile().delete()) {
            messageToReturn.append("Thumbnail deleted from PC" + "\n");
        }

        return(messageToReturn.toString());
    }

    /*public static void main(String []args) {

        File torrentFile = new File("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\200801 aaa.mp4.torrent");

        System.out.println(torrentFile.exists());


        HttpResponse<String> response = Unirest.post("****")
                .field("torrent", torrentFile)
                .field("nfo", "")
                .field("name", "Hamachan ga! testtest")
                .field("description", "Encoded 720p with CMs cut out.")
                .field("mediainfo", "General\n" +
                        "    Complete name                            : Files\\\\***.mp4\n" +
                        "    Format                                   : MPEG-4\n" +
                        "    Format profile                           : Base Media\n" +
                        "    Codec ID                                 : isom (isom/iso2/avc1/mp41)\n" +
                        "    File size                                : 319 MiB\n" +
                        "    Duration                                 : 21 min 59 s\n" +
                        "    Overall bit rate                         : 2 027 kb/s\n" +
                        "    Writing application                      : Lavf57.66.101\n" +
                        "    \n" +
                        "    Video\n" +
                        "    ID                                       : 1\n" +
                        "    Format                                   : AVC\n" +
                        "    Format/Info                              : Advanced Video Codec\n" +
                        "    Format profile                           : High@L3.1\n" +
                        "    Format settings                          : CABAC / 4 Ref Frames\n" +
                        "    Format settings, CABAC                   : Yes\n" +
                        "    Format settings, Reference frames        : 4 frames\n" +
                        "    Codec ID                                 : avc1\n" +
                        "    Codec ID/Info                            : Advanced Video Coding\n" +
                        "    Duration                                 : 21 min 59 s\n" +
                        "    Bit rate                                 : 1 875 kb/s\n" +
                        "    Width                                    : 1 280 pixels\n" +
                        "    Height                                   : 720 pixels\n" +
                        "    Display aspect ratio                     : 16:9\n" +
                        "    Frame rate mode                          : Constant\n" +
                        "    Frame rate                               : 29.970 (30000/1001) FPS\n" +
                        "    Color space                              : YUV\n" +
                        "    Chroma subsampling                       : 4:2:0\n" +
                        "    Bit depth                                : 8 bits\n" +
                        "    Scan type                                : Progressive\n" +
                        "    Bits/(Pixel*Frame)                       : 0.068\n" +
                        "    Stream size                              : 295 MiB (93%)\n" +
                        "    Writing library                          : x264 core 148\n" +
                        "    Encoding settings                        : cabac=1 / ref=3 / deblock=1:0:0 / analyse=0x3:0x113 / me=hex / subme=7 / psy=1 / psy_rd=1.00:0.00 / mixed_ref=1 / me_range=16 / chroma_me=1 / trellis=1 / 8x8dct=1 / cqm=0 / deadzone=21,11 / fast_pskip=1 / chroma_qp_offset=-2 / threads=6 / lookahead_threads=1 / sliced_threads=0 / nr=0 / decimate=1 / interlaced=0 / bluray_compat=0 / constrained_intra=0 / bframes=3 / b_pyramid=2 / b_adapt=1 / b_bias=0 / direct=1 / weightb=1 / open_gop=0 / weightp=2 / keyint=250 / keyint_min=25 / scenecut=40 / intra_refresh=0 / rc_lookahead=40 / rc=crf / mbtree=1 / crf=23.0 / qcomp=0.60 / qpmin=0 / qpmax=69 / qpstep=4 / ip_ratio=1.40 / aq=1:1.00\n" +
                        "    Language                                 : Japanese\n" +
                        "    Codec configuration box                  : avcC\n" +
                        "    \n" +
                        "    Audio\n" +
                        "    ID                                       : 2\n" +
                        "    Format                                   : AAC LC\n" +
                        "    Format/Info                              : Advanced Audio Codec Low Complexity\n" +
                        "    Codec ID                                 : mp4a-40-2\n" +
                        "    Duration                                 : 21 min 59 s\n" +
                        "    Duration_LastFrame                       : -13 ms\n" +
                        "    Bit rate mode                            : Constant\n" +
                        "    Bit rate                                 : 144 kb/s\n" +
                        "    Channel(s)                               : 2 channels\n" +
                        "    Channel layout                           : L R\n" +
                        "    Sampling rate                            : 48.0 kHz\n" +
                        "    Frame rate                               : 46.875 FPS (1024 SPF)\n" +
                        "    Compression mode                         : Lossy\n" +
                        "    Stream size                              : 22.5 MiB (7%)\n" +
                        "    Language                                 : Japanese\n" +
                        "    Default                                  : Yes\n" +
                        "    Alternate group                          : 1\n" +
                        "    \n" +
                        "    \n" +
                        "    )")
                .field("category_id", "2")
                .field("type_id", "6")
                .field("resolution_id", "5")
                .field("user_id", "3144")
                .field("tmdb", "106408")
                .field("imdb", "5712614")
                .field("tvdb", "385178")
                .field("mal", "0")
                .field("igdb", "0")
                .field("anonymous", "0")
                .field("stream", "0")
                .field("sd", "0")
                .field("internal", "0")
                .asString();

        System.out.println(response.getBody());

        String sone = response.getBody();
        Pattern pattern = Pattern.compile("https:.+?(?=\")");
        Matcher matcher = pattern.matcher(sone);
        matcher.find();
        String oldlink = matcher.group();
        String newlink = oldlink.replaceAll("\\\\", "");

        File result = Unirest.get(newlink)
                .asFile("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\New folder\\aaa.torrent")
                .getBody();


    }*/

}
