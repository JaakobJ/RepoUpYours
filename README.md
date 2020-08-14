# RepoUpYours

Program with a GUI which uses a SQLite database to store information about shows. The program:
- Creates a torrent out of video file.
- Creates a thumbnail out of video file.
- Gets mediainfo about the video file.
- Uploads the thumbnail to imgbb and gets a link to the image.
- Uploads the torrent along with all the information and thumbnail to a hosting site.

For using the database, [src/main/java/lib/sqlite-jdbc-3.30.1.jar Database](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/sqlite-jdbc-3.30.1.jar) needs to be added as a dependency. The database file is [src/main/java/RepoUpYours.db](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/upyours.db) . Database needs to be linked in src/main/java/GUI/App.java [line 460](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/App.java#L460). 

For thumbnail creation, [src/main/java/lib/mtn-win32/bin/mtn.exe](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/mtn-win32/bin/mtn.exe) needs to be linked in src/main/java/GUI/CreateThumbnail.java [line 15](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/CreateThumbnail.java#L15).

For creating torrent, [src/main/java/lib/jlibtorrent-windows-1.2.8.0.jar](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/jlibtorrent-windows-1.2.8.0.jar) and [RepoUpYours/src/main/java/lib/jlibtorrent-1.2.8.0.dll](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/jlibtorrent-1.2.8.0.dll) needs to be added as dependencies.

For getting mediainfo, [RepoUpYours/src/main/java/lib/mtn-win32/bin/mtn.exe](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/lib/mtn-win32/bin/mtn.exe)  needs to be linked in src/main/java/GUI/GetMediainfo.java [line 11](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/GetMediainfo.java#L11).

For sending the thumbnail to [imgbb](https://imgbb.com/), an imgbb account and API token is needed. The API token can be entered in Settings in the application.

For classes [src/main/java/GUI/SendToIMGBB.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/SendToIMGBB.java) and [src/main/java/GUI/SendToSite.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/SendToSite.java), [Unirest](http://kong.github.io/unirest-java/) is needed.

The GUI can be opened with [src/main/java/GUI/App.java](https://github.com/JaakobJ/RepoUpYours/blob/master/src/main/java/GUI/App.java). You can test the GUI when specifying a folder (in the Settings) that contains a video file.

This application wasn't intended to be developed in GitHub. I used IntelliJ IDEA Ultimate and uploaded everything to here. THe GUI was designed with IntelliJ Swing UI Designer.

