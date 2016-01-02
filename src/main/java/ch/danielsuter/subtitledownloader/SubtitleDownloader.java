package ch.danielsuter.subtitledownloader;

import java.io.File;

import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class SubtitleDownloader {
	public SubtitleDownloader() {
	}
	
	public void download(File movie, SubtitleInfo subtitleInfo, boolean replace) {
		System.out.println("Downloading subtitle: " + subtitleInfo.getDownloadLink());
	}
}
