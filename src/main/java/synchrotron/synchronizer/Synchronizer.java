package synchrotron.synchronizer;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import synchrotron.fs.FileSystem;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Synchronizer {

    private final Map<File, byte[]> filesHash = new HashMap<>();

    public void synchronize(FileSystem fs1, FileSystem fs2) {
        List<String> dirtyPaths1 = computeDirty(fs1, fs1, "");
        List<String> dirtyPaths2 = computeDirty(fs2, fs2, "");
        reconcile(fs1, dirtyPaths1, fs2, dirtyPaths2, "");
    }

    public void reconcile(FileSystem fs1, List<String> dirtyPaths1, FileSystem fs2, List<String> dirtyPaths2, String currentRelativePath) {

    }

    public List<String> computeDirty(FileSystem lastSync, FileSystem fs, String currentRelativePath) {
        return null;
    }

    public void doSomething(@NotNull File file, @NotNull MessageDigest messageDigest) throws IOException {
        this.computeHash(file, messageDigest);
        this.printHashes();
    }

    private void printHashes() {
        System.out.println("Showing files hashes for " + this.filesHash.size() + " entries:");
        for (Map.Entry<File, byte[]> entry : this.filesHash.entrySet()) {
            final File file = entry.getKey();
            final byte[] hash = entry.getValue();
            System.out.println(file.toString() + ": " + DatatypeConverter.printHexBinary(hash));
        }
    }
    
    private void computeHash(@NotNull File file, @NotNull MessageDigest messageDigest) throws IOException {
        if (file.isFile()) {
            final byte[] hash = getHash(file, messageDigest);
            this.filesHash.put(file, hash);
        } else {
            @Nullable final File[] children = file.listFiles();
            if (children == null) return;

            for (File child: children) {
                if (child == null) continue;
                computeHash(child, messageDigest);
            }
        }
    }

    private byte[] getHash(@NotNull File file, @NotNull MessageDigest messageDigest) throws IOException {
        System.out.println("Get hash of file " + file);
        try (InputStream inputStream = new FileInputStream(file); DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
            int readedBytes;
            do {
                readedBytes = digestInputStream.read();
            } while (readedBytes != -1);
        }

        return messageDigest.digest();
    }
}