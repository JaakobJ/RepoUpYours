import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.swig.create_torrent;
import com.frostwire.jlibtorrent.swig.error_code;
import com.frostwire.jlibtorrent.swig.file_storage;
import com.frostwire.jlibtorrent.swig.libtorrent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class test {

    // THIS IS ONLY FOR TESTING, CAN BE DELETED LATER


    public static void main(String[] args) {
        File torrentFile = new File("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\****.ts.torrent");

        File videoFile = new File("C:\\Users\\Kasutaja\\Downloads\\Variety\\Up\\****.ts");

        file_storage fs = new file_storage();

        libtorrent.add_files(fs, videoFile.getAbsolutePath());

        create_torrent t = new create_torrent(fs);

        t.set_priv(true);

        t.add_tracker("***", 0);

        error_code ec = new error_code();

        libtorrent.set_piece_hashes(t, videoFile.getParent(), ec);

        if (ec.value() != 0) {
            System.out.println(ec.message());
        }

        Entry entry = new Entry(t.generate());
        Map<String, Entry> entryMap = entry.dictionary();
        Entry entryFromUpdatedMap = Entry.fromMap(entryMap);
        final byte[] bencode = entryFromUpdatedMap.bencode();

        try {
            FileOutputStream fos;
            fos = new FileOutputStream(torrentFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(bencode);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            System.out.println("Couldn't write file");
            e.printStackTrace();
        }
        //return torrentFile;
    }

}
