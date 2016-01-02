package ch.danielsuter.subtitledownloader;

public class Episode {
	private String season;
	private String episode;
	private String name;
	
	public String getSeason() {
		return season;
	}
	public String getEpisode() {
		return episode;
	}
	
	public String getName() {
		return name;
	}
	
	public Episode(String name, String season, String episode) {
		this.name = name;
		this.season = season;
		this.episode = episode;
	}
	
	@Override
	public String toString() {
		return "Episode [season=" + season + ", episode=" + episode + ", name=" + name + "]";
	}
	
}
