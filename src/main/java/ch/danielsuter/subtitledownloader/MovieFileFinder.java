package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MovieFileFinder {
	
	private final static Set<String> MOVIE_EXTENSIONS = new LinkedHashSet<>(Arrays.asList("mkv", "avi", "mp4"));
	
	public MovieFileFinder() {
	}
	
	public Set<File> findAll(File basePath) {
		Set<File> result = new HashSet<>();
		addMovieFiles(basePath, result);
		return result;
	}

	private void addMovieFiles(File file, Set<File> result) {
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for (File currentFile : files) {
				if(currentFile.isDirectory()) {
					addMovieFiles(currentFile, result);
				} else {
					handleFile(currentFile, result);
				}
			}
		} else {
			handleFile(file, result);
		}
	}

	private void handleFile(File currentFile, Set<File> result) {
		if(isMovie(currentFile)) {
			result.add(currentFile);
		}
	}

	private boolean isMovie(File file) {
		for (String extension : MOVIE_EXTENSIONS) {
			if(file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
