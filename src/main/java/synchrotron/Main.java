package synchrotron;

import synchrotron.fs.LocalFileSystem;
import synchrotron.fs.FileSystem;

public class Main {

    public static void main(String[] args) {
	    final FileSystem fileSystem = new LocalFileSystem("C://Users/ugocottin/Desktop");
        final String fileName = "cle/Capture.PNG";
        final String absolutePath = fileSystem.getAbsolutePath(fileName);

        System.out.println(absolutePath);
        System.out.println(fileSystem.getParent(absolutePath));
    }
}