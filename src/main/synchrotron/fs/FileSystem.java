package main.synchrotron.fs;

import java.io.File;
import java.util.List;

public interface FileSystem {

	public String getRoot();

	public String getParent(String path);

	public List<String> getChildren(String path);

	public List<String> getAncestors(String path);

	public String getAbsolutePath(String relativePath);

	public String getRelativePath(String absolutePath);

	public void replace(String absolutePathTargetFS, FileSystem fsSource, String absolutePathSourceFS);

	public FileSystem getReference();

	public File createDirectory(String path);

	public void fileCopy(File input, File output) throws Exception;

}
