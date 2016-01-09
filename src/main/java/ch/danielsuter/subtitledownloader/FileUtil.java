package ch.danielsuter.subtitledownloader;

import java.io.File;

public class FileUtil {
	public static String getFileWithoutExtension(File file) {
		return getFileWithoutExtension(file.getName());
	}
	
	public static String getFileWithoutExtension(String fileName) {
		int lastDot = fileName.lastIndexOf(".");
		return fileName.substring(0, lastDot);
	}
}
