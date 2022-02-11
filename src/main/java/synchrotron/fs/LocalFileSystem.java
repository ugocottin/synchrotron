package synchrotron.fs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalFileSystem implements FileSystem {

	private final String root;

	public LocalFileSystem(String root) {
		this.root = root;
	}

	@Override
	public String getRoot() {
		return this.root;
	}

	@Override
	public String getParent(String path) {
		return null;
	}

	@Override
	public List<String> getChildren(String path) {
		return null;
	}

	@Override
	public List<String> getAncestors(String path) {
		return null;
	}

	@Override
	public String getAbsolutePath(String relativePath) {
		final Path absolutePath = Paths.get(this.root, relativePath);

		return absolutePath.toString();
	}

	@Override
	public String getRelativePath(String absolutePath) {
		return null;
	}

	@Override
	public void replace(String absolutePathTargetFS, FileSystem fsSource, String absolutePathSourceFS) {

	}

	@Override
	public File createDirectory(String path) {
		return null;
	}

	@Override
	public void fileCopy(File input, File output) throws Exception {

	}
}
