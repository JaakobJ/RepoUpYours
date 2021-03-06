package GUI;

import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.swig.create_torrent;
import com.frostwire.jlibtorrent.swig.error_code;
import com.frostwire.jlibtorrent.swig.file_storage;
import com.frostwire.jlibtorrent.swig.libtorrent;

import java.io.*;
import java.util.Map;

// The class for creating torrent out of a video file/folder
// The class is used by App.java
// The methods in the class are using class DataHelper.java
public class CreateTorrent {

    // Method which creates a torrent file out of a video file/folder and stores the torrent file in DataHelper
    // https://programtalk.com/java-api-usage-examples/com.frostwire.jlibtorrent.Entry/
    public void createTorrent(DataHelper upFile) throws IOException {
        File torrentFile = new File(upFile.getActualThisFile().getParent() + "\\" + "tempname" + ".torrent");

        File videoFile = new File(upFile.getAbsolutePath());

        file_storage fs = new file_storage();

        libtorrent.add_files(fs, videoFile.getAbsolutePath());

        create_torrent t = new create_torrent(fs);

        t.set_priv(true);

        t.add_tracker("", 0);

        error_code ec = new error_code();

        libtorrent.set_piece_hashes(t, videoFile.getParent(), ec);

        if (ec.value() != 0) {
            System.out.println(ec.message());
        }

        Entry entry = new Entry(t.generate());
        Map<String, Entry> entryMap = entry.dictionary();
        Entry entryFromUpdatedMap = Entry.fromMap(entryMap);
        final byte[] bencode = entryFromUpdatedMap.bencode();

        FileOutputStream fos;
        fos = new FileOutputStream(torrentFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bencode);
        bos.flush();
        bos.close();
        upFile.setTorrentFile(torrentFile);

    }

}
