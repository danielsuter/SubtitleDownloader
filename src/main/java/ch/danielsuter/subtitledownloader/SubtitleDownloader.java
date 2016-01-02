package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.response.SubtitleFile;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class SubtitleDownloader {
	private OpenSubtitles server;

	public SubtitleDownloader(OpenSubtitles server) {
		this.server = server;
	}
	
	public boolean download(File movie, SubtitleInfo subtitleInfo, boolean replace) {
		System.out.println(" Downloading subtitle: " + subtitleInfo);
		
		try {
			List<SubtitleFile> subtitles = server.downloadSubtitles(subtitleInfo.getSubtitleFileId());
			
			if(subtitles.isEmpty()) {
				return false;
			}
			
			SubtitleFile downloadedSubtitle = subtitles.get(0);
			String subtitle = downloadedSubtitle.getContent().getContent();
			
			File movieDirectory = movie.getParentFile();
			File subtitleFile = new File(movieDirectory, FileUtil.getFileWithoutExtension(movie) + "." + subtitleInfo.getFormat());
			
			FileWriter writer = new FileWriter(subtitleFile);
			writer.write(subtitle);
			writer.close();
			
			return true;
		} catch (XmlRpcException | IOException e) {
			throw new RuntimeException("Failed to download subtitle", e);
		}
	}
}
