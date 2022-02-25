package synchrotron.synchronizer;

import synchrotron.fs.FileSystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;

public class Synchronizer {

	@Nullable
	private Map<File, byte[]> hashes;

	@NotNull
	private final MessageDigest messageDigest;

	public Synchronizer(@NotNull MessageDigest messageDigest) {
		this.hashes = null;
		this.messageDigest = messageDigest;
	}

	public void synchronize(@NotNull Path rootPath1, @NotNull Path rootPath2) {
		Map<File, byte[]> firstDirHashes = this.computeHashes(rootPath1.toFile(), this.messageDigest);
		Map<File, byte[]> secondDirHashes = this.computeHashes(rootPath2.toFile(), this.messageDigest);
	}

	private void reconcile(@NotNull Map<File, byte[]> firstDirHashes, @NotNull Map<File, byte[]> secondDirHashes) {

	}

	public @NotNull List<File> computeDirty(@NotNull Path rootPath) {
		final File rootFile = rootPath.toFile();
		final Map<File, byte[]> computedHashes = this.computeHashes(rootFile, this.messageDigest);
		final List<File> dirtyFiles = new ArrayList<>();

		if (this.hashes != null) {
			for (Map.Entry<File, byte[]> entry : computedHashes.entrySet()) {
				final File file = entry.getKey();
				if (this.hashes.containsKey(file)) {
					final byte[] currentHash = entry.getValue();
					final byte[] previousHash = this.hashes.get(file);

					if (!Arrays.equals(currentHash, previousHash)) {
						// DIRTY!
						dirtyFiles.add(file);
					}
				}
			}
		}

		this.hashes = computedHashes;
		return dirtyFiles;
	}

	public static void printHashes(@NotNull Map<File, byte[]> hashes) {
		System.out.println("Showing files hashes for " + hashes.size() + " entries:");
		for (Map.Entry<File, byte[]> entry : hashes.entrySet()) {
			final File file = entry.getKey();
			final byte[] hash = entry.getValue();
			System.out.println(file.toString() + ": " + DatatypeConverter.printHexBinary(hash));
		}
	}

	private @NotNull Map<File, byte[]> computeHashes(@NotNull File file, @NotNull MessageDigest messageDigest) {
		Map<File, byte[]> hashes = new HashMap<>();
		if (file.isFile()) {
			try {
				final byte[] hash = getHash(file, messageDigest);
				hashes.put(file, hash);
			} catch (IOException error) {
				System.err.println("Error while compute hash of file " + file.getAbsolutePath() + ": " + error);
			}

		} else {
			@Nullable final File[] children = file.listFiles();
			if (children == null) return hashes;

			for (File child: children) {
				if (child == null) continue;
				final Map<File, byte[]> subMap = computeHashes(child, messageDigest);
				hashes.putAll(subMap);
			}
		}

		return hashes;
	}

	private byte[] getHash(@NotNull File file, @NotNull MessageDigest messageDigest) throws IOException {
		System.out.println("Get hash of file " + file);

		try (InputStream inputStream = new FileInputStream(file); DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
			int readedBytes;
			do {
				readedBytes = digestInputStream.read();
			} while (readedBytes != -1);
		}

		return messageDigest.digest();
	}
}