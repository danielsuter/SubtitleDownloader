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
	private SubtitleDownloader downloader = new SubtitleDownloader();
	
	public static OSDownloader connect() throws MalformedURLException, XmlRpcException {
		return new OSDownloader(login());
	}
	
	public static void main(String[] args) throws Exception {
		OSDownloader.connect().download(new File("\\\\DiskStation\\video\\TV show\\Test"), true);
	}
	
	
	private OSDownloader(OpenSubtitles server) {
		this.server = server;
	}
	
	public void download(File baseDirectory, boolean replace) {
		Set<File> movieFiles = fileFinder.findAll(baseDirectory);
		for (File movieFile : movieFiles) {
			List<SubtitleInfo> subtitles = searchByFile(movieFile);
			if(subtitles.isEmpty()) {
				System.out.println("Could not find any subtitles by file hash. Using fulltext search...");
				subtitles = searchByFullName(movieFile);
			}
			
			if(subtitles.isEmpty()) {
				System.out.println("Could not find any subtitles for: " + movieFile.getName());
			} else {
				System.out.println("Choosing first match");
				downloader.download(movieFile, subtitles.get(0), replace);
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
		String movieName = getFileWithoutExtension(movie); 
		try {
			return server.searchSubtitles(SEARCH_LANGUAGE, movieName, null, null);
		} catch (XmlRpcException e) {
			throw new RuntimeException("Search failed", e);
		}
	}

	private static String getFileWithoutExtension(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf(".");
		return name.substring(0, lastDot);
	}
	
	private static OpenSubtitles login() throws MalformedURLException, XmlRpcException {
		URL serverUrl = new URL(SERVER_URL);
		OpenSubtitlesImpl server = new OpenSubtitlesImpl(serverUrl);
		
		server.login("en", USER_AGENT);
		return server;
	}
}
