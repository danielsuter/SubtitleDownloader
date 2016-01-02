package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class OSDownloader {
	
	private static final String USER_AGENT = "OSTestUserAgent";
	private static final String SERVER_URL = "http://api.opensubtitles.org/xml-rpc";
	private static final String SEARCH_LANGUAGE = "eng";

	private final OpenSubtitles server;
	
	private MovieFileFinder fileFinder = new MovieFileFinder();
	private SubtitleDownloader downloader;
	
	public static OSDownloader connect() throws MalformedURLException, XmlRpcException {
		return new OSDownloader(login());
	}
	
	public static void main(String[] args) throws Exception {
//		OSDownloader.connect().download(new File("\\\\DiskStation\\video\\TV show\\The Man In The High Castle Season 1 Mp4 1080p"), true);
		OSDownloader.connect().download(new File("\\\\DiskStation\\video\\TV show\\South Park\\South Park Season 8"), true);
	}
	
	
	private OSDownloader(OpenSubtitles server) {
		this.server = server;
		this.downloader = new SubtitleDownloader(server);
	}
	
	public void download(File baseDirectory, boolean replace) {
		Set<File> movieFiles = fileFinder.findAll(baseDirectory);
		System.out.println(String.format("Found %d movies", movieFiles.size()));
		for (File movieFile : movieFiles) {
			System.out.println(String.format("Processing %s ...", movieFile.getName()));
			List<SubtitleInfo> subtitles = searchByFile(movieFile);
			if(subtitles.isEmpty()) {
				System.out.println(" Could not find any subtitles by file hash. Using fulltext search...");
				subtitles = searchByFullName(movieFile);
			}
			
			if(subtitles.isEmpty()) {
				System.out.println(" Could not find any subtitles for: " + movieFile.getName());
			} else {
				int subtitleIndex = 0;
				boolean successfullyDownloaded = downloader.download(movieFile, subtitles.get(subtitleIndex++), replace);
				while(subtitleIndex < subtitles.size() && !successfullyDownloaded) {
					System.out.println(" Download failed - trying another subtitle");
					successfullyDownloaded = downloader.download(movieFile, subtitles.get(subtitleIndex++), replace);
				}
			}
		}
	}
	
	private List<SubtitleInfo> searchByFile(File movie) {
		try {
			return server.searchSubtitles(SEARCH_LANGUAGE, movie);
		} catch (IOException | XmlRpcException e) {
			throw new RuntimeException("Search failed", e);
		}
	}
	
	private List<SubtitleInfo> searchByFullName(File movie) {
		String movieName = FileUtil.getFileWithoutExtension(movie); 
		try {
			return server.searchSubtitles(SEARCH_LANGUAGE, movieName, null, null);
		} catch (XmlRpcException e) {
			throw new RuntimeException("Search failed", e);
		}
	}

	private static OpenSubtitles login() throws MalformedURLException, XmlRpcException {
		URL serverUrl = new URL(SERVER_URL);
		OpenSubtitlesImpl server = new OpenSubtitlesImpl(serverUrl);
		server.login("en", USER_AGENT);
		return server;
	}
}
