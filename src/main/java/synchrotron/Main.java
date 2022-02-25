package synchrotron;

import sun.misc.Signal;
import synchrotron.synchronizer.Synchronizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Main {

    public static void main(String[] args) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        Path firstPath = Paths.get("/", "tmp", "public");
        Path secondPath = Paths.get("/", "tmp", "public_copy");
        final Synchronizer synchronizer = new Synchronizer(messageDigest);

        Signal.handle(new Signal("INT"), signal -> synchronizer.stop());

        synchronizer.synchronize(firstPath, secondPath);

        try {
            synchronizer.waitForExit();
        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
        } finally {
            synchronizer.stop();
        }

        System.exit(0);
    }
}
