package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MovieFileFinder {

	private final static Set<String> MOVIE_EXTENSIONS = new LinkedHashSet<>(Arrays.asList("mkv", "avi", "mp4"));
	private final static Set<String> SUBTITLE_FORMATS = new LinkedHashSet<>(Arrays.asList("srt", "txt", "sub"));
	
	private Long filesNewerThanTimestamp;
	private boolean filterExisting = true;
	private Set<String> excludes;
	
	public MovieFileFinder() {
	}

	public MovieFileFinder(Long filesNewerThanTimestamp, boolean replaceExisting, Set<String> excludes) {
		this.filesNewerThanTimestamp = filesNewerThanTimestamp;
		this.excludes = excludes;
		this.filterExisting = !replaceExisting;
	}

	public Set<File> findAll(File basePath) {
		Set<File> result = new HashSet<>();
		addMovieFiles(basePath, result);
		return result;
	}

	private void addMovieFiles(File file, Set<File> result) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File currentFile : files) {
				if (currentFile.isDirectory()) {
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
		if (isMovie(currentFile) && isNewerThan(currentFile) && !skipBecauseOfSubtitle(currentFile) && isIncluded(currentFile)) {
			result.add(currentFile);
		}
	}

	private boolean isNewerThan(File file) {
		if (filesNewerThanTimestamp == null) {
			return true;
		}

		try {
			FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());
			long lastModifiedMillis = lastModifiedTime.toMillis();
			return lastModifiedMillis > filesNewerThanTimestamp;
		} catch (IOException e) {
			throw new RuntimeException("Failed to read last modified time", e);
		}
	}

	private boolean isMovie(File file) {
		for (String extension : MOVIE_EXTENSIONS) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean skipBecauseOfSubtitle(File movie) {
		return filterExisting && hasSubtitle(movie);
	}
	
	private boolean hasSubtitle(File movie) {
		for (String format : SUBTITLE_FORMATS) {
			String fileWithoutExtension = FileUtil.getFileWithoutExtension(movie);
			File possibleSubtitle = new File(movie.getParentFile(), fileWithoutExtension + "." + format);
			if(possibleSubtitle.exists()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isIncluded(File movie) {
		String path = movie.getAbsolutePath();
		for (String exclude : excludes) {
			if(path.contains(exclude)) {
				return false;
			}
		}
		return true;
	}
}
