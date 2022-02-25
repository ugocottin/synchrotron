package synchrotron.synchronizer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;

public class Repository {

	/**
	 * Path to the root of the repository
	 */
	@NotNull
	private final Path rootPath;

	/**
	 * Hashes of files in the repository
	 */
	@Nullable
	private Map<File, byte[]> hashes;

	/**
	 * Algorithm used to compute hashes
	 */
	@NotNull
	private final MessageDigest messageDigest;

	/**
	 * Previous snapshot of the repository
	 */
	@Nullable
	private Repository previousSnapshot;

	/**
	 * Create a repository
	 * @param rootPath path to the repository root
	 * @param messageDigest algorithm to compute hashes
	 */
	public Repository(@NotNull Path rootPath, @NotNull MessageDigest messageDigest) {
		this.rootPath = rootPath;
		this.hashes = null;
		this.messageDigest = messageDigest;
		this.previousSnapshot = null;
	}

	/**
	 * @return path to the repository root
	 */
	@NotNull
	public Path getRootPath() {
		return this.rootPath;
	}

	/**
	 * Set the hashes associated to the repository
	 * @param hashes hashes of the files in the repository
	 */
	private void setHashes(@NotNull Map<File, byte[]> hashes) {
		this.hashes = hashes;
	}


	/**
	 * Set the previous snapshot
	 * @param previousSnapshot previous snapshot of the repository
	 */
	private void setPreviousSnapshot(@NotNull Repository previousSnapshot) {
		this.previousSnapshot = previousSnapshot;
	}

	/**
	 * @return a new repository snapshot
	 */
	@NotNull
	public Repository getSnapshot() {
		// Create next repository
		final Repository snapshot = new Repository(this.rootPath, this.messageDigest);

		// Compute hashes of files in the repository
		final Map<File, byte[]> hashes = this.getHashes(this.rootPath.toFile(), this.messageDigest);

		// Set the hashes
		snapshot.setHashes(hashes);

		// Link the next snapshot to the current one
		snapshot.setPreviousSnapshot(this);

		return snapshot;
	}


	/**
	 * @return changes with the previous repository snapshot
	 * @implNote if there is no previous snapshot, or if the previous snapshot doesn't have files' hashes, the changes list will be empty
	 */
	@NotNull
	public Map<File, RepositoryChange> getChanges() {
		return this.getChanges(this.previousSnapshot);
	}

	/**
	 * @param otherRepository another repository
	 * @return changes with the other repository
	 */
	@NotNull
	public Map<File, RepositoryChange> getChanges(@Nullable Repository otherRepository) {

		final Map<File, RepositoryChange> changes = new HashMap<>();

		if (this.hashes == null || otherRepository == null || otherRepository.hashes == null) { return changes; }

		final Map<File, byte[]> hashes = this.hashes;
		final Map<File, byte[]> previousHashes = otherRepository.hashes;

		// Merge all keys (all files in both repos)
		Set<File> filesSet = new HashSet<>(hashes.keySet());
		filesSet.addAll(previousHashes.keySet());

		for (File file : filesSet) {

			// Get relative file path
			final Path filePath = file.toPath();
			final Path relativeFilePath = this.rootPath.relativize(filePath);
			final File relativeFile = relativeFilePath.toFile();

			if (previousHashes.containsKey(file) && !hashes.containsKey(file)) {
				// Deleted
				changes.put(relativeFile, RepositoryChange.DELETE);
			}

			if (!previousHashes.containsKey(file) && hashes.containsKey(file)) {
				// Created
				changes.put(relativeFile, RepositoryChange.CREATE);
			}

			if (previousHashes.containsKey(file) && hashes.containsKey(file)) {
				// Compare
				final byte[] previousHash = previousHashes.get(file);
				final byte[] hash = hashes.get(file);

				if (!Arrays.equals(previousHash, hash)) {
					// Modified
					changes.put(relativeFile, RepositoryChange.UPDATE);
				}
			}

		}

		return changes;
	}

	/**
	 * Compute hash(es) of file(s)
	 * @param file a file or a directory
	 * @param messageDigest hashing algorithm
	 * @return the hash(es) of the file(s), computed with the algorithm
	 */
	@NotNull
	private Map<File, byte[]> getHashes(@NotNull File file, @NotNull MessageDigest messageDigest) {
		Map<File, byte[]> hashes = new HashMap<>();
		if (file.isFile()) {

			// Ensure that we can read the file
			if (!file.canRead()) {
				System.err.println("Unable to read file " + file.getAbsolutePath());
				return hashes;
			}

			try {
				// Compute hash
				final byte[] hash = getHash(file, messageDigest);
				hashes.put(file, hash);
			} catch (IOException error) {
				System.err.println("Error while compute hash of file " + file.getAbsolutePath() + ": " + error);
			}

		} else {
			// If the file is a directory, list all children
			final File[] children = file.listFiles();
			if (children == null) return hashes;

			for (File child: children) {
				if (child == null) continue;
				// Compute hash of child
				final Map<File, byte[]> subMap = getHashes(child, messageDigest);
				hashes.putAll(subMap);
			}
		}

		return hashes;
	}

	/**
	 * Compute hash of file
	 * @param file a file
	 * @param messageDigest a hashing algorithm
	 * @return the hash of file with the selected algorithm
	 * @throws IOException if some errors occur while reading the file
	 */
	private byte[] getHash(@NotNull File file, @NotNull MessageDigest messageDigest) throws IOException {
		try (InputStream inputStream = new FileInputStream(file); DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
			int readedBytes;
			do {
				readedBytes = digestInputStream.read();
			} while (readedBytes != -1);
		}

		return messageDigest.digest();
	}
}
