package synchrotron.synchronizer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

public class Synchronizer {

	@NotNull
	private final MessageDigest messageDigest;

	@Nullable
	private SynchronizerThread backgroundThread;

	public Synchronizer(@NotNull MessageDigest messageDigest) {
		this.messageDigest = messageDigest;
	}

	public void synchronize(@NotNull Path firstPath, @NotNull Path secondPath, int timer) {

		if (this.backgroundThread != null) {
			System.err.println("An instance is already running");
			return;
		}

		final Repository firstRepo = new Repository(firstPath, this.messageDigest);
		final Repository secondRepo = new Repository(secondPath, this.messageDigest);

		this.backgroundThread = new SynchronizerThread(this, firstRepo, secondRepo, timer);
		this.backgroundThread.start();
	}

	public void stop() {
		if (this.backgroundThread == null) { return; }
		this.backgroundThread.doStop();
	}

	public void waitForExit() throws InterruptedException {
		if (this.backgroundThread == null) { return; }
		this.backgroundThread.join();
	}

	public void init(@NotNull final Repository firstRepo, @NotNull final Repository secondRepo) {
		Map<File, RepositoryChange> firstRepoChanges = firstRepo.getChanges(secondRepo);
		Map<File, RepositoryChange> secondRepoChanges = secondRepo.getChanges(firstRepo);

		Map<File, RepositoryChange> initFirstRepoChanges = new HashMap<>();
		Map<File, RepositoryChange> initSecondRepoChanges = new HashMap<>();

		for (File file : firstRepoChanges.keySet()) {
			RepositoryChange change = firstRepoChanges.get(file);
			if (change != RepositoryChange.DELETE) {
				initFirstRepoChanges.put(file, change);
			}
		}

		for (File file : secondRepoChanges.keySet()) {
			RepositoryChange change = secondRepoChanges.get(file);
			if (change != RepositoryChange.DELETE) {
				initSecondRepoChanges.put(file, change);
			}
		}

		try {
			applyChanges(initFirstRepoChanges, firstRepo, secondRepo);
			applyChanges(initSecondRepoChanges, secondRepo, firstRepo);
		} catch (IOException ioException) {
			System.err.println(ioException.getMessage());
		}
	}

	public void reconcile(@NotNull final Repository firstRepo, @NotNull final Repository secondRepo) {
		try {
			Map<File, RepositoryChange> firstRepoChanges = firstRepo.getChanges();
			Map<File, RepositoryChange> secondRepoChanges = secondRepo.getChanges();

			if (firstRepoChanges.size() > 0) applyChanges(firstRepoChanges, firstRepo, secondRepo);
			if (secondRepoChanges.size() > 0) applyChanges(secondRepoChanges, secondRepo, firstRepo);
		} catch (IOException ioException) {
			System.err.println(ioException.getMessage());
		}
	}

	private void applyChanges(@NotNull final Map<File, RepositoryChange> changes, @NotNull final Repository fromRepository, @NotNull final Repository toRepository) throws IOException {
		Set<File> files = changes.keySet();

		for (File file : files) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) { }
			RepositoryChange change = changes.get(file);

			File fromFile = this.getAbsoluteFileInRepository(file, fromRepository);
			File toFile = this.getAbsoluteFileInRepository(file, toRepository);

			boolean res;

			switch (change) {
				case CREATE:
					File parentFile = toFile.getParentFile();
					if (parentFile != null) {
						res = parentFile.mkdirs();
						if (res) {
							System.out.println("[CREATE]\t" + parentFile.getAbsolutePath() + " created");
						}
					}

					if (fromFile.isDirectory()) {
						toFile.mkdir();
						continue;
					}

					res = toFile.createNewFile();
					if (res) {
						System.out.println("[CREATE]\t" + toFile.getAbsolutePath() + " created");
						System.out.println("[COPY]  \t" + fromFile.getAbsolutePath() + " -> " + toFile.getAbsolutePath());
						try (FileInputStream inputStream = new FileInputStream(fromFile); FileOutputStream outputStream = new FileOutputStream(toFile)) {
							FileChannel inputChannel = inputStream.getChannel();
							FileChannel outputChannel = outputStream.getChannel();

							inputChannel.transferTo(0, inputChannel.size(), outputChannel);
						}
					}
					break;
				case UPDATE:
					System.out.println("[UPDATE]\t" + fromFile.getAbsolutePath());
					try (FileInputStream inputStream = new FileInputStream(fromFile); FileOutputStream outputStream = new FileOutputStream(toFile)) {
						FileChannel inputChannel = inputStream.getChannel();
						FileChannel outputChannel = outputStream.getChannel();

						inputChannel.transferTo(0, inputChannel.size(), outputChannel);
					}
					break;
				case DELETE:

					if (toFile.isDirectory()) {
						this.deleteDirectory(toFile);
						continue;
					}

					if (toFile.exists()) {
						res = toFile.delete();
						if (res) System.out.println("[DELETE]\t" + fromFile.getAbsolutePath() + " deleted");
					}

					break;
			}
		}
	}

	private @NotNull File getAbsoluteFileInRepository(@NotNull File file, @NotNull Repository repository) {
		Path relativePath = file.toPath();
		Path absolutePath = repository.getRootPath().resolve(relativePath);
		return absolutePath.toFile();
	}

	private void deleteDirectory(@NotNull File directory) {
		if (!directory.exists()) {
			return;
		}

		if (directory.isDirectory()) {
			@Nullable File[] children = directory.listFiles();

			if (children == null) { return; }

			for (File child : children) {
				if (child != null) deleteDirectory(child);
			}
		}

		directory.delete();
	}
}