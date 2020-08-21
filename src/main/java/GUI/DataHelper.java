package GUI;

import java.io.File;

// DataHelper is the class where all the information about a file is stored
// Every entry seen in Up Yours File Table is a DataHelper
// The class is used in classes AddTitle.java, App.java, CreateThumbnail.java, CreateTorrent.java, GetMediainfo.java,
// GettingFiles.java, SentToIMGBB.java and SendToSite.java
public class DataHelper {
    private String fileName;
    private String shortName;
    private String extension;

    private String title;
    private String category;
    private String type;
    private String resolution;
    private String tmdbID;
    private String imdbID;
    private String tvdbID;
    private String malID;
    private String description;
    private boolean anonymous;
    private boolean streamOptimized;
    private boolean sdContent;
    private String absolutePath;

    private boolean internal;
    private boolean thumbnail;
    private int screenshots;

    private String mediainfo;

    private File actualThisFile;
    private File torrentFile;
    private volatile File thumbnailFile;

    private String thumbnailLink;

    private boolean folder;
    private File insideFolderVideoFile;

    public DataHelper(String fileName, String extension, String absolutePath, File actualThisFile) {
        this.fileName = fileName;
        this.shortName = "";
        this.extension = extension;
        this.title = "$FILENAME";
        this.category = "TV";
        this.type = "TS (Raw)";
        this.resolution = "1080i";
        this.tmdbID = "0";
        this.imdbID = "0";
        this.tvdbID = "0";
        this.malID = "0";
        this.description = "";
        this.anonymous = false;
        this.streamOptimized = false;
        this.sdContent = false;

        this.absolutePath = absolutePath;

        this.internal = false;
        this.thumbnail = false;
        this.screenshots = 0;

        this.actualThisFile = actualThisFile;

        this.mediainfo = "";
        this.thumbnailLink = "";

        this.folder = false;

    }

    public DataHelper(String fileName, String shortName, String extension, String title, String category, String type,
                      String resolution, String tmdbID, String imdbID, String tvdbID, String malID, String description,
                      boolean anonymous, boolean streamOptimized, boolean sdContent, String absolutePath,
                      boolean internal, boolean thumbnail, int screenshots) {
        this.fileName = fileName;
        this.shortName = shortName;
        this.extension = extension;
        this.title = title;
        this.category = category;
        this.type = type;
        this.resolution = resolution;
        this.tmdbID = tmdbID;
        this.imdbID = imdbID;
        this.tvdbID = tvdbID;
        this.malID = malID;
        this.description = description;
        this.anonymous = anonymous;
        this.streamOptimized = streamOptimized;
        this.sdContent = sdContent;

        this.absolutePath = absolutePath;

        this.internal = internal;
        this.thumbnail = thumbnail;
        this.screenshots = screenshots;

        this.folder = false;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getTmdbID() {
        return tmdbID;
    }

    public void setTmdbID(String tmdbID) {
        this.tmdbID = tmdbID;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getTvdbID() {
        return tvdbID;
    }

    public void setTvdbID(String tvdbID) {
        this.tvdbID = tvdbID;
    }

    public String getMalID() {
        return malID;
    }

    public void setMalID(String malID) {
        this.malID = malID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isStreamOptimized() {
        return streamOptimized;
    }

    public void setStreamOptimized(boolean streamOptimized) {
        this.streamOptimized = streamOptimized;
    }

    public boolean isSdContent() {
        return sdContent;
    }

    public void setSdContent(boolean sdContent) {
        this.sdContent = sdContent;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(int screenshots) {
        this.screenshots = screenshots;
    }

    public String getMediainfo() {
        return mediainfo;
    }

    public void setMediainfo(String mediainfo) {
        this.mediainfo = mediainfo;
    }

    public File getTorrentFile() {
        return torrentFile;
    }

    public void setTorrentFile(File torrentFile) {
        this.torrentFile = torrentFile;
    }

    public File getActualThisFile() {
        return actualThisFile;
    }

    public void setActualThisFile(File actualThisFile) {
        this.actualThisFile = actualThisFile;
    }

    public File getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public File getInsideFolderVideoFile() {
        return insideFolderVideoFile;
    }

    public void setInsideFolderVideoFile(File insideFolderVideoFile) {
        this.insideFolderVideoFile = insideFolderVideoFile;
    }
}
