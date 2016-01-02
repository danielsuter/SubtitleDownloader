package ch.danielsuter.subtitledownloader;

import java.io.File;

public class FileUtil {
	public static String getFileWithoutExtension(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf(".");
		return name.substring(0, lastDot);
	}
}
