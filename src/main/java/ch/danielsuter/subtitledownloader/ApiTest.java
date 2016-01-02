package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.ServerInfo;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class ApiTest {
	private static final String SEARCH_LANGUAGE = "eng";
	private static final String HIGTH_CASTLE_1_1 = "\\\\DiskStation\\video\\TV show\\The Man In The High Castle Season 1 Mp4 1080p\\The Man In The High Castle S01E01.mp4";
	private static final String BBT_9_10 = "\\\\DiskStation\\video\\TV show\\The.Big.Bang.Theory.S09E10.1080p.HDTV.X264-DIMENSION[rarbg]\\The.Big.Bang.Theory.S09E10.1080p.HDTV.X264-DIMENSION.mkv";
	private static final String SP_12_09 = "\\\\DiskStation\\video\\TV show\\South Park\\South Park Season 12\\S12E09.Breast Cancer Show Ever\\South Park.S12E09.Breast Cancer Show Ever.avi";
	private static final String DB_123 = "\\\\DiskStation\\video\\TV show\\01 Dragon Ball (Uncut)\\01 Episodes\\09 Piccolo Jr Saga\\123 Lost And Found.mp4";
	private static final String TRAINING_DAY = "\\\\DiskStation\\video\\Filme\\Training Day (2001) [1080p]\\Training Day 2001 BrRip 1080p x264 YIFY.mp4";
	
	private static OpenSubtitlesImpl server;

	public static void main(String[] args) throws XmlRpcException, IOException {
		server = login();
		
		performSearches(TRAINING_DAY);
		
	}

	public static void firstTest(OpenSubtitlesImpl server) throws XmlRpcException, IOException {
		List<SubtitleInfo> subtitles = server.searchSubtitles(SEARCH_LANGUAGE, "The Man In The High Castle", "1", "1");
		System.out.println("Name and episode search: " + subtitles.size());
		
		subtitles = server.searchSubtitles(SEARCH_LANGUAGE, "The Man In The High Castle S01E01", null, null);
		System.out.println("Full text search: " + subtitles.size());
		
		File episodeOne = new File(HIGTH_CASTLE_1_1);
		System.out.println("File exists: " + episodeOne.exists());
		subtitles = server.searchSubtitles(SEARCH_LANGUAGE, episodeOne);
		System.out.println("File search: " + subtitles.size());
	}

	private static OpenSubtitlesImpl login() throws MalformedURLException, XmlRpcException {
		URL serverUrl = new URL("http://api.opensubtitles.org/xml-rpc");
		OpenSubtitlesImpl server = new OpenSubtitlesImpl(serverUrl);
		
		server.login("en", "OSTestUserAgent");
		return server;
	}
	
	public static void performSearches(String fileLocation) throws XmlRpcException, IOException {
		File movie = new File(fileLocation);
		if(!movie.exists()) {
			throw new RuntimeException("Movie does not exist: " + fileLocation);
		}
		
		searchByFullName(movie);
		searchByNameAndEpisode(movie);
		searchByFile(movie);
	}

	private static void searchByFile(File movie) throws IOException, XmlRpcException {
		List<SubtitleInfo> subtitles = server.searchSubtitles(SEARCH_LANGUAGE, movie);
		System.out.println("File search: " + subtitles.size());
	}

	private static void searchByNameAndEpisode(File movie) throws XmlRpcException {
		Episode episode = EpisodeParser.parseEpisode(getFileWithoutExtension(movie));
		if(episode != null) {
			List<SubtitleInfo> subtitles = server.searchSubtitles(SEARCH_LANGUAGE, episode.getName(), episode.getSeason(), episode.getEpisode());
			System.out.println("Name and episode search: " + subtitles.size());
		} else {
			System.out.println("could not parse episode: " + getFileWithoutExtension(movie));
		}
	}

	private static void searchByFullName(File movie) throws XmlRpcException {
		String movieName = getFileWithoutExtension(movie); 
		List<SubtitleInfo> subtitles = server.searchSubtitles(SEARCH_LANGUAGE, movieName, null, null);
		System.out.println("Full name search: " + subtitles.size());
	}

	private static String getFileWithoutExtension(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf(".");
		return name.substring(0, lastDot);
	}
}
