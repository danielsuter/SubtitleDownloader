package ch.danielsuter.subtitledownloader;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilTest {
	@Test
	public void getFileWithoutExtension() throws Exception {
		assertEquals("The.Summit.2012.HDTV.XviD.spinzes", FileUtil.getFileWithoutExtension("The.Summit.2012.HDTV.XviD.spinzes.avi"));
	}

	@Test
	public void getFileWithoutExtensionNoExtension() throws Exception {
		assertEquals("test-blub-mkv", FileUtil.getFileWithoutExtension("test-blub-mkv"));
	}
}