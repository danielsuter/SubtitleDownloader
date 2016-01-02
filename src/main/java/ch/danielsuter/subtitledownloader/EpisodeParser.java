package ch.danielsuter.subtitledownloader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpisodeParser {
	public static void main(String[] args) {
		System.out.println(parseEpisode("The Man In The High Castle S01E01"));
	}
	
	public static Episode parseEpisode(String fileName) {
		Pattern pattern = Pattern.compile("(.*)S(\\d+)E(\\d+).*");
		Matcher matcher = pattern.matcher(fileName);
		if(matcher.matches()) {
			String name = matcher.group(1);
			String cleanedName = name.replaceAll("\\.", " ").trim();
			String season = matcher.group(2);
			String episode = matcher.group(3);
			return new Episode(cleanedName, season, episode);
		}
		return null;
	}
}
