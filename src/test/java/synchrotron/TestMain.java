package synchrotron;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.junit.jupiter.api.Test;
import synchrotron.synchronizer.Synchronizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestMain {

	final String filePathString = "/Users/ugocottin/Desktop/projets/Ressources";
	final Path filePath = Paths.get(filePathString);

	@Test
	void testAnything() {
		assertDoesNotThrow(() -> {
			final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			try (InputStream inputStream = Files.newInputStream(filePath); DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
				int readedBytes;
				do {
					readedBytes = digestInputStream.read();
				} while (readedBytes != -1);
			}

			byte[] digest = messageDigest.digest();
			String digestString = HexBin.encode(digest);
			System.out.println(digestString);
		});
	}

	@Test
	void testOther() throws NoSuchAlgorithmException, IOException {
		Synchronizer synchronizer = new Synchronizer();
		final File file = this.filePath.toFile();
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		synchronizer.doSomething(file, messageDigest);
	}
}
