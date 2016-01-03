package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.util.Set;

public class Settings {
	private String path;
	private boolean replaceExisting;
	private Long timestamp;
	private String user;
	private String password;
	private Set<String> excludes;

	public Settings(Set<String> excludes, String path, boolean replaceExisting, Long timestamp, String user, String password) {
		this.excludes = excludes;
		this.path = path;
		this.replaceExisting = replaceExisting;
		this.timestamp = timestamp;
		this.user = user;
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public File getPath() {
		return new File(path);
	}

	public boolean isReplaceExisting() {
		return replaceExisting;
	}

	public Long getTimestamp() {
		return timestamp;
	}
	
	public Set<String> getExcludes() {
		return excludes;
	}
}
