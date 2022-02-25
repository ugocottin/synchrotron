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

	public void synchronize(@NotNull Path firstPath, @NotNull Path secondPath) {

		if (this.backgroundThread != null) {
			System.err.println("An instance is already running");
			return;
		}

		final Repository firstRepo = new Repository(firstPath, this.messageDigest);
		final Repository secondRepo = new Repository(secondPath, this.messageDigest);

		this.backgroundThread = new SynchronizerThread(this, firstRepo, secondRepo);
		this.backgroundThread.start();
	}

	public void stop() {
		if (this.backgroundThread == null) { return; }
		this.backgroundThread.doStop();
	}

	public void waitAndExit() throws InterruptedException {
		if (this.backgroundThread == null) { return; }
		this.backgroundThread.join();
	}

	public void reconcile(@NotNull final Repository firstRepo, @NotNull final Repository secondRepo) {
		try {
			applyChanges(firstRepo.getChanges(), firstRepo, secondRepo);
			applyChanges(secondRepo.getChanges(), secondRepo, firstRepo);
		} catch (IOException ioException) {
			System.err.println(ioException.getMessage());
		}
	}

	private void applyChanges(@NotNull final Map<File, RepositoryChange> changes, @NotNull final Repository fromRepository, @NotNull final Repository toRepository) throws IOException {
		Set<File> files = changes.keySet();

		for (File file : files) {
			RepositoryChange change = changes.get(file);

			File fromFile = this.getAbsoluteFileInRepository(file, fromRepository);
			File toFile = this.getAbsoluteFileInRepository(file, toRepository);

			boolean res;

			switch (change) {
				case CREATE:
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
					break;
				case DELETE:
					res = toFile.delete();
					if (res) System.out.println("[DELETE]\t" + fromFile.getAbsolutePath() + " deleted");
					break;
			}
		}
	}

	private @NotNull File getAbsoluteFileInRepository(@NotNull File file, @NotNull Repository repository) {
		Path relativePath = file.toPath();
		Path absolutePath = repository.getRootPath().resolve(relativePath);
		return absolutePath.toFile();
	}
}