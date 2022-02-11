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

	@Test
	void testGetParentRelative() {
		assertEquals(this.fileSystem.getParent("file.txt"), this.rootPath);
		assertEquals(this.fileSystem.getParent("/"), this.rootPath);
		assertEquals(this.fileSystem.getParent("/.hiddenFile"), this.rootPath);
		assertEquals(this.fileSystem.getParent(""), this.rootPath);
	}

	@Test
	void testGetParentAbsolute() {
		assertEquals(this.fileSystem.getParent(this.rootPath + "/file.txt"), this.rootPath);
		assertEquals(this.fileSystem.getParent(this.rootPath + "/"), "/home/john");
		assertEquals(this.fileSystem.getParent(this.rootPath + "/.hiddenFile"), this.rootPath);
		assertEquals(this.fileSystem.getParent(this.rootPath + ""), "/home/john");
	}
}
