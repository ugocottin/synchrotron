package synchrotron.fs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import synchrotron.fs.FileSystem;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalFileSystem {

	FileSystem fileSystem;
	final String rootPath = "/home/john/downloads";

	@BeforeEach
	void setup() {
		fileSystem = new LocalFileSystem(this.rootPath);
	}

	@AfterEach
	void tearDown() {
		fileSystem = null;
	}

	@Test
	void testGetRoot() {
		assertEquals(fileSystem.getRoot(), this.rootPath);
	}
}
