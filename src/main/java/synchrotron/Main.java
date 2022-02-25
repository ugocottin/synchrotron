package synchrotron;

import org.jetbrains.annotations.NotNull;
import synchrotron.synchronizer.Repository;
import synchrotron.synchronizer.RepositoryChange;
import synchrotron.synchronizer.Synchronizer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;

public class Main {

    public static final int timeout = 1_000;

    public static void main(String[] args) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        Path firstPath = Paths.get("/", "tmp", "public");
        Path secondPath = Paths.get("/", "tmp", "public_copy");
        final Synchronizer synchronizer = new Synchronizer(messageDigest);

        synchronizer.synchronize(firstPath, secondPath);

        synchronizer.waitAndExit();

//        while (true) {
//            repository = repository.getSnapshot();
//            Main.printChanges(repository);
//            Thread.sleep(Main.timeout);
//        }
    }

    private static void printChanges(@NotNull Repository repository) {
        Map<File, RepositoryChange> changes = repository.getChanges();
        Set<File> files = changes.keySet();

        if (files.isEmpty()) { return; }

        for (File file : files) {
            RepositoryChange change = changes.get(file);
            Path absolutePath = repository.getRootPath().resolve(file.toPath());
            System.out.println(change + "\t" + absolutePath);
        }
    }
}
