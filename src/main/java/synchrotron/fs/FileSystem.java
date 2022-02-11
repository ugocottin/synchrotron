package synchrotron.fs;

import java.io.File;
import java.util.List;

public interface FileSystem {

	public String getRoot();

	/**
	 * Retourne le chemin absolu du parent du chemin passé en paramètre.
	 * @param path un chemin absolu ou local.
	 * @return le parent du chemin
	 */
	public String getParent(String path);

	public List<String> getChildren(String path);

	public List<String> getAncestors(String path);

	public String getAbsolutePath(String relativePath);

	public String getRelativePath(String absolutePath);

	public void replace(String absolutePathTargetFS, FileSystem fsSource, String absolutePathSourceFS);

	public File createDirectory(String path);

	public void fileCopy(File input, File output) throws Exception;

}
