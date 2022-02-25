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

	@NotNull
	private final Path rootPath;

	@Nullable
	private Map<File, byte[]> hashes;

	@NotNull
	private final MessageDigest messageDigest;

	@Nullable
	private Repository previousSnapshot;

	public Repository(@NotNull Path rootPath, @NotNull MessageDigest messageDigest) {
		this.rootPath = rootPath;
		this.hashes = null;
		this.messageDigest = messageDigest;
		this.previousSnapshot = null;
	}

	@NotNull
	public Path getRootPath() {
		return this.rootPath;
	}

	private void setHashes(@NotNull Map<File, byte[]> hashes) {
		this.hashes = hashes;
	}

	private void setPreviousSnapshot(@NotNull Repository previousSnapshot) {
		this.previousSnapshot = previousSnapshot;
	}

	@NotNull
	public Repository getSnapshot() {
		final Repository snapshot = new Repository(this.rootPath, this.messageDigest);
		final Map<File, byte[]> hashes = this.getHashes(this.rootPath.toFile(), this.messageDigest);
		snapshot.setHashes(hashes);
		snapshot.setPreviousSnapshot(this);
		return snapshot;
	}

	@NotNull
	public Map<File, RepositoryChange> getChanges() {
		return this.getChanges(this.previousSnapshot);
	}

	@NotNull
	public Map<File, RepositoryChange> getChanges(@Nullable Repository otherRepository) {

		final Map<File, RepositoryChange> changes = new HashMap<>();

		if (this.hashes == null || otherRepository == null || otherRepository.hashes == null) { return changes; }

		final Map<File, byte[]> hashes = this.hashes;
		final Map<File, byte[]> previousHashes = otherRepository.hashes;

		Set<File> filesSet = new HashSet<>(hashes.keySet());
		filesSet.addAll(previousHashes.keySet());

		for (File file : filesSet) {

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

	@NotNull
	private Map<File, byte[]> getHashes(@NotNull File file, @NotNull MessageDigest messageDigest) {
		Map<File, byte[]> hashes = new HashMap<>();
		if (file.isFile()) {

			if (!file.canRead()) {
				System.err.println("Unable to read file " + file.getAbsolutePath());
				return hashes;
			}

			try {
				final byte[] hash = getHash(file, messageDigest);
				hashes.put(file, hash);
			} catch (IOException error) {
				System.err.println("Error while compute hash of file " + file.getAbsolutePath() + ": " + error);
			}

		} else {
			final File[] children = file.listFiles();
			if (children == null) return hashes;

			for (File child: children) {
				if (child == null) continue;
				final Map<File, byte[]> subMap = getHashes(child, messageDigest);
				hashes.putAll(subMap);
			}
		}

		return hashes;
	}

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
