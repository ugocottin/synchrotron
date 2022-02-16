package synchrotron.fs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public interface FileSystem {

	public String getRoot();

	/**
	 * Retourne le chemin absolu du parent du chemin passé en paramètre.
	 * @param path un chemin absolu ou local.
	 * @return le parent du chemin
	 */
	@Nullable
	public String getParent(@NotNull String path);


	/**
	 * @param path chemin vers un dossier
	 * @return la liste des fichiers dans le dossier
	 */
	@Nullable
	public List<String> getChildren(@NotNull String path);

	/**
	 * @param path chemin vers un fichier
	 * @return la liste des composants du chemin vers le fichier
	 */
	@NotNull
	public List<String> getAncestors(@NotNull String path);

	/**
	 * @param relativePath un chemin
	 * @return le chemin absolu dans le système de fichier de la machine
	 */
	@NotNull
	public String getAbsolutePath(@NotNull String relativePath);

	/**
	 * @param absolutePath un chemin
	 * @return le chemin relatif dans le système de ficher de la machine, par rapport à la racine
	 */
	@NotNull
	public String getRelativePath(@NotNull String absolutePath);

	public void replace(String absolutePathTargetFS, FileSystem fsSource, String absolutePathSourceFS);

	public File createDirectory(String path);

	public void fileCopy(File input, File output) throws Exception;

}
