package synchrotron.fs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystem implements FileSystem {

	private final Path rootPath;

	public LocalFileSystem(String root) {
		this.rootPath = Paths.get(root).normalize();
	}

	@Override
	public String getRoot() {
		return this.rootPath.toString();
	}

	private Path getRootPath() {
		return this.rootPath;
	}

	@Override @Nullable
	public String getParent(@NotNull String pathString) {
		Path path = Paths.get(pathString).normalize();
        if(!path.isAbsolute()) {
	        path = Paths.get(getAbsolutePath(path.toString())).normalize();
		}

		if (path.equals(this.getRootPath())) return null;
		Path parentPath = path.getParent();

		if (parentPath == null) return null;
		return parentPath.toString();
	}

	@Override
	public @NotNull List<String> getChildren(@NotNull String pathString) {
		return null;
	}

	@Override
	public @NotNull List<String> getAncestors(@NotNull String pathString) {
		final String absolutePath = this.getAbsolutePath(pathString);
		Path path = Paths.get(absolutePath);

		List<String> ancestors = new ArrayList<>();
		final int ancestorsCount = path.getNameCount();
		for(int ancestorIndex = 0; ancestorIndex < ancestorsCount; ancestorIndex++) {
			ancestors.add(path.getName(ancestorIndex).toString());
		}

		return ancestors;
	}

	@Override
	public @NotNull String getAbsolutePath(@NotNull String pathString) {
		final Path path = Paths.get(pathString);
		if (path.isAbsolute()) {
			return path.toString();
		}

		final Path absolutePath = Paths.get(this.getRoot(), path.toString());

		return absolutePath.toString();
	}

	@Override
	public @NotNull String getRelativePath(@NotNull String pathString) {
		final Path path = Paths.get(pathString);
		if (!path.isAbsolute()) {
			return path.toString();
		}

		return this.getRootPath().relativize(path).toString();

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
