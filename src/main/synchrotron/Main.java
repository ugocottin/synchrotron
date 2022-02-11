package main.synchrotron;

import main.synchrotron.fs.FileSystem;
import main.synchrotron.fs.LocalFileSystem;

public class Main {

    public static void main(String[] args) {
	    final FileSystem fileSystem = new LocalFileSystem("/Users/ugocottin/Desktop");
        final String fileName = "cle/Capture.PNG";
        final String absolutePath = fileSystem.getAbsolutePath(fileName);

        System.out.println(absolutePath);
        System.out.println(fileSystem.getParent("bobo/toto.txt"));
    }
}
