package synchrotron.fs;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
	void testAnything() {
		String absolutePathString = "/somewhere/something/that.txt";
		Path absolutePath = Paths.get(absolutePathString);
		assertTrue(absolutePath.isAbsolute());

		String relativePathString = "somewhere/something/that.txt";
		Path relativePath = Paths.get(relativePathString);
		assertFalse(relativePath.isAbsolute());
	}

	@Test
	void testGetRoot() {
		assertEquals(fileSystem.getRoot(), this.rootPath);
	}

	@Test
	void testGetParentRelative() {
		assertEquals(this.fileSystem.getParent("file.txt"), this.rootPath);
		assertEquals(this.fileSystem.getParent(Paths.get(".ssh", "id_rsa.pub").toString()), Paths.get(this.rootPath, ".ssh").toString());
		assertNull(this.fileSystem.getParent(""));
	}

	@Test
	void testGetParentAbsolute() {
		assertEquals(this.fileSystem.getParent(this.rootPath + "/file.txt"), this.rootPath);
		assertNull(this.fileSystem.getParent(Paths.get(this.rootPath, File.separator).toString()));
		assertEquals(this.fileSystem.getParent(this.rootPath + "/.hiddenFile"), this.rootPath);
	}

	@Test
	void testGetAncestors() {
		List<String> list = new ArrayList<>();
		list.add("Users");
		list.add("ugocottin");
		list.add("Desktop");

		System.out.println(list.stream().map(str -> "/" + str).collect(Collectors.joining()));
		assertEquals(list, this.fileSystem.getAncestors("/Users/ugocottin/Desktop"));
	}

	@Test
	void testGetChildren() {
		System.out.println(this.fileSystem.getChildren("/Users/ugocottin/Desktop"));
	}
}
