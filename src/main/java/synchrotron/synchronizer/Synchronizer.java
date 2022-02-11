package synchrotron.synchronizer;


import synchrotron.fs.FileSystem;

import java.util.List;

public class Synchronizer {
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

}