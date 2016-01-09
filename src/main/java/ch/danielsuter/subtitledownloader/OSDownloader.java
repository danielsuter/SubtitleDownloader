package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class OSDownloader {
	private final static Logger logger = LoggerFactory.getLogger(OSDownloader.class);
	
	private static final String USER_AGENT = "OSTestUserAgent";
	private static final String SERVER_URL = "http://api.opensubtitles.org/xml-rpc";
	private static final String SEARCH_LANGUAGE = "eng";

	private final OpenSubtitles server;
	
	private SubtitleDownloader downloader;
	
	public static OSDownloader connect(String user, String password) throws MalformedURLException, XmlRpcException {
		return new OSDownloader(login(user, password));
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			printUsage();
		} else {
			Settings settings = SettingsFactory.parse(new File(args[0]));
			OSDownloader osDownloader = OSDownloader.connect(settings.getUser(), settings.getPassword());
			osDownloader.download(settings.getPath(), settings.isReplaceExisting(), settings.getTimestamp(), settings.getExcludes());
		}
	}
	
	private static void printUsage() {
		System.out.println("Provide path to settings.properties");
	}

	private OSDownloader(OpenSubtitles server) {
		this.server = server;
		this.downloader = new SubtitleDownloader(server);
	}
	
	public void download(File baseDirectory, boolean replace, Long timestamp, Set<String> excludes) {
		MovieFileFinder fileFinder = new MovieFileFinder(timestamp, replace, excludes);
		
		Set<File> movieFiles = fileFinder.findAll(baseDirectory);
		logger.info("Found {} movies", movieFiles.size());
		for (File movieFile : movieFiles) {
			logger.debug("Processing {} ...", movieFile.getName());
			List<SubtitleInfo> subtitles = searchByFile(movieFile);
			if(subtitles.isEmpty()) {
				logger.debug(" Could not find any subtitles by file hash. Using fulltext search...");
				subtitles = searchByFullName(movieFile);
			}
			
			if(subtitles.isEmpty()) {
				logger.info(" Could not find any subtitles for: {}", movieFile.getName());
			} else {
				sortByRelevance(movieFile, subtitles);
				
				int subtitleIndex = 0;
				boolean successfullyDownloaded = downloader.download(movieFile, subtitles.get(subtitleIndex++), replace);
				while(subtitleIndex < subtitles.size() && !successfullyDownloaded) {
					logger.warn(" Download failed - trying another subtitle");
					successfullyDownloaded = downloader.download(movieFile, subtitles.get(subtitleIndex++), replace);
				}
			}
		}
	}
	
	private void sortByRelevance(File movie, List<SubtitleInfo> subtitles) {
		Collections.sort(subtitles, new Comparator<SubtitleInfo>() {

			@Override
			public int compare(SubtitleInfo subtitleInfo1, SubtitleInfo subtitleInfo2) {
				String movieName = FileUtil.getFileWithoutExtension(movie);
				String filename1 = FileUtil.getFileWithoutExtension(subtitleInfo1.getFileName());
				String filename2 = FileUtil.getFileWithoutExtension(subtitleInfo2.getFileName());
				
				int distance1 = StringUtils.getLevenshteinDistance(movieName, filename1);
				int distance2 = StringUtils.getLevenshteinDistance(movieName, filename2);
				
				return Integer.compare(distance1, distance2);
			}
		});
		
		logger.debug("Relevance for {} is now", movie.getName());
		for (SubtitleInfo subtitleInfo : subtitles) {
			logger.debug(" {}", subtitleInfo.getFileName());
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

	private static OpenSubtitles login(String user, String password) throws MalformedURLException, XmlRpcException {
		URL serverUrl = new URL(SERVER_URL);
		OpenSubtitlesImpl server = new OpenSubtitlesImpl(serverUrl);
		server.login(user, password, "en", USER_AGENT);
		return server;
	}
}
