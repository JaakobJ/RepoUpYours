# RepoUpYours

Program with a GUI which uses a SQLite database to store information about shows. The program:
1. Creates a torrent out of video file.
2. Creates a thumbnail out of video file.
3. Gets mediainfo about the video file.
4. Uploads the thumbnail to imgbb and gets a link to the image.
5. Uploads the torrent along with all the information and thumbnail to a hosting site.

### Testing the program

You can find a zip file with a working jar in here: [Up_Yours_jar.zip](https://github.com/JaakobJ/RepoUpYours/blob/master/Up_Yours_jar.zip) . Everything necessary for opening the program is inside. Just extract it to a new folder and open it with Up Yours.jar. This has been tested with jre1.8.0_241 (Java Version 8 Update 241). The 5th part (*Uploads the torrent along with...*) does not work with this .jar. That part has been intentionally commented out here: [src/main/java/GUI/App.java line 296](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/App.java#L296). 

The program can be tested with inserting two values in Settings: "Upload path" which is the folder containing your video files and "imgbb API Token" which you can get for free when registering at [imgbb](https://imgbb.com/). Then insert a random integer > 1 for TMDB ID and press "Upload all". Torrent file and thumbnails will be created to the same folder where your video files are located and the thumbnail is uploaded to your imgbb account.

Database can be tested when first Adding a series, then Reloading and by inserting values on the right side of GUI and pressing Save. The GUI also stores temporary information when changing between between files. After pressing Reload, that information will be lost.


### Using in IDE

For using the database, [src/main/java/lib/sqlite-jdbc-3.30.1.jar](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/sqlite-jdbc-3.30.1.jar) needs to be added as a dependency. The database file is [database.db](https://github.com/JaakobJ/RepoUpYours/blob/master/database.db) . Database needs to be linked in src/main/java/GUI/App.java [line 479](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/App.java#L479). 

For thumbnail creation, [lib/mtn-win32/bin/mtn.exe](https://github.com/JaakobJ/RepoUpYours/blob/master/lib/mtn-win32/bin/mtn.exe) needs to be linked in src/main/java/GUI/CreateThumbnail.java [line 25](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/CreateThumbnail.java#L25) (two places on the same line). mtn.exe is used with Java Runtime/Process.

For creating torrent, [src/main/java/lib/jlibtorrent-windows-1.2.8.0.jar](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/jlibtorrent-windows-1.2.8.0.jar) and [src/main/java/lib/jlibtorrent-1.2.8.0.dll](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/jlibtorrent-1.2.8.0.dll) needs to be added as dependencies.

For getting mediainfo, [lib/MediaInfo.exe](https://github.com/JaakobJ/RepoUpYours/blob/master/lib/MediaInfo.exe) needs to be linked in src/main/java/GUI/GetMediainfo.java [line 16](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/GetMediainfo.java#L16). MediaInfo is used with Java Runtime/Process.

For sending the thumbnail to [imgbb](https://imgbb.com/), an imgbb account and API token is needed. The API token can be entered in Settings in the GUI application.

For classes [src/main/java/GUI/SendToIMGBB.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/SendToIMGBB.java) and [src/main/java/GUI/SendToSite.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/SendToSite.java), [Unirest](http://kong.github.io/unirest-java/) is needed.

The GUI can be opened in IDE with [src/main/java/GUI/App.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/App.java). 

This application wasn't intended to be developed in GitHub. I used IntelliJ IDEA Ultimate and uploaded everything to here. The GUI components were designed with IntelliJ Swing UI Designer.

### Guide
#### What files are allowed?
Currently allowed files are mp4, mkv, ts, avi, flv, divx, m2ts, mov, mpg, mpeg, wmv, xvid and folders that contain at least one of the beforementioned files. In the application, the extension for a folder is folder.

#### Adding Series
There are three ways to add a series:
1. Click on Add Series button, choose the name all the shows in that series have in common and press Add.
2. First enter all the information about the series on the right side of the interface and then click on the Add Series button, choose the name all the shows in that series have in common and press Add.
3. Press Add Series button, click on the series you wish to copy, change the name and/or extension and press Copy/Add. Now the new series has the same information.

After pressing Reload, you can see the Series Name column to fill up if it matches a file name and extension.

#### Entering information about a show
The program has two types of memory: application and database memory. All the information you enter about a file is stored in the application memory. This means that you don't actually have to add a series if you don't want to. The application memory is deleted when you press Reload or close the program. Only when you press Save, the information is stored in database (provided you have previously added the series).

The reason behind this is so you could upload multiple files from the same series: For example three files can have the same series name but different title and description. Also, this makes it possible to upload a file without adding a series at all: For example if it is a one-off show.

#### Tips
There are some useful $ words which you can use in the title and description:
- $FILENAME - This word is changed to the filename when sent to a site
- $DATE - This word is changed to the date found in the filename when sent to a site. Currently working date types are: 20200816 ; 200816 ; 2020-08-16 ; 2020.08.16
- $EP - This word is changed to the episode number in the filename when sent to a site. Currently working date types are: S01E01 ; EP01 ; Ep01 ; E01 (it isn't limited to just two numbers, it can also be S123E456 or E78910, etc.)
