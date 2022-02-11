package synchrotron.fs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalFileSystem implements FileSystem {

	private final String root;

	/**
	 * Instancie un système de fichier local dont la racine est passée en argument
	 * @param root Chemin vers la racine du système de fichier
	 * @implNote la racine est virtuelle, on peut définir la racine comme un répertoire,
	 qui agira comme une limite dans la hiérarchie du système.
	 */
	public LocalFileSystem(String root) {
		this.root = root;
	}

	/**
	 * @return la racine du système de fichier
	 */
	@Override
	public String getRoot() {
		return this.root;
	}

	@Override
	public String getParent(String pathString) {
		Path path = Paths.get(pathString);
        if(!path.isAbsolute()) {
	        path = Paths.get(getAbsolutePath(path.toString()));
		}
		Path parentPath = path.getParent();

		if (parentPath == null) return this.root;
		return parentPath.toString();
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
		final Path pathAbsolute = Paths.get(absolutePath);
		final Path pathRoot = Paths.get(this.root);

		return pathRoot.relativize(pathAbsolute).toString();

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
