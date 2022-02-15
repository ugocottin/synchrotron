package synchrotron;

import synchrotron.fs.FileSystem;
import synchrotron.fs.LocalFileSystem;
import synchrotron.fs.UpdateDetector;

import java.io.File;
import java.security.MessageDigest;

public class Main {

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/ugocottin/Desktop/changes.patch");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        UpdateDetector updateDetector = new UpdateDetector(messageDigest);
        System.out.println(updateDetector.getHash(file));

        final FileSystem fileSystem = new LocalFileSystem("C://Users/ugocottin/Desktop");
        final String fileName = "cle/Capture.PNG";
        final String absolutePath = fileSystem.getAbsolutePath(fileName);

        System.out.println(absolutePath);
        System.out.println(fileSystem.getParent(absolutePath));
    }
}
